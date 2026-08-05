/**
 * Modèles TypeScript miroirs des DTO Java du backend `saas_facturation`.
 *
 * Chaque interface correspond exactement à un `record` du package
 * `com.mns.cda.saas_facturation.DTO` afin que la (dé)sérialisation JSON soit
 * transparente. Les noms de champs sont volontairement identiques à ceux du
 * backend — ne les renommez pas côté front.
 */

/* ==================================================================
   Géographie
   ================================================================== */

/** `DTO/CountryDTO.java` */
export interface Country {
  cntId: number;
  cntName: string;
}

/** `DTO/requestDTO/CountryRequestDTO.java` */
export interface CountryRequest {
  cntName: string;
}

/** `DTO/PostalCodeDTO.java` */
export interface PostalCode {
  pCodeId: number;
  pCodeName: string;
}

/** `DTO/requestDTO/PostalCodeRequestDTO.java` */
export interface PostalCodeRequest {
  pCodeName: string;
}

/** `DTO/CityDTO.java` */
export interface City {
  cityId: number;
  cityName: string;
  country: Country | null;
}

/** `DTO/requestDTO/CityRequestDTO.java` */
export interface CityRequest {
  cityName: string;
  cntId: number;
}

/** `DTO/PostalCodeCityDTO.java` — association code postal / ville. */
export interface PostalCodeCity {
  postalCode: PostalCode;
  city: City;
}

/** `DTO/requestDTO/PostalCodeCityRequestDTO.java` */
export interface PostalCodeCityRequest {
  pCodeId: number;
  cityId: number;
}

/** `DTO/AddressDTO.java` */
export interface Address {
  addId: number;
  addNumber: string | null;
  addStreet: string | null;
  addComplement: string | null;
  postalCode: PostalCode | null;
  city: City | null;
}

/** `DTO/requestDTO/AddressRequestDTO.java` */
export interface AddressRequest {
  addNumber: string | null;
  addStreet: string | null;
  addComplement: string | null;
  pCodeId: number;
  cityId: number;
}

/* ==================================================================
   TVA
   ================================================================== */

/** `model/Tva.java` — l'entité est renvoyée telle quelle par le contrôleur. */
export interface Tva {
  tvaId: number;
  tvaName: string;
  /** Taux exprimé en fraction décimale : 0.2 signifie 20 %. */
  tvaTaux: number;
}

/** `DTO/responseDTO/TvaResponseDTO.java` */
export interface TvaResponse {
  tvaId: number;
  tvaName: string;
  tvaTaux: number;
}

/** `DTO/requestDTO/TvaRequestDTO.java` */
export interface TvaRequest {
  tvaName: string;
  tvaTaux: number;
}

/** `DTO/updateDTO/UpdateTvaTauxDTO.java` */
export interface TvaTauxUpdate {
  tvaTaux: number;
}

/* ==================================================================
   Catégories
   ================================================================== */

/** `DTO/CategoryDTO.java` — structure récursive (arbre de catégories). */
export interface Category {
  catId: number;
  catName: string;
  catSlug: string;
  catParentName: string | null;
  children: Category[] | null;
}

/** `DTO/responseDTO/CategoryResponseDTO.java` */
export interface CategoryResponse {
  catId: number;
  catName: string;
  catSlug: string;
  catParentName: string | null;
}

/** `DTO/requestDTO/CategoryRequestDTO.java` */
export interface CategoryRequest {
  catName: string;
  catSlug: string;
  parentId: number | null;
}

/* ==================================================================
   Fournisseurs & fabricants
   ================================================================== */

/** `DTO/SupplierDTO.java` */
export interface Supplier {
  splId: number;
  splName: string;
  splEmail: string;
  splPhone: string;
  address: Address | null;
}

/** `DTO/responseDTO/SupplierResponseDTO.java` */
export interface SupplierResponse {
  splId: number;
  splName: string;
}

/**
 * `DTO/requestDTO/SupplierRequestDTO.java`
 * Attention : les noms de champs diffèrent du DTO de réponse.
 */
export interface SupplierRequest {
  name: string;
  email: string;
  phoneNumber: string;
  addressId: number;
}

