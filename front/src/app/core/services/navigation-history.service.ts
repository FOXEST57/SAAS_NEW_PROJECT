import { Injectable, computed, inject, signal } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs/operators';

interface Visited {
  readonly url: string;
  /** Groupe nominal avec sa préposition : « au pipeline », « aux articles ». */
  readonly label: string;
}

/**
 * Libellé de retour associé à chaque route, **préposition comprise**.
 *
 * Déduire la préposition d'un nom d'écran ne marche pas en français : « au
 * pipeline » mais « à l'approvisionnement », « aux articles » mais « à la file
 * À traiter ». On la déclare donc plutôt que de la deviner.
 *
 * L'ordre compte : la première entrée dont le préfixe correspond gagne, les
 * routes les plus spécifiques doivent donc précéder les plus générales.
 */
const LABELS: readonly { readonly prefix: string; readonly label: string }[] = [
  { prefix: '/tableau-de-bord', label: 'au tableau de bord' },
  { prefix: '/pipeline', label: 'au pipeline' },
  { prefix: '/a-traiter', label: 'à la file À traiter' },
  { prefix: '/approvisionnement', label: "à l'approvisionnement" },
  { prefix: '/documents/nouveau', label: 'au nouveau document' },
  { prefix: '/documents', label: 'aux devis & factures' },
  { prefix: '/articles', label: 'aux articles' },
  { prefix: '/categories', label: 'aux catégories' },
  { prefix: '/tva', label: 'aux taux de TVA' },
  { prefix: '/references', label: 'aux références' },
  { prefix: '/clients', label: 'aux clients' },
  { prefix: '/types-de-compte', label: 'aux types de compte' },
  { prefix: '/fournisseurs', label: 'aux fournisseurs' },
  { prefix: '/fabricants', label: 'aux fabricants' },
  { prefix: '/adresses', label: 'aux adresses' },
  { prefix: '/villes', label: 'aux villes' },
  { prefix: '/codes-postaux', label: 'aux codes postaux' },
  { prefix: '/pays', label: 'aux pays' },
  { prefix: '/associations-cp-ville', label: 'aux associations CP / ville' },
];

/**
 * Mémorise la provenance de l'utilisateur, pour que « Retour » le ramène
 * réellement d'où il vient.
 *
 * Un lien de retour codé en dur ment dès qu'un écran est atteignable par
 * plusieurs chemins. Le document imprimable, par exemple, s'ouvre depuis le
 * pipeline, depuis la liste des documents et depuis l'éditeur : le renvoyer
 * systématiquement vers l'éditeur casse le geste dans deux cas sur trois.
 *
 * On ne s'appuie pas sur `history.back()` : l'historique du navigateur inclut
 * les pages extérieures à l'application, et ne fournit aucun libellé. Ici on ne
 * retient que la navigation interne, et on sait la nommer.
 */
@Injectable({ providedIn: 'root' })
export class NavigationHistoryService {
  private readonly router = inject(Router);

  /** Pile des URL internes visitées, la plus récente en dernier. */
  private readonly stack = signal<string[]>([]);

  /** Écran précédent, hors écran courant. */
  readonly previous = computed<Visited | null>(() => {
    const list = this.stack();
    // L'avant-dernière entrée : la dernière est l'écran affiché.
    const url = list.length >= 2 ? list[list.length - 2] : null;
    return url ? { url, label: labelFor(url) } : null;
  });

  constructor() {
    this.router.events
      .pipe(filter((e): e is NavigationEnd => e instanceof NavigationEnd))
      .subscribe((event) => {
        const url = event.urlAfterRedirects;

        // L'écran de connexion n'a pas à figurer dans l'historique : y
        // « revenir » après s'être identifié n'a pas de sens, et le garde
        // `guestGuard` renverrait aussitôt ailleurs.
        if (url.startsWith('/connexion')) return;

        this.stack.update((list) => {
          // Une navigation vers l'écran courant ne crée pas d'entrée.
          if (list[list.length - 1] === url) return list;

          // Retour en arrière : on dépile au lieu d'empiler, sinon un
          // aller-retour répété ferait grossir la pile indéfiniment.
          if (list[list.length - 2] === url) return list.slice(0, -1);

          // On borne la pile : au-delà, l'information n'a plus d'usage.
          return [...list, url].slice(-10);
        });
      });
  }

  /**
   * Destination de retour : l'écran précédent s'il existe, sinon le repli
   * fourni par l'appelant.
   */
  target(fallbackUrl: string, fallbackLabel?: string): Visited {
    const previous = this.previous();
    if (previous) return previous;
    return { url: fallbackUrl, label: fallbackLabel ?? labelFor(fallbackUrl) };
  }
}

function labelFor(url: string): string {
  const path = url.split('?')[0].split('#')[0];
  const match = LABELS.find((entry) => path.startsWith(entry.prefix));
  return match?.label ?? 'à l’écran précédent';
}
