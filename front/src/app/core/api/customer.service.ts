import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Customer, CustomerRequest } from '../models/api.models';
import { API_BASE_URL } from './api.config';

/** Miroir de `CustomerController` (`/customer`). */
@Injectable({ providedIn: 'root' })
export class CustomerService {
  private readonly http = inject(HttpClient);
  private readonly url = `${inject(API_BASE_URL)}/customer`;

  list(): Observable<Customer[]> {
    return this.http.get<Customer[]>(`${this.url}/list`);
  }

  getById(ctmId: number): Observable<Customer> {
    return this.http.get<Customer>(`${this.url}/${ctmId}`);
  }

  create(payload: CustomerRequest): Observable<Customer> {
    return this.http.post<Customer>(this.url, payload);
  }

  update(ctmId: number, payload: CustomerRequest): Observable<Customer> {
    return this.http.put<Customer>(`${this.url}/${ctmId}`, payload);
  }

  delete(ctmId: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${ctmId}`);
  }
}