/** `DTO/MakerDTO.java` */
export interface Maker {
  mkrId: number;
  mkrName: string;
  mkrEmail: string;
  mkrPhone: string;
  address: Address | null;
}

/** `DTO/responseDTO/MakerResponseDTO.java` */
export interface MakerResponse {
  mkrId: number;
  mkrName: string;
}

/** `DTO/requestDTO/MakerRequestDTO.java` */
export interface MakerRequest {
  mkrName: string;
  mkrEmail: string;
  mkrPhone: string;
  addressId: number;
}

/* ==================================================================
   Articles
   ================================================================== */

/** Clé composite `SupplierReference.SupplierReferenceId`. */
export interface SupplierReferenceId {
  articleId: number;
  supplierId: number;
}

/** Clé composite `MakerReference.MakerReferenceId`. */
export interface MakerReferenceId {
  articleId: number;
  makerId: number;
}

/** Clé composite `OrderLine.OrderLineId`. */
export interface OrderLineId {
  articleId: number;
  cartId: number;
}

/** `DTO/responseDTO/SupplierReferenceResponseDTO.java` */
export interface SupplierReferenceResponse {
  splRefId: SupplierReferenceId | null;
  supplier: SupplierResponse | null;
  splRefReference: string;
  supplierPrice: number;
  splRefStock: number;
}

/** `DTO/responseDTO/MakerReferenceResponseDTO.java` */
export interface MakerReferenceResponse {
  makerReferenceId: MakerReferenceId | null;
  maker: MakerResponse | null;
  reference: string;
  artMkrStock: number;
  artMkrSellPrice: number;
}

/** `DTO/ArticleDTO.java` */
export interface Article {
  artId: number;
  artReference: string;
  artName: string;
  artDescription: string;
  artPriceExcludeTaxes: number;
  /**
   * Stock **calculé** par le serveur, non stocké : somme des quantités de
   * toutes les références fournisseur et fabricant, statut de livraison
   * ignoré. Une commande encore en attente y est donc déjà comptée.
   */
  artStock: number;
  tva: TvaResponse | null;
  artPriceTTC: number;
  artCreatedDate: string | null;
  artUpdatedDate: string | null;
  categories: CategoryResponse[] | null;
  suppliers: SupplierReferenceResponse[] | null;
  makers: MakerReferenceResponse[] | null;
}

/** `DTO/ArticleLightDTO.java` */
export interface ArticleLight {
  artId: number;
  artReference: string;
  artName: string;
  artDescription: string;
  artStock: number;
  artPriceTTC: number;
}

/** `DTO/responseDTO/ArticleResponseSupplierDTO.java` */
export interface ArticleResponseSupplier {
  artId: number;
  artReference: string;
  artName: string;
  artDescription: string;
  artPriceExcludeTaxes: number;
  artStock: number;
  tva: TvaResponse | null;
  categories: CategoryResponse[] | null;
  makers: MakerReferenceResponse[] | null;
}

/** `DTO/responseDTO/ArticleResponseMakerReferenceDTO.java` */
export interface ArticleResponseMakerReference {
  artId: number;
  artName: string;
  artReference: string;
  suppliers: SupplierReferenceResponse[] | null;
}

/* ==================================================================
   Inventaire
   ================================================================== */

/** `enumeration/DeliveryStatus.java` */
export type DeliveryStatus = 'CANCELLED' | 'RECEIVED' | 'PENDING' | 'ACCEPTED';

export const DELIVERY_STATUSES: readonly DeliveryStatus[] = [
  'PENDING',
  'ACCEPTED',
  'RECEIVED',
  'CANCELLED',
];

export const DELIVERY_STATUS_LABELS: Record<DeliveryStatus, string> = {
  PENDING: 'Commandé, en attente',
  ACCEPTED: 'Accepté par le fournisseur',
  RECEIVED: 'Reçu en dépôt',
  CANCELLED: 'Annulé',
};

/** `DTO/responseDTO/ArticleResponseInventoryDTO.java` */
export interface ArticleResponseInventory {
  artId: number;
  /**
   * Attention : `InventoryMapper` construit ce DTO avec
   * `(artId, artName, artReference)` alors que le record déclare
   * `(artId, artReference, artName)`. Les deux valeurs arrivent donc
   * **inversées**. Voir `backend-patch/Inventory-blocages.patch`, section 7.
   */
  artReference: string;
  artName: string;
}

