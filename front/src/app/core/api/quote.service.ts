import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Quote, QuoteLineQuantityPatch, QuoteRequest, QuoteStatus } from '../models/api.models';
import { API_BASE_URL } from './api.config';

/** Miroir de `QuoteController` (`/quote`). */
@Injectable({ providedIn: 'root' })
export class QuoteService {
  private readonly http = inject(HttpClient);
  private readonly url = `${inject(API_BASE_URL)}/quote`;

  list(): Observable<Quote[]> {
    return this.http.get<Quote[]>(`${this.url}/list`);
  }

  getById(qotId: number): Observable<Quote> {
    return this.http.get<Quote>(`${this.url}/${qotId}`);
  }

  create(payload: QuoteRequest): Observable<Quote> {
    return this.http.post<Quote>(this.url, payload);
  }

  /**
   * Modifie la quantité d'une ligne, désignée par la **référence d'article**.
   *
   * Le backend cherche cette référence dans toute la table plutôt que dans le
   * devis visé : la ligne modifiée peut appartenir à un autre devis, et l'appel
   * échoue dès que deux devis partagent un article. Voir
   * `backend-patch/Quote-analyse.patch`, point 2.
   */
  patchQuantity(qotId: number, payload: QuoteLineQuantityPatch): Observable<Quote> {
    return this.http.patch<Quote>(`${this.url}/quantity/${qotId}`, payload);
  }

  /**
   * Le contrôleur attend l'énumération **nue** en corps de requête — une chaîne
   * JSON brute (`"ACCEPTED"`), sans objet enveloppant. D'où le `JSON.stringify`
   * explicite et l'en-tête posé à la main : passer la chaîne telle quelle
   * ferait envoyer `ACCEPTED` sans guillemets, que Jackson refuserait.
   */
  patchStatus(qotId: number, status: QuoteStatus): Observable<Quote> {
    return this.http.patch<Quote>(`${this.url}/status/${qotId}`, JSON.stringify(status), {
      headers: { 'Content-Type': 'application/json' },
    });
  }

  delete(qotId: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${qotId}`);
  }
}
