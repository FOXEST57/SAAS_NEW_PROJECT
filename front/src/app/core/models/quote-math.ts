import { Quote, QuoteLine } from './api.models';

/**
 * Lecture d'un devis émis : totaux, numérotation, chaîne de révisions.
 *
 * Le devis est un document figé. Tout ce qui est calculé ici l'est à partir de
 * ses propres lignes, jamais du catalogue courant — c'est précisément l'intérêt
 * du `QuoteLine` : ce qui a été proposé au client reste lisible tel quel, des
 * années plus tard.
 */

export interface QuoteTotals {
  readonly totalHT: number;
  readonly totalTVA: number;
  readonly totalTTC: number;
  readonly lineCount: number;
  readonly itemCount: number;
}

/**
 * Totaux des **seules lignes du devis**.
 *
 * À distinguer des champs `totalHT` / `totalTva` / `totalTTC` de la réponse,
 * auxquels `QuoteMapper` ajoute les totaux du devis parent. Selon que la
 * révision reprend toutes les lignes ou seulement les ajouts, ce cumul est
 * légitime ou double le montant — l'interface montre les deux plutôt que de
 * trancher à la place du serveur.
 */
export function ownTotals(lines: readonly QuoteLine[] | null | undefined): QuoteTotals {
  const list = lines ?? [];
  let totalHT = 0;
  let totalTVA = 0;
  let itemCount = 0;

  for (const line of list) {
    const ht = Number(line.qotLnPriceHT ?? 0) * Number(line.qotLnQuantity ?? 0);
    totalHT += ht;
    totalTVA += ht * Number(line.tvaRate ?? 0);
    itemCount += Number(line.qotLnQuantity ?? 0);
  }

  return {
    totalHT: round(totalHT),
    totalTVA: round(totalTVA),
    totalTTC: round(totalHT + totalTVA),
    lineCount: list.length,
    itemCount,
  };
}

/** Écart entre le total annoncé par l'API et celui des lignes du devis. */
export function totalsGap(quote: Quote): number {
  return round(Number(quote.totalTTC ?? 0) - ownTotals(quote.qotLines).totalTTC);
}

/* ------------------------------------------------------------------ */
/* Chaîne de révisions                                                 */
/* ------------------------------------------------------------------ */

/**
 * Remonte la chaîne des parents, du plus récent au plus ancien.
 *
 * Le backend n'interdit pas qu'un devis descende de lui-même. Une boucle
 * infinie ferait tomber l'interface : on borne donc la remontée et on s'arrête
 * dès qu'un numéro se répète.
 */
export function ancestry(quote: Quote): Quote[] {
  const chain: Quote[] = [];
  const seen = new Set<string>([quote.qotNumber]);
  let current = quote.qotParent;

  while (current && chain.length < 50) {
    if (seen.has(current.qotNumber)) break;
    seen.add(current.qotNumber);
    chain.push(current);
    current = current.qotParent;
  }

  return chain;
}

/** Numéro de la première version de la chaîne, celle qui donne son nom au dossier. */
export function rootNumber(quote: Quote): string {
  const chain = ancestry(quote);
  return chain.length > 0 ? chain[chain.length - 1].qotNumber : quote.qotNumber;
}

/* ------------------------------------------------------------------ */
/* Numérotation                                                        */
/* ------------------------------------------------------------------ */

/**
 * Numérotation d'un premier devis : `DEV-2026-0001`.
 *
 * Le numéro est à la charge du client — `qotNumber` est `@NotBlank` et unique
 * en base. On repart donc du plus grand numéro connu de l'année. Deux
 * utilisateurs qui émettraient au même instant pourraient entrer en collision ;
 * un compteur côté serveur lèverait ce risque.
 */
export function nextQuoteNumber(existing: readonly Quote[], year: number): string {
  const pattern = new RegExp(`^DEV-${year}-(\\d{4})`, 'i');

  const max = existing.reduce((acc, quote) => {
    const match = pattern.exec((quote.qotNumber ?? '').trim());
    return match ? Math.max(acc, Number(match[1])) : acc;
  }, 0);

  return `DEV-${year}-${String(max + 1).padStart(4, '0')}`;
}

/**
 * Numéro de la révision suivante : `DEV-2026-0001` → `-B` → `-C`…
 *
 * C'est la convention professionnelle de l'indice : le dossier garde son
 * numéro, la lettre dit quelle version fait foi. Le client retrouve ainsi son
 * devis d'origine tout en voyant qu'une version plus récente existe.
 */
export function nextRevisionNumber(quote: Quote, existing: readonly Quote[]): string {
  const base = rootNumber(quote).replace(/-[A-Z]$/i, '');
  const pattern = new RegExp(`^${escapeRegExp(base)}(?:-([A-Z]))?$`, 'i');

  // 'A' est implicite : la version d'origine ne porte pas d'indice.
  let highest = 'A';
  for (const candidate of existing) {
    const match = pattern.exec((candidate.qotNumber ?? '').trim());
    if (!match) continue;
    const letter = (match[1] ?? 'A').toUpperCase();
    if (letter > highest) highest = letter;
  }

  const next = String.fromCharCode(highest.charCodeAt(0) + 1);
  return `${base}-${next}`;
}

/** Validité par défaut d'un devis : trois mois, usage courant du bâtiment. */
export function defaultExpiration(from: Date = new Date()): string {
  const date = new Date(from);
  date.setMonth(date.getMonth() + 3);
  return date.toISOString().slice(0, 10);
}

export function isExpired(quote: Quote, now: Date = new Date()): boolean {
  if (!quote.expirationDate) return false;
  return new Date(quote.expirationDate).getTime() < now.getTime();
}

function round(value: number): number {
  return Math.round((value + Number.EPSILON) * 100) / 100;
}

function escapeRegExp(value: string): string {
  return value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}
