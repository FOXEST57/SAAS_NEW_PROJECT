/**
 * Jetons de couleur des visualisations.
 *
 * Ces valeurs ne sont pas choisies à l'œil : elles ont été passées au
 * validateur de palette (bande de clarté OKLCH, plancher de chroma, séparation
 * sous deutéranopie et protanopie, contraste sur la surface). Chaque
 * modification doit être revalidée.
 *
 * Résultats retenus :
 *
 *   Catégoriel 2 séries (Climatisation / Chauffage)
 *     clair  #2579eb / #ef6008  sur #ffffff → tous les contrôles passent
 *     sombre #3987e5 / #d95926  sur #22272f → tous les contrôles passent
 *     Séparation la plus faible : ΔE 26,8 sous protanopie (cible ≥ 8).
 *
 *   Ordinal 4 étapes (entonnoir Devis → Commande → Facture → Payée)
 *     clair  #86b6ef #5598e7 #256abf #104281 → extrémité claire à 2,11:1
 *     sombre #b7d3f6 #86b6ef #3987e5 #256abf → extrémité sombre à 2,78:1
 *
 * La surface des graphiques en thème sombre est volontairement plus foncée que
 * celle des cartes (#22272f au lieu de #333b48) : sur la surface des cartes,
 * l'orange tombait à 2,91:1, sous le seuil de 3:1.
 */

/** Deux séries catégorielles, identité produit. */
export const SERIES = {
  clim: { light: '#2579eb', dark: '#3987e5' },
  chauffage: { light: '#ef6008', dark: '#d95926' },
  autre: { light: '#667894', dark: '#8596ae' },
} as const;

/** Rampe ordinale du pipeline, du plus clair au plus foncé. */
export const FUNNEL_RAMP = {
  light: ['#86b6ef', '#5598e7', '#256abf', '#104281'],
  dark: ['#b7d3f6', '#86b6ef', '#3987e5', '#256abf'],
} as const;

/** Surface sur laquelle les marques sont validées. */
export const VIZ_SURFACE = { light: '#ffffff', dark: '#22272f' } as const;

/**
 * Couleurs d'état, réservées : jamais réutilisées comme couleur de série.
 * Toujours accompagnées d'une icône et d'un libellé, jamais seules.
 */
export const STATUS_COLORS = {
  good: { light: '#047857', dark: '#34d399' },
  warning: { light: '#b45309', dark: '#fbbf24' },
  critical: { light: '#b91c1c', dark: '#f87171' },
} as const;

export type VizMode = 'light' | 'dark';