/**
 * `DTO/InventoryDTO.java` — un relevé de stock physique, daté.
 *
 * C'est le point d'ancrage du calcul : on constate une quantité réelle à un
 * instant donné, puis on ajoute les entrées postérieures pour obtenir le stock
 * théorique d'aujourd'hui.
 */
export interface Inventory {
  invId: number;
  /**
   * Date du relevé. **Null tant que le correctif backend n'est pas appliqué** :
   * `@CreatedDate` reste inerte sur `Inventory`, faute d'`@EntityListeners`.
   */
  invDate: string | null;
  invStock: number;
  article: ArticleResponseInventory | null;
}

/** `DTO/requestDTO/InventoryRequestDTO.java` */
export interface InventoryRequest {
  invStock: number;
  articleId: number;
}

/**
 * `DTO/requestDTO/ArticleRequestDTO.java` (création).
 *
 * `artStock` a disparu : le stock n'est plus une valeur saisie. Il est
 * désormais **calculé** par le serveur à partir des références fournisseur et
 * fabricant, et il ne se renseigne donc plus à la main.
 */
export interface ArticleRequest {
  artReference: string;
  artName: string;
  artDescription: string;
  artPriceExcludeTaxes: number;
  tvaId: number;
  categoryIds: number[];
  suppliers: SupplierReferenceRequest[];
}

/**
 * `DTO/updateDTO/ArticleUpdateDTO.java` (modification — sans `suppliers`).
 *
 * `invIds` rattache des relevés d'inventaire à l'article. Le champ est
 * **déréférencé sans contrôle** côté serveur : l'omettre, ou l'envoyer à
 * `null`, provoque une `NullPointerException`. On transmet donc toujours un
 * tableau, fût-il vide.
 */
export interface ArticleUpdate {
  artReference: string;
  artName: string;
  artDescription: string;
  artPriceExcludeTaxes: number;
  tvaId: number;
  categoryIds: number[];
  invIds: number[];
}

/* ==================================================================
   Références fournisseur / fabricant
   ================================================================== */

/** `DTO/SupplierReferenceDTO.java` */
export interface SupplierReference {
  article: ArticleResponseSupplier | null;
  supplier: SupplierResponse | null;
  splRefReference: string;
  supplierPrice: number;
  splRefStock: number;
}

/** `DTO/requestDTO/SupplierReferenceRequestDTO.java` */
/**
 * Le statut de livraison est **obligatoire en écriture** (`@NotNull`), mais
 * absent des DTO de réponse. Le front doit donc le fournir sans jamais pouvoir
 * relire celui qui est en base : à la modification, la valeur transmise écrase
 * l'existante. Voir `backend-patch/Inventory-blocages.patch`, section 4.
 */
export interface SupplierReferenceRequest {
  articleId: number;
  supplierId: number;
  splRefReference: string;
  splRefSellPrice: number;
  splRefStock: number;
  status: DeliveryStatus;
}

/** `DTO/updateDTO/UpdateSupplierReferenceDTO.java` */
export interface SupplierReferenceUpdate {
  splRefReference: string;
  splRefSellPrice: number;
  splRefStock: number;
  status: DeliveryStatus;
}

/** `DTO/MakerReferenceDTO.java` */
export interface MakerReference {
  article: ArticleResponseMakerReference | null;
  maker: MakerResponse | null;
  reference: string;
  artMkrStock: number;
  artMkrSellPrice: number;
}

/** `DTO/requestDTO/MakerReferenceRequestDTO.java` — voir la note sur `status` ci-dessus. */
export interface MakerReferenceRequest {
  artId: number;
  mkrId: number;
  artMkrReference: string;
  artMkrStock: number;
  artMkrSellPrice: number;
  status: DeliveryStatus;
}

/** `DTO/updateDTO/UpdateMakerReferenceDTO.java` */
export interface MakerReferenceUpdate {
  reference: string;
  artMkrStock: number;
  artMkrSellPrice: number;
  status: DeliveryStatus;
}

