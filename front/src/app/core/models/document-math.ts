import { Article, Cart, OrderLine } from './api.models';

/**
 * Calculs commerciaux d'un document : totaux, ventilation de TVA et
 * **rentabilité**.
 *
 * La marge est le point différenciant. Le MCD stocke le prix d'achat consenti
 * par chaque fournisseur dans `SupplierReference.supplierPrice` : on dispose
 * donc d'un coût de revient réel, ce que la plupart des logiciels de
 * facturation ignorent. La règle retenue est le **meilleur prix d'achat
 * connu** — le fournisseur le moins cher référencé pour l'article — car c'est
 * celui sur lequel l'acheteur se positionnerait.
 *
 * Un article sans référence fournisseur a un coût inconnu : il est exclu du
 * calcul de marge plutôt que compté à zéro, ce qui gonflerait artificiellement
 * la rentabilité. Le taux de couverture indique quelle part du chiffre
 * d'affaires repose sur un coût connu.
 */

/* ==================================================================
   Familles de produits
   ================================================================== */

export type ProductFamily = 'clim' | 'chauffage' | 'autre';

export interface FamilyMeta {
  readonly key: ProductFamily;
  readonly label: string;
  readonly icon: string;
}

export const FAMILIES: Record<ProductFamily, FamilyMeta> = {
  clim: { key: 'clim', label: 'Climatisation', icon: 'snowflake' },
  chauffage: { key: 'chauffage', label: 'Chauffage', icon: 'flame' },
  autre: { key: 'autre', label: 'Autre', icon: 'package' },
};

const CLIM_PATTERN = /(clim|split|r[ée]versible|froid|gainable|cassette|monobloc)/i;
const CHAUFFAGE_PATTERN =
  /(chauff|radiateur|chaudi|pac\b|pompe\s*à\s*chaleur|po[êe]le|plancher|thermodynamique|ballon)/i;

/**
 * Déduit la famille d'un article de son nom, de sa description et de ses
 * catégories. Le chauffage est testé en premier : « pompe à chaleur
 * réversible » relève du chauffage même si le mot « réversible » évoque la
 * climatisation.
 */
export function familyOf(article: Article | null | undefined): ProductFamily {
  if (!article) return 'autre';
  const haystack = [
    article.artName,
    article.artDescription,
    ...(article.categories ?? []).map((c) => c.catName),
  ]
    .filter(Boolean)
    .join(' ');

  if (CHAUFFAGE_PATTERN.test(haystack)) return 'chauffage';
  if (CLIM_PATTERN.test(haystack)) return 'clim';
  return 'autre';
}

/* ==================================================================
   Lignes et totaux
   ================================================================== */

export interface DocumentLine {
  readonly articleId: number;
  readonly reference: string;
  readonly name: string;
  readonly description: string;
  readonly family: ProductFamily;
  readonly quantity: number;
  /** Prix unitaire de vente hors taxes. */
  readonly unitHt: number;
  /** Meilleur prix d'achat unitaire connu, `null` si aucun fournisseur. */
  readonly unitCost: number | null;
  /** Nom du fournisseur retenu pour le coût. */
  readonly costSupplier: string | null;
  /** Taux de TVA sous forme de fraction (0.2 pour 20 %). */
  readonly vatRate: number;
  readonly vatLabel: string;
  readonly totalHt: number;
  readonly totalVat: number;
  readonly totalTtc: number;
  /** Coût de revient total de la ligne, `null` si inconnu. */
  readonly totalCost: number | null;
  /** Marge brute de la ligne, `null` si le coût est inconnu. */
  readonly margin: number | null;
  /** Taux de marge sur le prix de vente, `null` si le coût est inconnu. */
  readonly marginRate: number | null;
  /** Stock disponible, pour signaler les lignes non honorables. */
  readonly stock: number;
}

export interface VatBucket {
  readonly rate: number;
  readonly label: string;
  readonly baseHt: number;
  readonly amount: number;
}

