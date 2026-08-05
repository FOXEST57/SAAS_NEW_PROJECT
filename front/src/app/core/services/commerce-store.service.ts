import { Injectable, computed, inject, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import {
  ArticleService,
  CartService,
  CustomerService,
  OrderLineService,
} from '../api';
import { Article, Cart, Customer, OrderLine } from '../models/api.models';
import {
  DocumentStatus,
  FUNNEL_STAGES,
  PIPELINE_STAGES,
  normalizeStatus,
  statusMeta,
} from '../models/document-status';
import {
  ProductFamily,
  ValuedDocument,
  round2,
  sumMargin,
  sumTtc,
  valueDocument,
} from '../models/document-math';
import { SupplyGroup, SupplyLine, computeSupplyNeeds, groupBySupplier } from '../models/supply';

/**
 * Magasin de données commerciales, partagé par le tableau de bord, le pipeline
 * et l'écran des relances.
 *
 * Ces trois vues ont besoin exactement du même jeu de données enrichi
 * (documents + lignes + catalogue). Sans mise en commun, chacune referait la
 * requête par document — soit trois fois N+1 appels à chaque navigation. Le
 * magasin charge une fois, expose des `signal`, et les vues se contentent de
 * `computed`.
 *
 * Le rafraîchissement reste explicite : `load()` au montage, `reload()` après
 * une écriture. Pas de rechargement implicite, pour que le comportement reste
 * prévisible.
 */
@Injectable({ providedIn: 'root' })
export class CommerceStore {
  private readonly cartApi = inject(CartService);
  private readonly lineApi = inject(OrderLineService);
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

  /** Instant de référence des calculs d'ancienneté, figé à chaque chargement. */
  readonly now = signal<Date>(new Date(0));

  readonly catalog = computed(() => new Map(this.articles().map((a) => [a.artId, a])));

  /* ---------------- Sélecteurs ---------------- */

  readonly byStatus = computed(() => {
    const map = new Map<DocumentStatus, ValuedDocument[]>();
    for (const doc of this.documents()) {
      const key = normalizeStatus(doc.cart.crtStatus);
      const list = map.get(key);
      if (list) list.push(doc);
      else map.set(key, [doc]);
    }
    return map;
  });

  /** Chiffre d'affaires facturé : factures émises et réglées. */
  readonly revenue = computed(() =>
    sumTtc(this.documents().filter((d) => statusMeta(d.cart.crtStatus).isRevenue)),
  );

  readonly revenueMargin = computed(() =>
    sumMargin(this.documents().filter((d) => statusMeta(d.cart.crtStatus).isRevenue)),
  );

  /** Valeur du pipeline : tout ce qui n'est ni facturé ni annulé. */
  readonly pipelineValue = computed(() =>
    sumTtc(
      this.documents().filter((d) => {
        const meta = statusMeta(d.cart.crtStatus);
        return meta.stage !== null && !meta.isRevenue;
      }),
    ),
  );

  /** Commandes fermes en attente de facturation. */
  readonly committedValue = computed(() =>
    sumTtc(this.of('COMMANDE')),
  );

  /** Encours client : factures émises non encore réglées. */
  readonly outstanding = computed(() => sumTtc(this.of('FACTURE')));

  readonly marginRate = computed(() => {
    const docs = this.documents().filter((d) => statusMeta(d.cart.crtStatus).isRevenue);
    const covered = round2(
      docs.reduce((s, d) => s + d.totals.totalHt * d.totals.costCoverage, 0),
    );
    return covered > 0 ? sumMargin(docs) / covered : null;
  });

  /** Part du CA reposant sur un coût d'achat connu. */
  readonly costCoverage = computed(() => {
    const docs = this.documents().filter((d) => statusMeta(d.cart.crtStatus).isRevenue);
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
        const meta = statusMeta(d.cart.crtStatus);
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
        if (!statusMeta(doc.cart.crtStatus).isRevenue) continue;
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
        const meta = statusMeta(d.cart.crtStatus);
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
      if (!statusMeta(doc.cart.crtStatus).isRevenue) continue;
      const id = doc.cart.customer?.ctmId;
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
    const map = new Map<number, { article: Article | null; qty: number; total: number }>();

    for (const doc of this.documents()) {
      if (!statusMeta(doc.cart.crtStatus).isRevenue) continue;
      for (const line of doc.totals.lines) {
        const entry = map.get(line.articleId) ?? {
          article: this.catalog().get(line.articleId) ?? null,
          qty: 0,
          total: 0,
        };
        entry.qty += line.quantity;
        entry.total = round2(entry.total + line.totalTtc);
        map.set(line.articleId, entry);
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

  find(crtId: number): ValuedDocument | undefined {
    return this.documents().find((d) => d.cart.crtId === crtId);
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
      const [carts, articles, customers] = await Promise.all([
        firstValueFrom(this.cartApi.list()),
        firstValueFrom(this.articleApi.list()).catch(() => [] as Article[]),
        firstValueFrom(this.customerApi.list()).catch(() => [] as Customer[]),
      ]);

      this.articles.set(articles);
      this.customers.set(customers);

      const catalog = new Map(articles.map((a) => [a.artId, a]));

      // Le backend n'expose pas les lignes en masse : une requête par document,
      // mais lancées en parallèle et mutualisées pour les trois écrans.
      const valued = await Promise.all(
        carts.map(async (cart: Cart) => {
          const lines = await firstValueFrom(this.lineApi.listByCart(cart.crtId)).catch(
            () => [] as OrderLine[],
          );
          return valueDocument(cart, lines, catalog, now);
        }),
      );

      this.documents.set(valued);
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

  /**
   * Met à jour localement le statut d'un document, sans rechargement complet.
   * Utilisé par le pipeline pour que le glisser-déposer reste instantané ;
   * l'appel réseau reste à la charge de l'appelant.
   */
  patchStatus(crtId: number, status: DocumentStatus, reference?: string): void {
    this.documents.update((docs) =>
      docs.map((d) =>
        d.cart.crtId === crtId
          ? {
              ...d,
              cart: { ...d.cart, crtStatus: status, crtRef: reference ?? d.cart.crtRef },
            }
          : d,
      ),
    );
  }
}
