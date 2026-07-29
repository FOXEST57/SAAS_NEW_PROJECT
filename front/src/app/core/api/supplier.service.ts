import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Supplier, SupplierRequest } from '../models/api.models';
import { API_BASE_URL } from './api.config';

/** Miroir de `SupplierController` (`/supplier`). */
@Injectable({ providedIn: 'root' })
export class SupplierService {
  private readonly http = inject(HttpClient);
  private readonly url = `${inject(API_BASE_URL)}/supplier`;

  list(): Observable<Supplier[]> {
    return this.http.get<Supplier[]>(`${this.url}/list`);
  }

  getById(id: number): Observable<Supplier> {
    return this.http.get<Supplier>(`${this.url}/${id}`);
  }

  create(payload: SupplierRequest): Observable<Supplier> {
    return this.http.post<Supplier>(this.url, payload);
  }

  update(id: number, payload: SupplierRequest): Observable<Supplier> {
    return this.http.put<Supplier>(`${this.url}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
