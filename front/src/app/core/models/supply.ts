import { Article } from './api.models';
import { ValuedDocument, bestCost, familyOf, round2, ProductFamily } from './document-math';
import { statusMeta } from './document-status';

/**
 * Besoin d'approvisionnement.
 *
 * Un devis peut porter sur du matériel qui n'est pas en stock — c'est le cas
 * nominal en CVC, où l'on chiffre avant d'acheter. Dès que le devis devient une
 * **commande**, l'engagement est pris : il faut approvisionner.
 *
 * Ce module calcule, pour les documents engagés, l'écart entre les quantités
 * dues et le stock disponible, puis regroupe le manque par fournisseur — celui
 * qui propose le meilleur prix, cohérent avec le calcul de marge.
 *
 * Le stock est **réparti dans l'ordre d'ancienneté des commandes** : la plus
 * ancienne sert en premier. Sans cela, deux commandes portant sur le même
 * article se croiraient chacune servie par le même stock.
 */

/** Une commande à passer chez un fournisseur, pour un article. */
export interface SupplyLine {
  readonly articleId: number;
  readonly reference: string;
  readonly name: string;
  readonly family: ProductFamily;
  /** Quantité totale due par les documents engagés. */
  readonly required: number;
  /** Stock disponible au moment du calcul. */
  readonly available: number;
  /** Quantité manquante, à commander. */
  readonly missing: number;
  /** Meilleur prix d'achat unitaire connu. */
  readonly unitCost: number | null;
  /** Coût total de la commande fournisseur. */
  readonly totalCost: number | null;
  readonly supplierName: string | null;
  /** Documents à l'origine du besoin. */
  readonly sources: readonly SupplySource[];
  /** Le besoin découle-t-il d'un engagement ferme (commande) ? */
  readonly firm: boolean;
}

export interface SupplySource {
  readonly crtId: number;
  readonly crtRef: string;
  readonly customer: string;
  readonly quantity: number;
  readonly firm: boolean;
}

/** Besoins regroupés par fournisseur, tels qu'on passe commande. */
export interface SupplyGroup {
  readonly supplierName: string;
  readonly lines: readonly SupplyLine[];
  readonly total: number;
  readonly itemCount: number;
  /** Au moins une ligne provient d'une commande ferme. */
  readonly firm: boolean;
}

/** Article sans fournisseur référencé : impossible de savoir où commander. */
export const NO_SUPPLIER = 'Fournisseur à référencer';

/**
 * Calcule les besoins d'approvisionnement.
 *
 * @param documents documents valorisés (toutes étapes confondues)
 * @param catalog   catalogue indexé par `artId`
 * @param includeQuotes inclure les devis, en besoin prévisionnel
 */
export function computeSupplyNeeds(
  documents: readonly ValuedDocument[],
  catalog: ReadonlyMap<number, Article>,
  includeQuotes = false,
): SupplyLine[] {
  // Les commandes fermes d'abord, puis les devis : le stock disponible sert
  // en priorité ce qui est déjà engagé.
  const relevant = documents
    .filter((d) => {
      const meta = statusMeta(d.cart.crtStatus);
      if (meta.value === 'COMMANDE') return true;
      return includeQuotes && meta.value === 'DEVIS';
    })
    .sort((a, b) => {
      const aFirm = statusMeta(a.cart.crtStatus).value === 'COMMANDE' ? 0 : 1;
      const bFirm = statusMeta(b.cart.crtStatus).value === 'COMMANDE' ? 0 : 1;
      if (aFirm !== bFirm) return aFirm - bFirm;
      // À engagement égal, la commande la plus ancienne est servie d'abord.
      return (b.ageDays ?? 0) - (a.ageDays ?? 0);
    });

  const byArticle = new Map<
    number,
    { required: number; sources: SupplySource[]; firm: boolean }
  >();

  for (const doc of relevant) {
    const firm = statusMeta(doc.cart.crtStatus).value === 'COMMANDE';
    const customer = [doc.cart.customer?.ctmFirstName, doc.cart.customer?.ctmLastName]
      .filter(Boolean)
      .join(' ');

    for (const line of doc.totals.lines) {
      const entry = byArticle.get(line.articleId) ?? {
        required: 0,
        sources: [],
        firm: false,
      };
      entry.required += line.quantity;
      entry.firm = entry.firm || firm;
      entry.sources.push({
        crtId: doc.cart.crtId,
        crtRef: doc.cart.crtRef,
        customer: customer || 'Client inconnu',
        quantity: line.quantity,
        firm,
      });
      byArticle.set(line.articleId, entry);
    }
  }

  const needs: SupplyLine[] = [];

  for (const [articleId, entry] of byArticle) {
    const article = catalog.get(articleId);
    if (!article) continue;

    const available = Math.max(0, Number(article.artStock ?? 0));
    const missing = Math.max(0, entry.required - available);
    if (missing === 0) continue;

    const cost = bestCost(article);

    needs.push({
      articleId,
      reference: article.artReference,
      name: article.artName,
      family: familyOf(article),
      required: entry.required,
      available,
      missing,
      unitCost: cost?.cost ?? null,
      totalCost: cost ? round2(cost.cost * missing) : null,
      supplierName: cost?.supplier ?? null,
      sources: entry.sources,
      firm: entry.firm,
    });
  }

  // Les engagements fermes en premier, puis par montant décroissant.
  return needs.sort((a, b) => {
    if (a.firm !== b.firm) return a.firm ? -1 : 1;
    return (b.totalCost ?? 0) - (a.totalCost ?? 0);
  });
}

/** Regroupe les besoins par fournisseur : une commande d'achat par groupe. */
export function groupBySupplier(needs: readonly SupplyLine[]): SupplyGroup[] {
  const map = new Map<string, SupplyLine[]>();

  for (const need of needs) {
    const key = need.supplierName ?? NO_SUPPLIER;
    const list = map.get(key);
    if (list) list.push(need);
    else map.set(key, [need]);
  }

  return [...map.entries()]
    .map(([supplierName, lines]) => ({
      supplierName,
      lines,
      total: round2(lines.reduce((s, l) => s + (l.totalCost ?? 0), 0)),
      itemCount: lines.reduce((s, l) => s + l.missing, 0),
      firm: lines.some((l) => l.firm),
    }))
    .sort((a, b) => {
      if (a.firm !== b.firm) return a.firm ? -1 : 1;
      return b.total - a.total;
    });
}

/**
 * Manques d'un document isolé, pour l'alerte affichée au passage en commande.
 * Ne tient pas compte des autres documents : c'est un contrôle local, destiné à
 * informer au moment de la décision.
 */
export function shortagesOf(
  doc: ValuedDocument,
): { name: string; reference: string; missing: number; supplierName: string | null }[] {
  return doc.totals.lines
    .filter((l) => l.quantity > l.stock)
    .map((l) => ({
      name: l.name,
      reference: l.reference,
      missing: l.quantity - Math.max(0, l.stock),
      supplierName: l.costSupplier,
    }));
}
