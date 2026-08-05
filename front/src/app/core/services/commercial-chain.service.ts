import { Injectable, inject } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { CommandService, InvoiceService, QuoteService } from '../api';
import { Cart, Command, Invoice, Quote } from '../models/api.models';
import { ToastService } from './toast.service';

/**
 * Matérialisation en base des étapes Commande et Facture.
 *
 * Le pipeline affiche l'avancement d'un document à partir de `crtStatus`, un
 * champ porté par le panier. Mais ce statut ne fait que **décrire** l'étape :
 * il ne crée rien. Les documents commerciaux eux-mêmes — devis, bon de
 * commande, facture — sont de vraies entités, avec leurs propres lignes
 * figées et leur propre numérotation.
 *
 * `QuoteIssueService` assure déjà cette matérialisation pour le devis. Ce
 * service fait exactement la même chose pour les deux étapes suivantes, en
 * suivant la chaîne imposée par le backend :
 *
 *   Panier ──▶ Quote ──▶ Command ──▶ Invoice
 *
 * Chaque maillon a besoin du précédent : on ne peut pas créer une commande
 * sans devis, ni une facture sans commande. Les méthodes ci-dessous
 * reconstituent donc la chaîne à partir du panier, et s'arrêtent proprement
 * (avec un message) si un maillon manque.
 *
 * Toutes sont **idempotentes** : rejouer une transition ne crée pas de
 * doublon. C'est nécessaire, car le serveur refuse une seconde commande sur
 * le même devis (comme une seconde facture sur la même commande) avec une
 * erreur 409 ; mieux vaut retrouver l'existant que de faire échouer le geste
 * de l'utilisateur.
 */
@Injectable({ providedIn: 'root' })
export class CommercialChainService {
  private readonly quoteApi = inject(QuoteService);
  private readonly commandApi = inject(CommandService);
  private readonly invoiceApi = inject(InvoiceService);
  private readonly toast = inject(ToastService);

  /**
   * Devis en vigueur d'un panier : le premier qui n'a pas été remplacé par
   * une révision. C'est celui qui fait foi, et donc celui qui doit être
   * transformé en commande.
   */
  private async quoteInForce(cartId: number): Promise<Quote | null> {
    const all = await firstValueFrom(this.quoteApi.list()).catch(() => [] as Quote[]);
    return all.find((q) => q.cartId === cartId && q.qotStatus !== 'REVISITED') ?? null;
  }

  /**
   * Garantit qu'une commande existe pour ce panier, et la renvoie.
   *
   * @param silent n'affiche pas de notification de succès. Le pipeline en
   *   émet déjà une pour la transition elle-même ; deux messages pour un seul
   *   geste seraient du bruit.
   */
  async ensureCommandForCart(
    cart: Cart,
    options: { readonly silent?: boolean } = {},
  ): Promise<Command | null> {
    const quote = await this.quoteInForce(cart.crtId);
    if (!quote?.quoteId) {
      this.toast.warning(
        'Commande non créée',
        "Aucun devis en vigueur pour ce document : le bon de commande n'a pas pu être établi.",
      );
      return null;
    }

    // Une commande existe peut-être déjà — repasser un document en devis puis
    // à nouveau en commande ne doit pas la dupliquer (le serveur refuserait).
    const commands = await firstValueFrom(this.commandApi.list()).catch(() => [] as Command[]);
    const existing = commands.find((c) => c.quoteId === quote.quoteId);
    if (existing) return existing;

    try {
      const created = await firstValueFrom(this.commandApi.create({ qotId: quote.quoteId }));
      if (!options.silent) {
        this.toast.success(
          `Bon de commande établi pour ${quote.qotNumber}`,
          'Les lignes du devis sont reprises telles quelles : le contenu est désormais engagé.',
        );
      }
      return created;
    } catch {
      // La transition du panier a déjà eu lieu : mieux vaut la conserver et
      // signaler l'échec que de tout annuler.
      this.toast.warning(
        'Commande non créée',
        `Le document est bien passé en commande, mais l’enregistrement du bon de commande de ${quote.qotNumber} a échoué.`,
      );
      return null;
    }
  }

