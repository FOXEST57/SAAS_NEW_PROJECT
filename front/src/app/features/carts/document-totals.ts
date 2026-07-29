import { Article, OrderLine } from '../../core/models/api.models';

/** Ligne enrichie : la ligne de commande + l'article complet (prix HT, TVA). */
export interface DocumentLine {
  readonly articleId: number;
  readonly reference: string;
  readonly name: string;
  readonly description: string;
  readonly quantity: number;
  /** Prix unitaire hors taxes. */
  readonly unitHt: number;
  /** Taux de TVA sous forme de fraction (0.2 pour 20 %). */
  readonly vatRate: number;
  readonly vatLabel: string;
  readonly totalHt: number;
  readonly totalVat: number;
  readonly totalTtc: number;
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
}

/** Accepte un taux exprimé en fraction (0.2) ou en points (20). */
export function normalizeRate(taux: number | null | undefined): number {
  const n = Number(taux ?? 0);
  if (!Number.isFinite(n)) return 0;
  return n > 1 ? n / 100 : n;
}

function round2(value: number): number {
  return Math.round((value + Number.EPSILON) * 100) / 100;
}

/**
 * Construit une ligne de document à partir d'un article du catalogue.
 *
 * Le calcul part systématiquement du prix HT et du taux de TVA de l'article :
 * c'est la seule base fiable pour ventiler la TVA par taux dans le pied du
 * document (le `artPriceTTC` renvoyé par l'API est déjà arrondi).
 */
export function buildLine(article: Article, quantity: number): DocumentLine {
  const unitHt = Number(article.artPriceExcludeTaxes ?? 0);
  const vatRate = normalizeRate(article.tva?.tvaTaux);
  const qty = Math.max(0, Number(quantity ?? 0));

  const totalHt = round2(unitHt * qty);
  const totalVat = round2(totalHt * vatRate);

  return {
    articleId: article.artId,
    reference: article.artReference,
    name: article.artName,
    description: article.artDescription,
    quantity: qty,
    unitHt,
    vatRate,
    vatLabel: article.tva?.tvaName ?? '—',
    totalHt,
    totalVat,
    totalTtc: round2(totalHt + totalVat),
    stock: Number(article.artStock ?? 0),
  };
}

/**
 * Agrège les lignes d'un document et ventile la TVA par taux.
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

/** Agrège une liste de lignes déjà construites. */
export function totalsOf(lines: readonly DocumentLine[]): DocumentTotals {
  const byRate = new Map<number, VatBucket>();

  let totalHt = 0;
  let totalVat = 0;
  let itemCount = 0;

  for (const line of lines) {
    totalHt += line.totalHt;
    totalVat += line.totalVat;
    itemCount += line.quantity;

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

  return {
    lines: [...lines],
    totalHt,
    totalVat,
    totalTtc: round2(totalHt + totalVat),
    buckets: [...byRate.values()].sort((a, b) => a.rate - b.rate),
    itemCount,
  };
}

/**
 * Génère une référence de document lisible.
 * Ex. : `DEV-2026-0007`.
 */
export function buildReference(prefix: string, existingRefs: readonly string[]): string {
  const year = new Date().getFullYear();
  const pattern = new RegExp(`^${prefix}-${year}-(\\d+)$`, 'i');

  const max = existingRefs.reduce((acc, ref) => {
    const match = pattern.exec((ref ?? '').trim());
    return match ? Math.max(acc, Number(match[1])) : acc;
  }, 0);

  return `${prefix}-${year}-${String(max + 1).padStart(4, '0')}`;
}
