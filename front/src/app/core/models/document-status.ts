/**
 * Cycle de vie d'un document commercial.
 *
 * Le backend ne possède pas d'entités `Devis` / `Facture` : un `Cart` porte un
 * champ texte libre `crtStatus`. Ce module définit le vocabulaire métier utilisé
 * par le front et normalise les statuts historiques présents dans `data.sql`
 * (`OPEN`, `VALIDATED`, `ABANDONED`).
 *
 * Transition métier :
 *   PANIER  ──▶  DEVIS  ──▶  FACTURE  ──▶  PAYEE
 *      └──────────┴────────────┴──────────▶  ANNULE
 */

export type DocumentStatus = 'PANIER' | 'DEVIS' | 'FACTURE' | 'PAYEE' | 'ANNULE';

export interface DocumentStatusMeta {
  /** Valeur persistée dans `crtStatus`. */
  readonly value: DocumentStatus;
  /** Libellé affiché à l'utilisateur. */
  readonly label: string;
  /** Nom du document une fois ce statut atteint. */
  readonly docLabel: string;
  /** Classe CSS du badge correspondant. */
  readonly badgeClass: string;
  /** Statuts atteignables depuis celui-ci. */
  readonly next: readonly DocumentStatus[];
  /** Le contenu (lignes, client) est-il encore modifiable ? */
  readonly editable: boolean;
  /** Préfixe de référence suggéré. */
  readonly prefix: string;
  readonly description: string;
}

export const DOCUMENT_STATUSES: Record<DocumentStatus, DocumentStatusMeta> = {
  PANIER: {
    value: 'PANIER',
    label: 'Panier',
    docLabel: 'Panier',
    badgeClass: 'badge-neutral',
    next: ['DEVIS', 'ANNULE'],
    editable: true,
    prefix: 'PAN',
    description: 'Brouillon en cours de saisie. Les lignes sont librement modifiables.',
  },
  DEVIS: {
    value: 'DEVIS',
    label: 'Devis',
    docLabel: 'Devis',
    badgeClass: 'badge-brand',
    next: ['FACTURE', 'PANIER', 'ANNULE'],
    editable: true,
    prefix: 'DEV',
    description: 'Proposition commerciale transmise au client, en attente d’acceptation.',
  },
  FACTURE: {
    value: 'FACTURE',
    label: 'Facture',
    docLabel: 'Facture',
    badgeClass: 'badge-warn',
    next: ['PAYEE', 'ANNULE'],
    editable: false,
    prefix: 'FAC',
    description: 'Document comptable émis. Le contenu est verrouillé.',
  },
  PAYEE: {
    value: 'PAYEE',
    label: 'Payée',
    docLabel: 'Facture',
    badgeClass: 'badge-success',
    next: [],
    editable: false,
    prefix: 'FAC',
    description: 'Facture réglée par le client.',
  },
  ANNULE: {
    value: 'ANNULE',
    label: 'Annulé',
    docLabel: 'Document annulé',
    badgeClass: 'badge-danger',
    next: ['PANIER'],
    editable: false,
    prefix: 'ANN',
    description: 'Document abandonné ou annulé.',
  },
};

export const DOCUMENT_STATUS_LIST: readonly DocumentStatusMeta[] = [
  DOCUMENT_STATUSES.PANIER,
  DOCUMENT_STATUSES.DEVIS,
  DOCUMENT_STATUSES.FACTURE,
  DOCUMENT_STATUSES.PAYEE,
  DOCUMENT_STATUSES.ANNULE,
];

/** Correspondance des statuts hérités du jeu de données initial. */
const LEGACY_ALIASES: Record<string, DocumentStatus> = {
  OPEN: 'PANIER',
  DRAFT: 'PANIER',
  CART: 'PANIER',
  QUOTE: 'DEVIS',
  VALIDATED: 'FACTURE',
  INVOICED: 'FACTURE',
  PAID: 'PAYEE',
  ABANDONED: 'ANNULE',
  CANCELLED: 'ANNULE',
  CANCELED: 'ANNULE',
};

/** Convertit une valeur brute de `crtStatus` en statut métier connu. */
export function normalizeStatus(raw: string | null | undefined): DocumentStatus {
  if (!raw) return 'PANIER';
  const key = raw.trim().toUpperCase();
  if (key in DOCUMENT_STATUSES) return key as DocumentStatus;
  return LEGACY_ALIASES[key] ?? 'PANIER';
}

export function statusMeta(raw: string | null | undefined): DocumentStatusMeta {
  return DOCUMENT_STATUSES[normalizeStatus(raw)];
}

/** Un document au statut FACTURE ou PAYEE ne doit plus être modifié. */
export function isLocked(raw: string | null | undefined): boolean {
  return !statusMeta(raw).editable;
}
