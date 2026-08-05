import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Invoice, InvoiceRequest, InvoiceStatusPatch } from '../models/api.models';
import { API_BASE_URL } from './api.config';

/**
 * Miroir de `InvoiceController` (`/invoice`).
 *
 * Mêmes conventions que `CommandService` : création et changement de statut
 * sur la racine (`POST /invoice`, `PATCH /invoice`), l'identifiant voyageant
 * dans le corps pour le `PATCH`.
 */
@Injectable({ providedIn: 'root' })
export class InvoiceService {
  private readonly http = inject(HttpClient);
  private readonly url = `${inject(API_BASE_URL)}/invoice`;

  list(): Observable<Invoice[]> {
    return this.http.get<Invoice[]>(`${this.url}/list`);
  }

  getById(invoiceId: number): Observable<Invoice> {
    return this.http.get<Invoice>(`${this.url}/${invoiceId}`);
  }

  /**
   * Émet la facture d'une commande.
   *
   * Le numéro est à la charge du client (`@NotBlank`, unique en base) ; les
   * lignes sont recopiées côté serveur depuis le devis de la commande. Une
   * seconde facture sur la même commande est refusée (HTTP 409).
   */
  create(payload: InvoiceRequest): Observable<Invoice> {
    return this.http.post<Invoice>(this.url, payload);
  }

  patchStatus(payload: InvoiceStatusPatch): Observable<Invoice> {
    return this.http.patch<Invoice>(this.url, payload);
  }

  delete(invoiceId: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${invoiceId}`);
  }
}