  /**
   * Garantit qu'une facture existe pour ce panier, et la renvoie.
   *
   * La commande est créée au passage si elle n'existe pas encore : facturer
   * un document qui n'a pas transité par la case commande reste possible
   * (l'utilisateur peut sauter l'étape), et la chaîne doit rester complète en
   * base quoi qu'il arrive.
   */
  async ensureInvoiceForCart(
    cart: Cart,
    options: { readonly silent?: boolean } = {},
  ): Promise<Invoice | null> {
    const command = await this.ensureCommandForCart(cart, { silent: true });
    if (!command) {
      this.toast.warning(
        'Facture non créée',
        "Aucune commande n'a pu être établie pour ce document : la facture n'a pas été émise.",
      );
      return null;
    }

    const invoices = await firstValueFrom(this.invoiceApi.list()).catch(() => [] as Invoice[]);
    const number = this.invoiceNumberFor(cart, invoices);

    const existing = this.matchInvoice(invoices, number);
    if (existing) return existing;

    try {
      const created = await firstValueFrom(
        this.invoiceApi.create({ invoiceNumber: number, commandId: command.cmdId }),
      );
      if (!options.silent) {
        this.toast.success(
          `Facture ${created.invoiceNumber} émise`,
          'Le contenu est figé définitivement : toute correction passera par un avoir.',
        );
      }
      return created;
    } catch {
      this.toast.warning(
        'Facture non créée',
        `Le document est bien passé en facture, mais l’enregistrement de la facture ${number} a échoué.`,
      );
      return null;
    }
  }

  /**
   * Facture déjà émise pour ce panier, s'il y en a une.
   *
   * `InvoiceDTO` ne porte pas l'identifiant de sa commande : impossible de
   * reconnaître une facture par son lien d'origine. On se rabat sur le
   * numéro, dérivé de la référence du document et donc stable d'un appel à
   * l'autre. À remplacer par un test sur `commandId` dès que le backend
   * l'exposera.
   */
  async invoiceForCart(cart: Cart): Promise<Invoice | null> {
    const invoices = await firstValueFrom(this.invoiceApi.list()).catch(() => [] as Invoice[]);
    return this.matchInvoice(invoices, this.invoiceNumberFor(cart, invoices));
  }

  private matchInvoice(invoices: readonly Invoice[], number: string): Invoice | null {
    const key = number.toUpperCase();
    return invoices.find((i) => (i.invoiceNumber ?? '').toUpperCase() === key) ?? null;
  }

  /**
   * Marque la facture comme transmise au client (`SENT`).
   *
   * Le pendant, côté facture, de la transmission d'un devis : le document a
   * quitté la maison, le client en détient un exemplaire.
   */
  async markInvoiceAsSent(invoice: Invoice): Promise<Invoice | null> {
    try {
      const updated = await firstValueFrom(
        this.invoiceApi.patchStatus({ invoiceId: invoice.invoiceId, invoiceStatus: 'SENT' }),
      );
      this.toast.success(
        `Facture ${invoice.invoiceNumber} transmise`,
        'Le client en détient désormais un exemplaire.',
      );
      return updated;
    } catch {
      return null;
    }
  }

  /**
   * Marque comme réglée (`PAID`) la facture de ce panier.
   *
   * Appelée au passage du document à l'étape « Payée ». La facture est créée
   * au besoin : marquer payé un document qui n'a pas transité explicitement
   * par la case facture doit tout de même laisser une facture réglée en base.
   *
   * @param silent n'affiche pas de notification de succès — le pipeline en
   *   émet déjà une pour la transition elle-même.
   */
  async markInvoicePaidForCart(
    cart: Cart,
    options: { readonly silent?: boolean } = {},
  ): Promise<Invoice | null> {
    const invoice = await this.ensureInvoiceForCart(cart, { silent: true });
    if (!invoice) return null;

    if (invoice.invoiceStatus === 'PAID') return invoice;

    try {
      const updated = await firstValueFrom(
        this.invoiceApi.patchStatus({ invoiceId: invoice.invoiceId, invoiceStatus: 'PAID' }),
      );
      if (!options.silent) {
        this.toast.success(`Facture ${invoice.invoiceNumber} réglée`, 'Le règlement est enregistré.');
      }
      return updated;
    } catch {
      this.toast.warning(
        'Règlement non enregistré',
        `Le document est bien passé en « payée », mais le statut de la facture ${invoice.invoiceNumber} n’a pas pu être mis à jour.`,
      );
      return null;
    }
  }

  /**
   * Numéro de facture d'un document.
   *
   * La référence du panier porte déjà le millésime et le numéro d'ordre du
   * dossier (`CDE-2026-0007`) : on se contente d'en changer le préfixe, pour
   * qu'une facture reste rattachable à l'œil à son devis et à sa commande.
   * Si la référence ne suit pas la convention, on retombe sur une
   * numérotation propre, calculée à partir des factures existantes.
   */
  private invoiceNumberFor(cart: Cart, existing: readonly Invoice[]): string {
    const match = /^[A-Za-z]{2,4}-(\d{4})-(\d+)$/.exec((cart.crtRef ?? '').trim());
    if (match) return `FAC-${match[1]}-${match[2]}`;

    const year = new Date().getFullYear();
    const pattern = new RegExp(`^FAC-${year}-(\\d{4})$`, 'i');
    const max = existing.reduce((acc, invoice) => {
      const found = pattern.exec((invoice.invoiceNumber ?? '').trim());
      return found ? Math.max(acc, Number(found[1])) : acc;
    }, 0);

    return `FAC-${year}-${String(max + 1).padStart(4, '0')}`;
  }
}
