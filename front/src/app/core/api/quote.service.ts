import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
  PatchQuoteLineQuantity,
  Quote,
  QuoteRequest,
  QuoteStatus,
} from '../models/api.models';
import { API_BASE_URL } from './api.config';

/** Miroir de `QuoteController` (`/quote`). */
@Injectable({ providedIn: 'root' })
export class QuoteService {
  private readonly http = inject(HttpClient);
  private readonly url = `${inject(API_BASE_URL)}/quote`;

  list(): Observable<Quote[]> {
    return this.http.get<Quote[]>(`${this.url}/list`);
  }

  getById(id: number): Observable<Quote> {
    return this.http.get<Quote>(`${this.url}/${id}`);
  }

  create(payload: QuoteRequest): Observable<Quote> {
    return this.http.post<Quote>(this.url, payload);
  }

  /** Ne fonctionne que sur un devis dans un statut modifiable (CREATED, PENDING, REJECTED). */
  updateQuantity(id: number, payload: PatchQuoteLineQuantity): Observable<Quote> {
    return this.http.patch<Quote>(`${this.url}/quantity/${id}`, payload);
  }

  updateStatus(id: number, status: QuoteStatus): Observable<Quote> {
    return this.http.patch<Quote>(`${this.url}/status/${id}`, status);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