export interface DocumentTotals {
  readonly lines: DocumentLine[];
  readonly totalHt: number;
  readonly totalVat: number;
  readonly totalTtc: number;
  readonly buckets: VatBucket[];
  readonly itemCount: number;
  /** Coût de revient cumulé des lignes dont le coût est connu. */
  readonly totalCost: number;
  /** Marge brute cumulée sur ces mêmes lignes. */
  readonly margin: number;
  /** Taux de marge global, `null` si aucun coût n'est connu. */
  readonly marginRate: number | null;
  /** Part du CA HT reposant sur un coût connu (0 à 1). */
  readonly costCoverage: number;
  /** Répartition du CA HT par famille de produits. */
  readonly byFamily: Record<ProductFamily, number>;
}

/** Accepte un taux exprimé en fraction (0.2) ou en points (20). */
export function normalizeRate(taux: number | null | undefined): number {
  const n = Number(taux ?? 0);
  if (!Number.isFinite(n)) return 0;
  return n > 1 ? n / 100 : n;
}

export function round2(value: number): number {
  return Math.round((value + Number.EPSILON) * 100) / 100;
}

/** Meilleur prix d'achat connu pour un article, avec le fournisseur retenu. */
export function bestCost(
  article: Article | null | undefined,
): { cost: number; supplier: string | null } | null {
  const refs = (article?.suppliers ?? []).filter(
    (r) => r && Number.isFinite(Number(r.supplierPrice)) && Number(r.supplierPrice) > 0,
  );
  if (!refs.length) return null;

  const best = refs.reduce((acc, r) =>
    Number(r.supplierPrice) < Number(acc.supplierPrice) ? r : acc,
  );
  return { cost: Number(best.supplierPrice), supplier: best.supplier?.splName ?? null };
}

/** Construit une ligne de document à partir d'un article du catalogue. */
export function buildLine(article: Article, quantity: number): DocumentLine {
  const unitHt = Number(article.artPriceExcludeTaxes ?? 0);
  const vatRate = normalizeRate(article.tva?.tvaTaux);
  const qty = Math.max(0, Number(quantity ?? 0));

  const totalHt = round2(unitHt * qty);
  const totalVat = round2(totalHt * vatRate);

  const cost = bestCost(article);
  const totalCost = cost ? round2(cost.cost * qty) : null;
  const margin = totalCost === null ? null : round2(totalHt - totalCost);
  const marginRate = margin === null || totalHt === 0 ? null : margin / totalHt;

  return {
    articleId: article.artId,
    reference: article.artReference,
    name: article.artName,
    description: article.artDescription,
    family: familyOf(article),
    quantity: qty,
    unitHt,
    unitCost: cost?.cost ?? null,
    costSupplier: cost?.supplier ?? null,
    vatRate,
    vatLabel: article.tva?.tvaName ?? '—',
    totalHt,
    totalVat,
    totalTtc: round2(totalHt + totalVat),
    totalCost,
    margin,
    marginRate,
    stock: Number(article.artStock ?? 0),
  };
}

