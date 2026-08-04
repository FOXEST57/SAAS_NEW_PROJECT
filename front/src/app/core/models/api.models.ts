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

/** `DTO/requestDTO/ArticleRequestDTO.java` (création). */
export interface ArticleRequest {
  artReference: string;
  artName: string;
  artDescription: string;
  artPriceExcludeTaxes: number;
  artStock: number;
  tvaId: number;
  categoryIds: number[];
  suppliers: SupplierReferenceRequest[];
}

/** `DTO/updateDTO/ArticleUpdateDTO.java` (modification — sans `suppliers`). */
export interface ArticleUpdate {
  artReference: string;
  artName: string;
  artDescription: string;
  artPriceExcludeTaxes: number;
  artStock: number;
  tvaId: number;
  categoryIds: number[];
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
export interface SupplierReferenceRequest {
  articleId: number;
  supplierId: number;
  splRefReference: string;
  splRefSellPrice: number;
  splRefStock: number;
}

/** `DTO/updateDTO/UpdateSupplierReferenceDTO.java` */
export interface SupplierReferenceUpdate {
  splRefReference: string;
  splRefSellPrice: number;
  splRefStock: number;
}

/** `DTO/MakerReferenceDTO.java` */
export interface MakerReference {
  article: ArticleResponseMakerReference | null;
  maker: MakerResponse | null;
  reference: string;
  artMkrStock: number;
  artMkrSellPrice: number;
}

/** `DTO/requestDTO/MakerReferenceRequestDTO.java` */
export interface MakerReferenceRequest {
  artId: number;
  mkrId: number;
  artMkrReference: string;
  artMkrStock: number;
  artMkrSellPrice: number;
}

/** `DTO/updateDTO/UpdateMakerReferenceDTO.java` */
export interface MakerReferenceUpdate {
  reference: string;
  artMkrStock: number;
  artMkrSellPrice: number;
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
   Devis / commandes / factures
   ================================================================== */

/** `enumeration/QuoteStatus.java` */
export type QuoteStatus =
  | 'CREATED'
  | 'ACCEPTED'
  | 'PENDING'
  | 'REJECTED'
  | 'EXPIRED'
  | 'CLOSED'
  | 'REVISITED';

/** `enumeration/CommandStatus.java` */
export type CommandStatus = 'CREATED' | 'PENDING' | 'ACCEPTED' | 'DELIVERED';

/** `enumeration/InvoiceStatus.java` */
export type InvoiceStatus =
  | 'CREATED'
  | 'ISSUED'
  | 'SENT'
  | 'OVERDUE'
  | 'PARTIALLY_PAID'
  | 'PAID'
  | 'CANCELLED';

/** `DTO/QuoteLineDTO.java` */
export interface QuoteLine {
  qotLnId: number;
  qotLnQuantity: number;
  qotLnPriceHT: number;
  articleName: string;
  articleRef: string;
  tvaRate: number;
  totalHT: number;
  totalTVA: number;
  totalTTC: number;
}

/** `DTO/QuoteDTO.java` */
export interface Quote {
  quoteId: number;
  qotNumber: string;
  qotCreatedDate: string;
  expirationDate: string;
  qotStatus: QuoteStatus;
  qotParent: Quote | null;
  cartId: number;
  qotLines: QuoteLine[];
}

/** `DTO/requestDTO/QuoteRequestDTO.java` */
export interface QuoteRequest {
  qotNumber: string;
  qotExpirationDate: string;
  qotParentId: number | null;
  cartId: number;
}

/** `DTO/updateDTO/PatchQuoteLineQuantity.java` */
export interface PatchQuoteLineQuantity {
  artRef: string;
  qotLineQuantity: number;
}

/**
 * `DTO/CommandDTO.java`.
 *
 * Attention : le champ d'identifiant s'appelle `cmfId` côté backend (coquille
 * pour `cmdId`, non corrigée ici — voir la note sur les DTO dans le README).
 * `CommandDTO` n'expose ni client ni identifiant de devis, seulement son
 * numéro : la mise en correspondance avec un `Cart`/`Customer` se fait par
 * `quoteNumber` (voir `commerce-store.service.ts`).
 */
export interface Command {
  cmfId: number;
  cmdCreatedDate: string;
  cmdModifiedDate: string;
  cmdStatus: CommandStatus;
  quoteNumber: string;
  quoteLines: QuoteLine[];
}

/** `DTO/requestDTO/CommandRequestDTO.java` */
export interface CommandRequest {
  qotId: number;
}

/** `DTO/requestDTO/PatchCommandStatus.java` */
export interface PatchCommandStatus {
  cmdId: number;
  cmdStatus: CommandStatus;
}

/** `DTO/InvoiceLineDTO.java` */
export interface InvoiceLine {
  invLnId: number;
  invLnQuantity: number;
  invLnPriceHT: number;
  articleName: string;
  articleRef: string;
  tvaRate: number;
  totalHT: number;
  totalTVA: number;
  totalTTC: number;
}

/**
 * `DTO/InvoiceDTO.java`.
 *
 * Ne porte aucune référence vers la `Command` ni le `Quote` d'origine : il est
 * impossible, depuis ce DTO seul, de savoir quelle commande a été facturée.
 * Voir la note dans le README sur ce manque côté backend.
 */
export interface Invoice {
  invoiceId: number;
  invoiceNumber: string;
  invoiceCreatedDate: string;
  invoicePathPDF: string;
  invoiceStatus: InvoiceStatus;
  invoiceLines: InvoiceLine[];
}

/** `DTO/requestDTO/InvoiceRequestDTO.java` */
export interface InvoiceRequest {
  invoiceNumber: string;
  commandId: number;
}

/** `DTO/requestDTO/PatchInvoiceStatus.java` */
export interface PatchInvoiceStatus {
  invoiceId: number;
  invoiceStatus: InvoiceStatus;
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
