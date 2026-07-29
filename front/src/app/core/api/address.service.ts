import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Address, AddressRequest } from '../models/api.models';
import { API_BASE_URL } from './api.config';

/** Miroir de `AddressController` (`/address`). */
@Injectable({ providedIn: 'root' })
export class AddressService {
  private readonly http = inject(HttpClient);
  private readonly url = `${inject(API_BASE_URL)}/address`;

  list(): Observable<Address[]> {
    return this.http.get<Address[]>(`${this.url}/list`);
  }

  getById(addId: number): Observable<Address> {
    return this.http.get<Address>(`${this.url}/${addId}`);
  }

  create(payload: AddressRequest): Observable<Address> {
    return this.http.post<Address>(this.url, payload);
  }

  update(addId: number, payload: AddressRequest): Observable<Address> {
    return this.http.put<Address>(`${this.url}/${addId}`, payload);
  }

  delete(addId: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${addId}`);
  }
}
