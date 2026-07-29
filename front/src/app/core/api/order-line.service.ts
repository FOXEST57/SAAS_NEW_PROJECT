import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Cart, OrderLine, OrderLineRequest, OrderLineUpdate } from '../models/api.models';
import { silent } from '../interceptors/http-context';
import { API_BASE_URL } from './api.config';

/** Miroir de `OrderLineController` (`/order-line`). */
@Injectable({ providedIn: 'root' })
export class OrderLineService {
  private readonly http = inject(HttpClient);
  private readonly url = `${inject(API_BASE_URL)}/order-line`;

  getById(articleId: number, cartId: number): Observable<OrderLine> {
    return this.http.get<OrderLine>(`${this.url}/${articleId}/${cartId}`);
  }

  /** Lignes (avec article détaillé) d'un panier donné. */
  listByCart(cartId: number): Observable<OrderLine[]> {
    return this.http.get<OrderLine[]>(`${this.url}/list-articles/${cartId}`);
  }

  /** Paniers contenant un article donné. */
  listCartsOfArticle(articleId: number): Observable<Cart[]> {
    return this.http.get<Cart[]>(`${this.url}/list-cart/${articleId}`);
  }

  /**
   * @param quiet laisse l'appelant formuler le message d'erreur. La
   * synchronisation d'un document préfère un message consolidé à une
   * notification par ligne.
   */
  create(payload: OrderLineRequest, quiet = false): Observable<OrderLine> {
    return this.http.post<OrderLine>(this.url, payload, quiet ? { context: silent() } : {});
  }

  update(
    articleId: number,
    cartId: number,
    payload: OrderLineUpdate,
    quiet = false,
  ): Observable<OrderLine> {
    return this.http.put<OrderLine>(
      `${this.url}/${articleId}/${cartId}`,
      payload,
      quiet ? { context: silent() } : {},
    );
  }

  delete(articleId: number, cartId: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${articleId}/${cartId}`);
  }
}
