import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Tva, TvaRequest, TvaTauxUpdate } from '../models/api.models';
import { API_BASE_URL } from './api.config';

/** Miroir de `TvaController` (`/tva`). */
@Injectable({ providedIn: 'root' })
export class TvaService {
  private readonly http = inject(HttpClient);
  private readonly url = `${inject(API_BASE_URL)}/tva`;

  list(): Observable<Tva[]> {
    return this.http.get<Tva[]>(`${this.url}/list`);
  }

  getById(id: number): Observable<Tva> {
    return this.http.get<Tva>(`${this.url}/${id}`);
  }

  create(payload: TvaRequest): Observable<Tva> {
    return this.http.post<Tva>(this.url, payload);
  }

  update(id: number, payload: TvaRequest): Observable<Tva> {
    return this.http.put<Tva>(`${this.url}/${id}`, payload);
  }

  /** Modification du seul taux (`PATCH /tva/{id}`). */
  patchTaux(id: number, payload: TvaTauxUpdate): Observable<Tva> {
    return this.http.patch<Tva>(`${this.url}/${id}`, payload);
  }

  delete(id: number): Observable<Tva> {
    return this.http.delete<Tva>(`${this.url}/${id}`);
  }
}
