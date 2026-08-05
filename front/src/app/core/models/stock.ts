import { Article, Inventory } from './api.models';

/**
 * Lecture du stock d'un article, à partir des relevés d'inventaire.
 *
 * Le principe posé par `StockService` côté serveur : un relevé constate une
 * quantité réelle à une date, puis on ajoute les entrées postérieures pour
 * obtenir le stock d'aujourd'hui. Le relevé sert d'ancrage — c'est la seule
 * valeur dont on soit certain, parce que quelqu'un est allé compter.
 *
 * ── Ce que le front peut calculer, et ce qu'il ne peut pas ──
 *
 * Les entrées postérieures ne sont **pas** calculables ici : les DTO de
 * réponse des références fournisseur et fabricant n'exposent ni leur statut de
 * livraison, ni leur date de mise à jour. Impossible donc de distinguer une
 * commande reçue d'une commande encore en attente, ni de savoir si elle est
 * postérieure au relevé.
 *
 * Ce module s'en tient donc à ce qui est établi : le dernier relevé, son
 * ancienneté, et l'écart avec le stock que le serveur annonce. Le reste est
 * nommé pour ce qu'il est — indisponible — plutôt que deviné.
 *
 * Voir `backend-patch/Inventory-blocages.patch`, section 4.
 */

/** Au-delà de ce délai, un relevé n'inspire plus confiance. */
export const STALE_AFTER_DAYS = 90;

export interface StockReading {
  /** Dernier relevé connu, ou `null` si l'article n'a jamais été inventorié. */
  readonly lastInventory: Inventory | null;
  /** Quantité constatée lors de ce relevé. */
  readonly countedStock: number | null;
  /** Ancienneté du relevé en jours. `null` si absent ou non daté. */
  readonly ageInDays: number | null;
  /**
   * Stock annoncé par le serveur (`artStock`), somme des références sans
   * égard au statut de livraison.
   */
  readonly reportedStock: number;
  /** Écart entre le stock annoncé et la dernière quantité constatée. */
  readonly drift: number | null;
  readonly status: StockStatus;
}

export type StockStatus =
  /** Jamais inventorié : on ne dispose d'aucune mesure. */
  | 'jamais-inventorie'
  /** Relevé présent mais sans date exploitable — correctif backend requis. */
  | 'date-manquante'
  /** Relevé récent et cohérent avec le stock annoncé. */
  | 'fiable'
  /** Relevé récent, mais l'écart avec le stock annoncé est notable. */
  | 'ecart'
  /** Relevé trop ancien pour être digne de foi. */
  | 'perime';

export const STOCK_STATUS_LABELS: Record<StockStatus, string> = {
  'jamais-inventorie': 'Jamais inventorié',
  'date-manquante': 'Relevé non daté',
  fiable: 'Relevé récent',
  ecart: 'Écart constaté',
  perime: 'Relevé périmé',
};

/**
 * @param now injecté pour rendre la fonction testable et déterministe.
 */
export function readStock(
  article: Pick<Article, 'artId' | 'artStock'>,
  inventories: readonly Inventory[],
  now: Date = new Date(),
): StockReading {
  const own = inventories.filter((inv) => inv.article?.artId === article.artId);
  const last = mostRecent(own);
  const reportedStock = article.artStock ?? 0;

  if (!last) {
    return {
      lastInventory: null,
      countedStock: null,
      ageInDays: null,
      reportedStock,
      drift: null,
      status: 'jamais-inventorie',
    };
  }

  const age = last.invDate ? daysBetween(new Date(last.invDate), now) : null;
  const drift = reportedStock - last.invStock;

  return {
    lastInventory: last,
    countedStock: last.invStock,
    ageInDays: age,
    reportedStock,
    drift,
    status: statusOf(age, drift),
  };
}

function statusOf(age: number | null, drift: number): StockStatus {
  // Sans date, on ne peut ni dater ni comparer : le relevé existe, mais on ne
  // sait pas de quand. Le dire est plus utile que de le supposer récent.
  if (age === null) return 'date-manquante';
  if (age > STALE_AFTER_DAYS) return 'perime';
  return drift === 0 ? 'fiable' : 'ecart';
}

/**
 * Relevé le plus récent.
 *
 * Les relevés non datés sont écartés du classement mais ne le font pas échouer.
 * Si aucun n'est daté, on renvoie le dernier créé — l'identifiant croissant
 * étant le seul ordre disponible.
 */
export function mostRecent(inventories: readonly Inventory[]): Inventory | null {
  if (inventories.length === 0) return null;

  const dated = inventories.filter((inv) => inv.invDate);
  if (dated.length === 0) {
    return inventories.reduce((a, b) => (b.invId > a.invId ? b : a));
  }

  return dated.reduce((a, b) =>
    new Date(b.invDate!).getTime() > new Date(a.invDate!).getTime() ? b : a,
  );
}

/** Relevés d'un article, du plus récent au plus ancien. */
export function chronology(inventories: readonly Inventory[]): Inventory[] {
  return [...inventories].sort((a, b) => {
    if (a.invDate && b.invDate) {
      return new Date(b.invDate).getTime() - new Date(a.invDate).getTime();
    }
    // Les relevés datés priment sur ceux qui ne le sont pas.
    if (a.invDate) return -1;
    if (b.invDate) return 1;
    return b.invId - a.invId;
  });
}

/**
 * Variation entre deux relevés successifs, pour lire l'historique d'un article.
 * Le plus ancien n'a pas de précédent : sa variation est `null`.
 */
export function withVariations(
  inventories: readonly Inventory[],
): { readonly inventory: Inventory; readonly variation: number | null }[] {
  const ordered = chronology(inventories);
  return ordered.map((inventory, index) => {
    const previous = ordered[index + 1];
    return {
      inventory,
      variation: previous ? inventory.invStock - previous.invStock : null,
    };
  });
}

function daysBetween(from: Date, to: Date): number {
  return Math.floor((to.getTime() - from.getTime()) / 86_400_000);
}
