import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Invoice, InvoiceRequest, PatchInvoiceStatus } from '../models/api.models';
import { API_BASE_URL } from './api.config';

/** Miroir de `InvoiceController` (`/invoice`). */
@Injectable({ providedIn: 'root' })
export class InvoiceService {
  private readonly http = inject(HttpClient);
  private readonly url = `${inject(API_BASE_URL)}/invoice`;

  list(): Observable<Invoice[]> {
    return this.http.get<Invoice[]>(`${this.url}/list`);
  }

  getById(id: number): Observable<Invoice> {
    return this.http.get<Invoice>(`${this.url}/${id}`);
  }

  /** Crée la facture à partir d'une commande ; les lignes sont figées côté serveur depuis le devis. */
  create(payload: InvoiceRequest): Observable<Invoice> {
    return this.http.post<Invoice>(this.url, payload);
  }

  /** Route unique, sans id dans l'URL : l'id voyage dans le corps (`invoiceId`). */
  patchStatus(payload: PatchInvoiceStatus): Observable<Invoice> {
    return this.http.patch<Invoice>(this.url, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