/* ==================================================================
   Devis émis (Quote / QuoteLine)
   ================================================================== */

/** `enumeration/QuoteStatus.java` */
export type QuoteStatus =
  | 'CREATED'
  | 'PENDING'
  | 'ACCEPTED'
  | 'REJECTED'
  | 'EXPIRED'
  | 'CLOSED'
  | 'REVISITED';

export const QUOTE_STATUSES: readonly QuoteStatus[] = [
  'CREATED',
  'PENDING',
  'ACCEPTED',
  'REJECTED',
  'EXPIRED',
  'CLOSED',
  'REVISITED',
];

export const QUOTE_STATUS_LABELS: Record<QuoteStatus, string> = {
  CREATED: 'Brouillon, non transmis',
  PENDING: 'En attente de réponse',
  ACCEPTED: 'Accepté',
  REJECTED: 'Refusé',
  EXPIRED: 'Expiré',
  CLOSED: 'Clos',
  REVISITED: 'Remplacé par une révision',
};

/**
 * Un devis n'est librement modifiable que tant qu'il n'a pas quitté la maison.
 *
 * `CREATED` est le brouillon : il existe en base dès que le document passe au
 * statut Devis, mais rien n'a encore été transmis. Dès `PENDING`, le client
 * détient un exemplaire portant un numéro et un montant — le modifier ferait
 * diverger les deux copies. Toute évolution passe alors par une révision.
 */
export function isQuoteEditable(status: QuoteStatus): boolean {
  return status === 'CREATED';
}

/** Un brouillon peut être transmis ; rien d'autre ne le peut. */
export function isQuoteSendable(status: QuoteStatus): boolean {
  return status === 'CREATED';
}

/**
 * `DTO/QuoteLineDTO.java` — une ligne **figée** au moment de l'émission.
 *
 * Tout y est recopié plutôt que référencé : désignation, référence, prix HT et
 * surtout taux de TVA. C'est ce qui garantit qu'un devis transmis ne se
 * déforme pas quand le catalogue évolue — un article renommé, un prix revu ou
 * un taux qui passe de 5,5 % à 10 % ne réécrivent pas le passé.
 */
export interface QuoteLine {
  qotLnId: number;
  qotLnQuantity: number;
  qotLnPriceHT: number;
  articleName: string;
  articleRef: string;
  /** Taux figé, exprimé en décimal (0.055 pour 5,5 %). */
  tvaRate: number;
  totalHT: number;
  totalTVA: number;
  totalTTC: number;
}

/**
 * `DTO/QuoteDTO.java` — un devis émis.
 *
 * L'identifiant est exposé sous le nom `quoteId`. L'interface reste tolérante à
 * son absence — les actions de mutation se désactivent alors d'elles-mêmes
 * plutôt que d'échouer à l'appel.
 */
export interface Quote {
  /**
   * Nommé `quoteId` côté serveur, et non `qotId` comme les autres champs du
   * record. Reste optionnel : le champ a été ajouté récemment, une réponse qui
   * ne le porterait pas ne doit pas casser l'affichage.
   */
  quoteId?: number | null;
  qotNumber: string;
  qotCreatedDate: string | null;
  expirationDate: string | null;
  qotStatus: QuoteStatus;
  /** Devis remplacé par celui-ci. Récursif : porte lui-même son parent. */
  qotParent: Quote | null;
  /** Identifiant du panier d'origine — remplace l'ancienne `cartRef`. */
  cartId: number | null;
  qotLines: QuoteLine[] | null;
  /**
   * Totaux renvoyés par l'API. Attention : `QuoteMapper` y **ajoute ceux du
   * parent** quand le devis en a un. Selon que la révision reprend toutes les
   * lignes ou seulement les ajouts, ce cumul est juste ou double le montant.
   * `ownTotals()` recalcule le total des seules lignes du devis, pour permettre
   * la comparaison.
   */
  totalHT: number;
  totalTva: number;
  totalTTC: number;
}

