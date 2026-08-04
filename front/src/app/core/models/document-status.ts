/**
 * Cycle de vie d'un document commercial.
 *
 * Depuis le 4 août 2026, le backend possède de vraies entités `Quote`,
 * `Command`, `Invoice` (+ `InvoiceLine`, prix figé) en plus du `Cart`
 * d'origine. `DocumentStatus` reste le vocabulaire métier à 6 valeurs utilisé
 * par tout le front (badges, colonnes du pipeline, entonnoir) : c'est une
 * **étape de présentation**, pas un champ persisté. Chaque entité y est
 * projetée par les fonctions `quoteBucket` / `commandBucket` /
 * `invoiceBucket` ci-dessous, à partir de son statut réel.
 *
 *   PANIER ──▶ DEVIS ──▶ COMMANDE ──▶ FACTURE ──▶ PAYEE
 *      └─────────┴──────────┴───────────┴──────▶ ANNULE
 *
 * Le passage d'une étape à l'autre n'est plus un changement de champ : c'est
 * la création d'une entité (`POST /quote`, `POST /command`, `POST /invoice`).
 * Un `Cart` qui a déjà engendré un `Quote` n'est donc plus affiché comme
 * PANIER — voir `CommerceStore` pour la composition du portefeuille de
 * documents à partir des quatre sources.
 *
 * La COMMANDE matérialise le devis accepté : l'engagement du client est pris,
 * la pose peut être planifiée, mais la facture n'est pas encore émise. C'est
 * l'étape où le stock est réputé engagé.
 */

import type { CommandStatus, InvoiceStatus, QuoteStatus } from './api.models';

export type DocumentKind = 'cart' | 'quote' | 'command' | 'invoice';

export type DocumentStatus =
  | 'PANIER'
  | 'DEVIS'
  | 'COMMANDE'
  | 'FACTURE'
  | 'PAYEE'
  | 'ANNULE';

export interface DocumentStatusMeta {
  /** Valeur persistée dans `crtStatus`. */
  readonly value: DocumentStatus;
  /** Libellé court affiché à l'utilisateur. */
  readonly label: string;
  /** Nom du document une fois ce statut atteint. */
  readonly docLabel: string;
  /** Classe CSS du badge correspondant. */
  readonly badgeClass: string;
  /** Statuts atteignables depuis celui-ci. */
  readonly next: readonly DocumentStatus[];
  /** Le contenu (lignes, client) est-il encore modifiable ? */
  readonly editable: boolean;
  /** Préfixe de référence propre à l'étape. */
  readonly prefix: string;
  /** Icône associée. */
  readonly icon: string;
  readonly description: string;
  /**
   * Position dans l'entonnoir commercial (`null` pour les états hors flux).
   * Sert à ordonner les colonnes du pipeline et les étapes de l'entonnoir.
   */
  readonly stage: number | null;
  /** Le document compte-t-il dans le chiffre d'affaires réalisé ? */
  readonly isRevenue: boolean;
  /** Le document est-il un engagement ferme du client ? */
  readonly isCommitted: boolean;
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
    icon: 'cart',
    description: 'Brouillon en cours de saisie. Les lignes sont librement modifiables.',
    stage: 0,
    isRevenue: false,
    isCommitted: false,
  },
  DEVIS: {
    value: 'DEVIS',
    label: 'Devis',
    docLabel: 'Devis',
    badgeClass: 'badge-brand',
    next: ['COMMANDE', 'PANIER', 'ANNULE'],
    editable: true,
    prefix: 'DEV',
    icon: 'file',
    description: 'Proposition transmise au client, en attente de son acceptation.',
    stage: 1,
    isRevenue: false,
    isCommitted: false,
  },
  COMMANDE: {
    value: 'COMMANDE',
    label: 'Commande',
    docLabel: 'Bon de commande',
    badgeClass: 'badge-violet',
    next: ['FACTURE'],
    // La Command n'a pas de lignes propres (elle reprend celles du Quote) et
    // n'expose qu'un statut de suivi logistique : plus rien n'est éditable à
    // ce stade côté front.
    editable: false,
    prefix: 'CDE',
    icon: 'clipboard',
    description: 'Devis accepté par le client. Pose à planifier, stock engagé.',
    stage: 2,
    isRevenue: false,
    isCommitted: true,
  },
  FACTURE: {
    value: 'FACTURE',
    label: 'Facture',
    docLabel: 'Facture',
    badgeClass: 'badge-warn',
    next: ['PAYEE', 'ANNULE'],
    editable: false,
    prefix: 'FAC',
    icon: 'invoice',
    description: 'Document comptable émis. Le contenu est verrouillé.',
    stage: 3,
    isRevenue: true,
    isCommitted: true,
  },
  PAYEE: {
    value: 'PAYEE',
    label: 'Payée',
    docLabel: 'Facture',
    badgeClass: 'badge-success',
    next: [],
    editable: false,
    prefix: 'FAC',
    icon: 'checkCircle',
    description: 'Facture réglée par le client.',
    stage: 4,
    isRevenue: true,
    isCommitted: true,
  },
  ANNULE: {
    value: 'ANNULE',
    label: 'Annulé',
    docLabel: 'Document annulé',
    badgeClass: 'badge-danger',
    next: ['PANIER'],
    editable: false,
    prefix: 'ANN',
    icon: 'close',
    description: 'Document abandonné ou annulé.',
    stage: null,
    isRevenue: false,
    isCommitted: false,
  },
};