/** Agrège une liste de lignes déjà construites. */
export function totalsOf(lines: readonly DocumentLine[]): DocumentTotals {
  const byRate = new Map<number, VatBucket>();
  const byFamily: Record<ProductFamily, number> = { clim: 0, chauffage: 0, autre: 0 };

  let totalHt = 0;
  let totalVat = 0;
  let itemCount = 0;
  let totalCost = 0;
  let coveredHt = 0;

  for (const line of lines) {
    totalHt += line.totalHt;
    totalVat += line.totalVat;
    itemCount += line.quantity;
    byFamily[line.family] += line.totalHt;

    if (line.totalCost !== null) {
      totalCost += line.totalCost;
      coveredHt += line.totalHt;
    }

    const existing = byRate.get(line.vatRate);
    byRate.set(line.vatRate, {
      rate: line.vatRate,
      label: line.vatLabel,
      baseHt: round2((existing?.baseHt ?? 0) + line.totalHt),
      amount: round2((existing?.amount ?? 0) + line.totalVat),
    });
  }

  totalHt = round2(totalHt);
  totalVat = round2(totalVat);
  totalCost = round2(totalCost);
  coveredHt = round2(coveredHt);

  const margin = round2(coveredHt - totalCost);

  return {
    lines: [...lines],
    totalHt,
    totalVat,
    totalTtc: round2(totalHt + totalVat),
    buckets: [...byRate.values()].sort((a, b) => a.rate - b.rate),
    itemCount,
    totalCost,
    margin,
    marginRate: coveredHt > 0 ? margin / coveredHt : null,
    costCoverage: totalHt > 0 ? coveredHt / totalHt : 0,
    byFamily: {
      clim: round2(byFamily.clim),
      chauffage: round2(byFamily.chauffage),
      autre: round2(byFamily.autre),
    },
  };
}

/**
 * Agrège les lignes d'un document.
 *
 * @param orderLines lignes renvoyées par `/order-line/list-articles/{cartId}`
 * @param catalog    catalogue complet, indexé par `artId`
 */
export function computeTotals(
  orderLines: readonly OrderLine[],
  catalog: ReadonlyMap<number, Article>,
): DocumentTotals {
  const lines: DocumentLine[] = [];

  for (const ol of orderLines) {
    const articleId = ol.article?.artId ?? ol.id?.articleId;
    if (articleId === undefined || articleId === null) continue;

    const article = catalog.get(articleId);
    if (!article) continue;

    lines.push(buildLine(article, ol.quantity ?? 0));
  }

  return totalsOf(lines);
}

/* ==================================================================
   Références
   ================================================================== */

/** Génère une référence de document lisible, ex. `DEV-2026-0007`. */
export function buildReference(
  prefix: string,
  existingRefs: readonly string[],
  year: number,
): string {
  // Le numéro d'ordre est global à l'année, tous préfixes confondus : un devis
  // transformé en commande conserve ainsi son numéro sans risque de collision.
  const pattern = new RegExp(`^[A-Za-z]{2,4}-${year}-(\\d+)$`, 'i');

  const max = existingRefs.reduce((acc, ref) => {
    const match = pattern.exec((ref ?? '').trim());
    return match ? Math.max(acc, Number(match[1])) : acc;
  }, 0);

  return `${prefix}-${year}-${String(max + 1).padStart(4, '0')}`;
}

/* ==================================================================
   Analyse d'un portefeuille de documents
   ================================================================== */

/** Document enrichi de ses totaux, utilisé par le tableau de bord. */
export interface ValuedDocument {
  readonly cart: Cart;
  readonly totals: DocumentTotals;
  /** Date de référence (dernière modification, à défaut la création). */
  readonly date: Date | null;
  /** Ancienneté en jours depuis cette date. */
  readonly ageDays: number | null;
}

export function valueDocument(
  cart: Cart,
  lines: readonly OrderLine[],
  catalog: ReadonlyMap<number, Article>,
  now: Date,
): ValuedDocument {
  const raw = cart.crtLastModifieDate ?? cart.crtCreateDate;
  const date = raw ? new Date(raw) : null;
  const valid = date && !Number.isNaN(date.getTime()) ? date : null;

  return {
    cart,
    totals: computeTotals(lines, catalog),
    date: valid,
    ageDays: valid ? Math.floor((now.getTime() - valid.getTime()) / 86_400_000) : null,
  };
}

/** Somme les totaux TTC d'un ensemble de documents. */
export function sumTtc(docs: readonly ValuedDocument[]): number {
  return round2(docs.reduce((s, d) => s + d.totals.totalTtc, 0));
}

/** Somme les marges d'un ensemble de documents. */
export function sumMargin(docs: readonly ValuedDocument[]): number {
  return round2(docs.reduce((s, d) => s + d.totals.margin, 0));
}
