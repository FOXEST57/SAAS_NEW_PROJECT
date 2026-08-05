import { Injectable, inject } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { QuoteService } from '../api';
import { Cart, Quote } from '../models/api.models';
import { defaultExpiration, nextQuoteNumber, nextRevisionNumber } from '../models/quote-math';
import { ToastService } from './toast.service';

/**
 * Création des devis au passage d'un document au statut Devis.
 *
 * Deux chemins mènent à cette transition — l'éditeur de document et le
 * glisser-déposer du pipeline — et ils doivent produire exactement le même
 * effet. D'où ce service partagé plutôt qu'une logique dupliquée des deux
 * côtés, avec le risque qu'elles divergent.
 *
 * Le devis naît au statut `CREATED` : il existe en base, il porte son numéro et
 * ses lignes figées, mais rien n'a encore été transmis au client. C'est cet
 * état qui autorise les corrections. Le passage en `PENDING` marque la
 * transmission, et fige le document.
 */
@Injectable({ providedIn: 'root' })
export class QuoteIssueService {
  private readonly api = inject(QuoteService);
  private readonly toast = inject(ToastService);

  /**
   * Garantit qu'un devis existe pour ce panier, et le renvoie.
   *
   * Si un devis en vigueur existe déjà — c'est-à-dire non remplacé —, on ne
   * crée rien : repasser un document en panier puis à nouveau en devis ne doit
   * pas engendrer une pile de brouillons identiques.
   *
   * @param silent n'affiche pas de notification. Le pipeline en émet déjà une
   *   pour la transition elle-même ; deux messages pour un seul geste seraient
   *   du bruit.
   */
  async ensureForCart(cart: Cart, options: { readonly silent?: boolean } = {}): Promise<Quote | null> {
    const all = await firstValueFrom(this.api.list()).catch(() => [] as Quote[]);
    const existing = all.filter((q) => q.cartId === cart.crtId);
    const inForce = existing.find((q) => q.qotStatus !== 'REVISITED');

    if (inForce) return inForce;

    // Un document déjà passé en devis par le passé garde son numéro de
    // dossier : on repart en révision plutôt qu'en numéro neuf.
    const previous = existing[0] ?? null;
    const number = previous
      ? nextRevisionNumber(previous, all)
      : nextQuoteNumber(all, new Date().getFullYear());

    try {
      const created = await firstValueFrom(
        this.api.create({
          qotNumber: number,
          qotExpirationDate: defaultExpiration(),
          // Le serveur impose `CREATED` de toute façon ; on l'envoie pour que
          // la requête dise ce qu'elle veut, et non l'inverse.
          qotStatus: 'CREATED',
          qotParentId: previous?.quoteId ?? null,
          cartId: cart.crtId,
        }),
      );

      if (!options.silent) {
        this.toast.success(
          `Devis ${created.qotNumber} créé`,
          'Prix et taux de TVA sont figés. Le document reste modifiable tant qu’il n’est pas transmis.',
        );
      }
      return created;
    } catch {
      // La transition du panier a déjà eu lieu : mieux vaut la conserver et
      // signaler l'échec du devis que de tout annuler.
      this.toast.warning(
        'Devis non créé',
        `Le document est bien passé en devis, mais l’enregistrement du devis ${number} a échoué. Vous pouvez le créer depuis l’éditeur.`,
      );
      return null;
    }
  }

  /** Marque un brouillon comme transmis au client. */
  async markAsSent(quote: Quote): Promise<Quote | null> {
    if (!quote.quoteId) {
      this.toast.error(
        'Transmission impossible',
        `L’API ne renvoie pas l’identifiant de ${quote.qotNumber}.`,
      );
      return null;
    }

    try {
      const updated = await firstValueFrom(this.api.patchStatus(quote.quoteId, 'PENDING'));
      this.toast.success(
        `${quote.qotNumber} transmis`,
        'Le devis est désormais figé : toute évolution passera par une révision.',
      );
      return updated;
    } catch {
      return null;
    }
  }
}