export const DOCUMENT_STATUS_LIST: readonly DocumentStatusMeta[] = [
  DOCUMENT_STATUSES.PANIER,
  DOCUMENT_STATUSES.DEVIS,
  DOCUMENT_STATUSES.COMMANDE,
  DOCUMENT_STATUSES.FACTURE,
  DOCUMENT_STATUSES.PAYEE,
  DOCUMENT_STATUSES.ANNULE,
];

/** Colonnes du pipeline commercial, dans l'ordre du flux. */
export const PIPELINE_STAGES: readonly DocumentStatus[] = [
  'PANIER',
  'DEVIS',
  'COMMANDE',
  'FACTURE',
  'PAYEE',
];

/** Étapes retenues pour l'entonnoir (le panier n'est pas encore commercial). */
export const FUNNEL_STAGES: readonly DocumentStatus[] = [
  'DEVIS',
  'COMMANDE',
  'FACTURE',
  'PAYEE',
];

/** Correspondance des statuts hérités du jeu de données initial. */
const LEGACY_ALIASES: Record<string, DocumentStatus> = {
  OPEN: 'PANIER',
  DRAFT: 'PANIER',
  CART: 'PANIER',
  QUOTE: 'DEVIS',
  ORDER: 'COMMANDE',
  ORDERED: 'COMMANDE',
  VALIDATED: 'COMMANDE',
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

/**
 * Renumérote une référence lors d'un changement d'étape :
 * `DEV-2026-0007` devient `CDE-2026-0007`, en conservant le millésime et le
 * numéro d'ordre. Une référence qui ne suit pas la convention est laissée
 * intacte — mieux vaut une référence inattendue qu'une référence perdue.
 */
export function reprefixReference(reference: string, next: DocumentStatus): string {
  const prefix = DOCUMENT_STATUSES[next].prefix;
  const match = /^[A-Za-z]{2,4}-(\d{4})-(\d+)$/.exec((reference ?? '').trim());
  if (!match) return reference;
  return `${prefix}-${match[1]}-${match[2]}`;
}

/** Libellé de l'action de transition, du point de vue de l'utilisateur. */
export function transitionLabel(next: DocumentStatus): string {
  return {
    PANIER: 'Repasser en panier',
    DEVIS: 'Transformer en devis',
    COMMANDE: 'Valider la commande',
    FACTURE: 'Facturer',
    PAYEE: 'Marquer comme payée',
    ANNULE: 'Annuler le document',
  }[next];
}

/* ==================================================================
   Projection des statuts réels (Quote / Command / Invoice) sur l'étape
   métier affichée par le front.
   ================================================================== */

/**
 * Un devis clos négativement (refusé, expiré, révisé) n'a plus sa place dans
 * le pipeline actif : il est présenté comme annulé plutôt que comme un
 * « devis » qui n'avancera plus.
 */
export function quoteBucket(status: QuoteStatus): DocumentStatus {
  if (status === 'REJECTED' || status === 'EXPIRED' || status === 'CLOSED') return 'ANNULE';
  return 'DEVIS';
}

/**
 * `CommandStatus` ne porte que du suivi logistique (créée, en attente,
 * acceptée, livrée) : aucune valeur ne sort une commande du pipeline avant
 * qu'elle soit facturée.
 */
export function commandBucket(_status: CommandStatus): DocumentStatus {
  return 'COMMANDE';
}

export function invoiceBucket(status: InvoiceStatus): DocumentStatus {
  if (status === 'PAID') return 'PAYEE';
  if (status === 'CANCELLED') return 'ANNULE';
  return 'FACTURE';
}