/** `DTO/requestDTO/QuoteRequestDTO.java` */
export interface QuoteRequest {
  qotNumber: string;
  /** Format `YYYY-MM-DD` : le backend attend une `LocalDate`. */
  qotExpirationDate: string;
  qotStatus: QuoteStatus;
  qotParentId: number | null;
  cartId: number;
}

/** `DTO/updateDTO/PatchQuoteLineQuantity.java` */
export interface QuoteLineQuantityPatch {
  artRef: string;
  qotLineQuantity: number;
}

/* ==================================================================
   Commandes (Command)
   ================================================================== */

/**
 * `enumeration/CommandStatus.java`
 *
 * Attention : `CANCELED` s'écrit ici avec un seul « l », là où
 * `InvoiceStatus.CANCELLED` en prend deux. C'est le backend qui en décide, on
 * s'y conforme — une faute de frappe côté front produirait un 400.
 */
export type CommandStatus = 'CREATED' | 'PENDING' | 'CANCELED' | 'ACCEPTED' | 'DELIVERED';

export const COMMAND_STATUS_LABELS: Record<CommandStatus, string> = {
  CREATED: 'Créée',
  PENDING: 'En attente',
  CANCELED: 'Annulée',
  ACCEPTED: 'Acceptée',
  DELIVERED: 'Livrée',
};

/**
 * `DTO/CommandDTO.java` — le bon de commande issu d'un devis accepté.
 *
 * La commande n'a pas de lignes propres : elle reprend telles quelles celles
 * du devis (`quoteLines`), déjà figées. Elle porte en revanche le lien vers
 * son devis d'origine (`quoteId`), ce qui permet de remonter jusqu'au panier
 * puis au client.
 */
export interface Command {
  cmdId: number;
  /** Nommé `cmdCreatedDate` dans le DTO, `cmdCreateDate` dans l'entité. Le JSON porte le premier. */
  cmdCreatedDate: string | null;
  cmdModifiedDate: string | null;
  cmdStatus: CommandStatus;
  quoteId: number | null;
  quoteNumber: string | null;
  quoteLines: QuoteLine[] | null;
}

/** `DTO/requestDTO/CommandRequestDTO.java` — le champ s'appelle `qotId`, pas `quoteId`. */
export interface CommandRequest {
  qotId: number;
}

/** `DTO/requestDTO/PatchCommandStatus.java` — l'id voyage dans le corps, pas dans l'URL. */
export interface CommandStatusPatch {
  cmdId: number;
  cmdStatus: CommandStatus;
}

/* ==================================================================
   Factures (Invoice / InvoiceLine)
   ================================================================== */

/** `enumeration/InvoiceStatus.java` — `CANCELLED` prend deux « l » (cf. `CommandStatus`). */
export type InvoiceStatus =
  | 'CREATED'
  | 'ISSUED'
  | 'SENT'
  | 'OVERDUE'
  | 'PARTIALLY_PAID'
  | 'PAID'
  | 'CANCELLED';

export const INVOICE_STATUS_LABELS: Record<InvoiceStatus, string> = {
  CREATED: 'Créée',
  ISSUED: 'Émise',
  SENT: 'Transmise',
  OVERDUE: 'En retard',
  PARTIALLY_PAID: 'Partiellement réglée',
  PAID: 'Réglée',
  CANCELLED: 'Annulée',
};

/**
 * `DTO/InvoiceLineDTO.java` — ligne de facture, figée à l'émission.
 *
 * Même principe que `QuoteLine` : tout est recopié (désignation, référence,
 * prix, taux de TVA) plutôt que référencé, pour qu'une facture reste lisible
 * à l'identique quelles que soient les évolutions ultérieures du catalogue.
 */
export interface InvoiceLine {
  invLnId: number;
  invLnQuantity: number;
  invLnPriceHT: number;
  articleName: string;
  articleRef: string;
  /** Taux figé, exprimé en décimal (0.055 pour 5,5 %). */
  tvaRate: number;
  totalHT: number;
  totalTVA: number;
  totalTTC: number;
}

/**
 * `DTO/InvoiceDTO.java` — la facture émise.
 *
 * Limite connue : ce DTO **n'expose aucun lien vers la commande d'origine**
 * (pas de `commandId`). Impossible donc, à partir d'une facture seule, de
 * remonter à sa commande, à son devis ou à son client. Tant que le backend
 * ne le porte pas, le rapprochement se fait par `invoiceNumber`, dérivé de la
 * référence du document — voir `CommercialChainService`.
 */
