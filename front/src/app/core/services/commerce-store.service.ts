import { Injectable, computed, inject, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import {
  ArticleService,
  CartService,
  CommandService,
  CustomerService,
  InvoiceService,
  OrderLineService,
  QuoteService,
} from '../api';
import { Article, Cart, Command, Customer, Invoice, OrderLine, Quote } from '../models/api.models';
import {
  DocumentStatus,
  FUNNEL_STAGES,
  PIPELINE_STAGES,
  statusMeta,
} from '../models/document-status';
import {
  ProductFamily,
  ValuedDocument,
  buildReference,
  round2,
  sumMargin,
  sumTtc,
  valueCart,
  valueCommand,
  valueInvoice,
  valueQuote,
} from '../models/document-math';
import { SupplyGroup, SupplyLine, computeSupplyNeeds, groupBySupplier } from '../models/supply';

/**
 * Magasin de données commerciales, partagé par le tableau de bord, le pipeline
 * et l'écran des relances.
 *
 * Depuis le 4 août 2026, un document commercial n'est plus une seule ligne de
 * `Cart` dont on relit le `crtStatus` : c'est l'une de quatre entités
 * (`Cart`, `Quote`, `Command`, `Invoice`), chacune avec son propre id et son
 * propre statut. `load()` récupère les quatre collections, écarte les entités
 * déjà « remplacées » par la suivante (un panier qui a un devis n'est plus un
 * panier actif) et compose un seul tableau `ValuedDocument[]` — voir
 * `document-math.ts` pour le détail par entité.
 *
 * Une limite vient encore d'un DTO backend, pas d'un choix du front :
 * `InvoiceDTO` n'expose aucun lien vers sa `Command` d'origine : il est
 * impossible de savoir quelle commande a été facturée, donc impossible
 * d'écarter une commande déjà facturée de la colonne « Commande ». Une
 * commande facturée continuera donc d'apparaître dans les deux colonnes
 * jusqu'à ce que `InvoiceDTO` porte `commandId`.
 *
 * (`CommandDTO` portait la même limite pour le client — corrigé le
 * 4 août 2026 par l'ajout de `quoteId`, qui permet désormais de remonter
 * jusqu'au panier et au client par id plutôt que par `quoteNumber`.)
 *
 * Le rafraîchissement reste explicite : `load()` au montage, `reload()` après
 * une écriture. Les transitions de pipeline (`transitionToQuote`, etc.) ne
 * font plus de mise à jour optimiste locale : l'identité même du document
 * change (un `Quote` n'est pas un `Cart` renommé), donc l'issue de l'appel
 * réseau est attendue avant de recharger.
 */
@Injectable({ providedIn: 'root' })
export class CommerceStore {
  private readonly cartApi = inject(CartService);
  private readonly lineApi = inject(OrderLineService);
  private readonly quoteApi = inject(QuoteService);
  private readonly commandApi = inject(CommandService);
  private readonly invoiceApi = inject(InvoiceService);
  private readonly articleApi = inject(ArticleService);
  private readonly customerApi = inject(CustomerService);

  /** Seuil de marge en deçà duquel une ligne est signalée. */
  readonly marginTarget = signal(0.25);

  /**
   * Inclure les devis dans les besoins d'approvisionnement.
   * Désactivé par défaut : on ne commande pas sur un devis non signé, mais
   * l'anticipation reste utile pour les délais longs.
   */
  readonly forecastSupply = signal(false);

  readonly loading = signal(false);
  readonly loaded = signal(false);
  readonly offline = signal(false);

  readonly articles = signal<Article[]>([]);
  readonly customers = signal<Customer[]>([]);
  readonly documents = signal<ValuedDocument[]>([]);

  /** Collections brutes, conservées pour construire les références et les transitions. */
  private readonly cartsRaw = signal<Cart[]>([]);
  private readonly quotesRaw = signal<Quote[]>([]);
  private readonly commandsRaw = signal<Command[]>([]);
  private readonly invoicesRaw = signal<Invoice[]>([]);

  /** Instant de référence des calculs d'ancienneté, figé à chaque chargement. */
  readonly now = signal<Date>(new Date(0));

  readonly catalog = computed(() => new Map(this.articles().map((a) => [a.artId, a])));

  /* ---------------- Sélecteurs ---------------- */

  readonly byStatus = computed(() => {
    const map = new Map<DocumentStatus, ValuedDocument[]>();
    for (const doc of this.documents()) {
      const list = map.get(doc.status);
      if (list) list.push(doc);
      else map.set(doc.status, [doc]);
    }
    return map;
  });

  /** Chiffre d'affaires facturé : factures émises et réglées. */
  readonly revenue = computed(() =>
    sumTtc(this.documents().filter((d) => statusMeta(d.status).isRevenue)),
  );

  readonly revenueMargin = computed(() =>
    sumMargin(this.documents().filter((d) => statusMeta(d.status).isRevenue)),
  );

  /** Valeur du pipeline : tout ce qui n'est ni facturé ni annulé. */
  readonly pipelineValue = computed(() =>
    sumTtc(
      this.documents().filter((d) => {
        const meta = statusMeta(d.status);
        return meta.stage !== null && !meta.isRevenue;
      }),
    ),
  );

  /** Commandes fermes en attente de facturation. */
  readonly committedValue = computed(() => sumTtc(this.of('COMMANDE')));

  /** Encours client : factures émises non encore réglées. */
  readonly outstanding = computed(() => sumTtc(this.of('FACTURE')));

  readonly marginRate = computed(() => {
    const docs = this.documents().filter((d) => statusMeta(d.status).isRevenue);
    const covered = round2(
      docs.reduce((s, d) => s + d.totals.totalHt * d.totals.costCoverage, 0),
    );
    return covered > 0 ? sumMargin(docs) / covered : null;
  });

  /** Part du CA reposant sur un coût d'achat connu. */
  readonly costCoverage = computed(() => {
    const docs = this.documents().filter((d) => statusMeta(d.status).isRevenue);
    const total = docs.reduce((s, d) => s + d.totals.totalHt, 0);
    if (total === 0) return null;
    const covered = docs.reduce((s, d) => s + d.totals.totalHt * d.totals.costCoverage, 0);
    return covered / total;
  });

  /** Entonnoir commercial, du devis au règlement. */
  readonly funnel = computed(() =>
    FUNNEL_STAGES.map((status) => {
      // Un document facturé a nécessairement été devisé : l'entonnoir est
      // cumulatif, chaque étape compte tout ce qui l'a atteinte ou dépassée.
      const minStage = statusMeta(status).stage ?? 0;
      const reached = this.documents().filter((d) => {
        const meta = statusMeta(d.status);
        return meta.stage !== null && meta.stage >= minStage;
      });
      return {
        key: status,
        label: statusMeta(status).label,
        value: sumTtc(reached),
        count: reached.length,
      };
    }),
  );

  /** Répartition mensuelle du CA facturé par famille, sur 12 mois glissants. */
  readonly monthlyByFamily = computed(() => {
    const ref = this.now();
    const months: {
      label: string;
      fullLabel: string;
      segments: { key: ProductFamily; label: string; value: number }[];
    }[] = [];

    for (let offset = 11; offset >= 0; offset--) {
      const d = new Date(ref.getFullYear(), ref.getMonth() - offset, 1);
      const totals: Record<ProductFamily, number> = { clim: 0, chauffage: 0, autre: 0 };

      for (const doc of this.documents()) {
        if (!statusMeta(doc.status).isRevenue) continue;
        if (!doc.date) continue;
        if (doc.date.getFullYear() !== d.getFullYear() || doc.date.getMonth() !== d.getMonth())
          continue;

        totals.clim += doc.totals.byFamily.clim;
        totals.chauffage += doc.totals.byFamily.chauffage;
        totals.autre += doc.totals.byFamily.autre;
      }

      months.push({
        label: d.toLocaleDateString('fr-FR', { month: 'short' }).replace('.', ''),
        fullLabel: d.toLocaleDateString('fr-FR', { month: 'long', year: 'numeric' }),
        segments: [
          { key: 'clim', label: 'Climatisation', value: round2(totals.clim) },
          { key: 'chauffage', label: 'Chauffage', value: round2(totals.chauffage) },
          { key: 'autre', label: 'Autre', value: round2(totals.autre) },
        ],
      });
    }

    return months;
  });

  /** Série des 12 derniers mois de CA, pour les sparklines. */
  readonly monthlyRevenue = computed(() =>
    this.monthlyByFamily().map((m) => m.segments.reduce((s, seg) => s + seg.value, 0)),
  );

  /* ---------------- Files de travail ---------------- */

  /** Devis sans réponse depuis plus de 7 jours, du plus gros au plus petit. */
  readonly staleQuotes = computed(() =>
    this.of('DEVIS')
      .filter((d) => (d.ageDays ?? 0) >= 7)
      .sort((a, b) => b.totals.totalTtc - a.totals.totalTtc),
  );

  /** Commandes acceptées mais pas encore facturées. */
  readonly toInvoice = computed(() =>
    this.of('COMMANDE').sort((a, b) => (b.ageDays ?? 0) - (a.ageDays ?? 0)),
  );

  /** Factures émises depuis plus de 30 jours et toujours pas réglées. */
  readonly overdue = computed(() =>
    this.of('FACTURE')
      .filter((d) => (d.ageDays ?? 0) > 30)
      .sort((a, b) => (b.ageDays ?? 0) - (a.ageDays ?? 0)),
  );

  /** Documents dont au moins une ligne passe sous le seuil de marge. */
  readonly thinMargin = computed(() => {
    const target = this.marginTarget();
    return this.documents()
      .filter((d) => {
        const meta = statusMeta(d.status);
        if (meta.stage === null || meta.isRevenue) return false;
        return d.totals.marginRate !== null && d.totals.marginRate < target;
      })
      .sort((a, b) => (a.totals.marginRate ?? 0) - (b.totals.marginRate ?? 0));
  });

  readonly actionCount = computed(
    () =>
      this.staleQuotes().length +
      this.toInvoice().length +
      this.overdue().length +
      this.firmSupplyNeeds().length,
  );

  /* ---------------- Approvisionnement ---------------- */

  /**
   * Besoins d'achat : ce qui est dû aux clients et qui manque en stock.
   * Le stock disponible est réparti dans l'ordre d'ancienneté des commandes.
   */
  readonly supplyNeeds = computed<SupplyLine[]>(() =>
    computeSupplyNeeds(this.documents(), this.catalog(), this.forecastSupply()),
  );

  /** Besoins issus d'engagements fermes : ceux-là sont à commander maintenant. */
  readonly firmSupplyNeeds = computed(() => this.supplyNeeds().filter((n) => n.firm));

  /** Besoins regroupés par fournisseur, tels qu'on passe commande. */
  readonly supplyGroups = computed<SupplyGroup[]>(() => groupBySupplier(this.supplyNeeds()));

  /** Montant total des achats à engager. */
  readonly supplyTotal = computed(() =>
    round2(this.supplyNeeds().reduce((s, n) => s + (n.totalCost ?? 0), 0)),
  );

  /** Articles manquants sans fournisseur référencé : impossible de commander. */
  readonly orphanNeeds = computed(() => this.supplyNeeds().filter((n) => !n.supplierName));

  /* ---------------- Classements ---------------- */

  readonly topCustomers = computed(() => {
    const map = new Map<number, { customer: Customer | null; total: number; count: number }>();

    for (const doc of this.documents()) {
      if (!statusMeta(doc.status).isRevenue) continue;
      const id = doc.customer?.ctmId;
      if (id === undefined || id === null) continue;

      const entry = map.get(id) ?? {
        customer: this.customers().find((c) => c.ctmId === id) ?? null,
        total: 0,
        count: 0,
      };
      entry.total = round2(entry.total + doc.totals.totalTtc);
      entry.count += 1;
      map.set(id, entry);
    }

    return [...map.values()].sort((a, b) => b.total - a.total).slice(0, 5);
  });

  readonly topArticles = computed(() => {
    // Clé par référence, pas par `articleId` : les lignes figées (Quote/
    // Command/Invoice) ne portent pas d'identifiant d'article (voir
    // `buildFrozenLine` dans document-math.ts).
    const map = new Map<string, { article: Article | null; qty: number; total: number }>();
    const byRef = new Map(this.articles().map((a) => [a.artReference.toLowerCase(), a]));

    for (const doc of this.documents()) {
      if (!statusMeta(doc.status).isRevenue) continue;
      for (const line of doc.totals.lines) {
        const key = line.reference.toLowerCase();
        const entry = map.get(key) ?? {
          article: this.catalog().get(line.articleId) ?? byRef.get(key) ?? null,
          qty: 0,
          total: 0,
        };
        entry.qty += line.quantity;
        entry.total = round2(entry.total + line.totalTtc);
        map.set(key, entry);
      }
    }

    return [...map.values()].sort((a, b) => b.total - a.total).slice(0, 5);
  });

  readonly pipelineColumns = computed(() =>
    PIPELINE_STAGES.map((status) => {
      const docs = this.of(status);
      return {
        status,
        meta: statusMeta(status),
        documents: docs.sort((a, b) => b.totals.totalTtc - a.totals.totalTtc),
        total: sumTtc(docs),
      };
    }),
  );

  /* ---------------- Chargement ---------------- */

  of(status: DocumentStatus): ValuedDocument[] {
    return this.byStatus().get(status) ?? [];
  }

  find(kind: ValuedDocument['kind'], id: number): ValuedDocument | undefined {
    return this.documents().find((d) => d.kind === kind && d.id === id);
  }

  /** Charge une seule fois, sauf si `force` est demandé. */
  async load(force = false): Promise<void> {
    if (this.loading()) return;
    if (this.loaded() && !force) return;

    this.loading.set(true);
    this.offline.set(false);
    const now = new Date();
    this.now.set(now);

    try {
      const [carts, quotes, commands, invoices, articles, customers] = await Promise.all([
        firstValueFrom(this.cartApi.list()),
        firstValueFrom(this.quoteApi.list()).catch(() => [] as Quote[]),
        firstValueFrom(this.commandApi.list()).catch(() => [] as Command[]),
        firstValueFrom(this.invoiceApi.list()).catch(() => [] as Invoice[]),
        firstValueFrom(this.articleApi.list()).catch(() => [] as Article[]),
        firstValueFrom(this.customerApi.list()).catch(() => [] as Customer[]),
      ]);

      this.articles.set(articles);
      this.customers.set(customers);
      this.cartsRaw.set(carts);
      this.quotesRaw.set(quotes);
      this.commandsRaw.set(commands);
      this.invoicesRaw.set(invoices);

      const catalog = new Map(articles.map((a) => [a.artId, a]));
      const cartsById = new Map(carts.map((c) => [c.crtId, c]));
      const quotesById = new Map(quotes.map((q) => [q.quoteId, q]));

      // Un panier qui a déjà engendré un devis n'est plus un document actif :
      // son contenu vit désormais dans ce devis.
      const cartIdsWithQuote = new Set(quotes.map((q) => q.cartId));
      // Un devis déjà transformé en commande sort de la colonne « Devis ».
      // Depuis la correction backend du 4 août 2026, `CommandDTO` porte
      // `quoteId` : la correspondance se fait par id, plus par `quoteNumber`.
      const quoteIdsWithCommand = new Set(commands.map((c) => c.quoteId));

      const activeCarts = carts.filter((c) => !cartIdsWithQuote.has(c.crtId));
      const activeQuotes = quotes.filter((q) => !quoteIdsWithCommand.has(q.quoteId));

      // Le backend n'expose pas les lignes de panier en masse : une requête
      // par panier actif, lancées en parallèle.
      const cartDocs = await Promise.all(
        activeCarts.map(async (cart) => {
          const lines = await firstValueFrom(this.lineApi.listByCart(cart.crtId)).catch(
            () => [] as OrderLine[],
          );
          return valueCart(cart, lines, catalog, now);
        }),
      );

      const quoteDocs = activeQuotes.map((quote) => {
        const customer = cartsById.get(quote.cartId)?.customer ?? null;
        return valueQuote(quote, customer, now);
      });

      // NB : aucun filtre n'écarte ici une commande déjà facturée — impossible
      // à détecter, `InvoiceDTO` ne porte toujours pas l'id de la commande
      // d'origine. Voir la note en tête de fichier.
      const commandDocs = commands.map((command) => {
        const quote = quotesById.get(command.quoteId);
        const customer = (quote && cartsById.get(quote.cartId)?.customer) ?? null;
        return valueCommand(command, customer, now);
      });

      const invoiceDocs = invoices.map((invoice) => valueInvoice(invoice, now));

      this.documents.set([...cartDocs, ...quoteDocs, ...commandDocs, ...invoiceDocs]);
      this.loaded.set(true);
    } catch {
      this.offline.set(true);
      this.documents.set([]);
    } finally {
      this.loading.set(false);
    }
  }

  reload(): Promise<void> {
    return this.load(true);
  }

  /* ---------------- Transitions ---------------- */

  /**
   * PANIER → DEVIS. Crée un `Quote` reprenant les lignes actuelles du panier
   * (figées côté serveur) et recharge le magasin.
   *
   * @returns l'id du devis créé, ou `null` en cas d'échec.
   */
  async transitionToQuote(cartId: number): Promise<number | null> {
    const year = new Date().getFullYear();
    const qotNumber = buildReference('DEV', this.quotesRaw().map((q) => q.qotNumber), year);
    const expiration = new Date();
    expiration.setDate(expiration.getDate() + 30);

    try {
      const created = await firstValueFrom(
        this.quoteApi.create({
          qotNumber,
          qotExpirationDate: expiration.toISOString().slice(0, 10),
          qotParentId: null,
          cartId,
        }),
      );
      await this.reload();
      return created.quoteId;
    } catch {
      return null;
    }
  }

  /** DEVIS → COMMANDE. @returns l'id de la commande créée, ou `null`. */
  async transitionToCommand(quoteId: number): Promise<number | null> {
    try {
      const created = await firstValueFrom(this.commandApi.create({ qotId: quoteId }));
      await this.reload();
      return created.cmdId;
    } catch {
      return null;
    }
  }

  /** COMMANDE → FACTURE. @returns l'id de la facture créée, ou `null`. */
  async transitionToInvoice(commandId: number): Promise<number | null> {
    const year = new Date().getFullYear();
    const invoiceNumber = buildReference(
      'FAC',
      this.invoicesRaw().map((i) => i.invoiceNumber),
      year,
    );

    try {
      const created = await firstValueFrom(
        this.invoiceApi.create({ invoiceNumber, commandId }),
      );
      await this.reload();
      return created.invoiceId;
    } catch {
      return null;
    }
  }

  /** FACTURE → PAYEE. */
  async markInvoicePaid(invoiceId: number): Promise<boolean> {
    try {
      await firstValueFrom(this.invoiceApi.patchStatus({ invoiceId, invoiceStatus: 'PAID' }));
      await this.reload();
      return true;
    } catch {
      return false;
    }
  }

  /** Annule un devis (le fait sortir du pipeline actif). */
  async cancelQuote(quoteId: number): Promise<boolean> {
    try {
      await firstValueFrom(this.quoteApi.updateStatus(quoteId, 'REJECTED'));
      await this.reload();
      return true;
    } catch {
      return false;
    }
  }

  /** Annule une facture émise par erreur. */
  async cancelInvoice(invoiceId: number): Promise<boolean> {
    try {
      await firstValueFrom(
        this.invoiceApi.patchStatus({ invoiceId, invoiceStatus: 'CANCELLED' }),
      );
      await this.reload();
      return true;
    } catch {
      return false;
    }
  }
}