export interface Invoice {
  invoiceId: number;
  invoiceNumber: string;
  invoiceCreatedDate: string | null;
  /** Chemin du PDF côté serveur. Actuellement une valeur de remplissage. */
  invoicePathPDF: string | null;
  invoiceStatus: InvoiceStatus;
  invoiceLines: InvoiceLine[] | null;
}

/** `DTO/requestDTO/InvoiceRequestDTO.java` — le numéro est à la charge du client, et unique en base. */
export interface InvoiceRequest {
  invoiceNumber: string;
  commandId: number;
}

/** `DTO/requestDTO/PatchInvoiceStatus.java` — l'id voyage dans le corps, pas dans l'URL. */
export interface InvoiceStatusPatch {
  invoiceId: number;
  invoiceStatus: InvoiceStatus;
}

/* ==================================================================
   Clients
   ================================================================== */

/** `DTO/AccountTypeDTO.java` */
export interface AccountType {
  accTypeId: number;
  accTypeLibelle: string;
}

/** `DTO/responseDTO/AccountTypeResponseDTO.java` */
export interface AccountTypeResponse {
  accTypeLibelle: string;
}

/** `DTO/requestDTO/AccountTypeRequestDTO.java` */
export interface AccountTypeRequest {
  accTypeLibelle: string;
}

/** `DTO/responseDTO/CustomerResponseDTO.java` */
export interface CustomerResponse {
  ctmId: number;
  ctmFirstName: string;
  ctmLastName: string;
  ctmEmail: string;
  ctmPhone: string;
  address: Address | null;
  accountType: AccountTypeResponse | null;
}

/** `DTO/CustomerDTO.java` */
export interface Customer {
  ctmId: number;
  ctmFirstName: string;
  ctmLastName: string;
  ctmEmail: string;
  ctmPhone: string;
  address: Address | null;
  accountType: AccountTypeResponse | null;
  /** Comptes rattachés (relation « Own » du MCD : super-utilisateur). */
  customers: CustomerResponse[] | null;
}

/** `DTO/requestDTO/CustomerRequestDTO.java` */
export interface CustomerRequest {
  ctmFirstName: string;
  ctmLastName: string;
  ctmEmail: string;
  ctmPhone: string;
  addId: number;
  accTypeId: number;
  customerIds: number[];
}

/* ==================================================================
   Paniers / devis / factures
   ================================================================== */

/** `DTO/responseDTO/OrderLineResponseDTO.java` */
export interface OrderLineResponse {
  id: OrderLineId;
  quantity: number;
}

/** `DTO/responseDTO/CartResponseDTO.java` */
export interface CartResponse {
  crtId: number;
  crtRef: string;
  crtStatus: string;
  orderLines: OrderLineResponse[] | null;
}

/** `DTO/OrderLineDTO.java` */
export interface OrderLine {
  id: OrderLineId;
  quantity: number;
  article: ArticleLight | null;
  cart: CartResponse | null;
}

/** `DTO/requestDTO/OrderLineRequestDTO.java` */
export interface OrderLineRequest {
  cartId: number;
  articleId: number;
  quantity: number;
}

/** `DTO/updateDTO/UpdateOrderLineDTO.java` */
export interface OrderLineUpdate {
  quantity: number;
}

/** `DTO/CartDTO.java` */
export interface Cart {
  crtId: number;
  crtRef: string;
  crtCreateDate: string | null;
  crtLastModifieDate: string | null;
  crtStatus: string;
  customer: Customer | null;
  orderLines: OrderLineResponse[] | null;
}

/** `DTO/requestDTO/CartRequestDTO.java` */
export interface CartRequest {
  crtRef: string;
  crtStatus: string;
  ctmId: number;
  orderLines: OrderLineRequest[];
}

/* ==================================================================
   Erreurs
   ================================================================== */

/** `DTO/GlobalExceptionInterceptorDTO.java` */
export interface ApiError {
  status: number;
  error: string;
  message: string;
}
