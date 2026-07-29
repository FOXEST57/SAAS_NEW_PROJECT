import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { AccountType, AccountTypeRequest } from '../models/api.models';
import { API_BASE_URL } from './api.config';

/** Miroir de `AccountTypeController` (`/AccountType`). */
@Injectable({ providedIn: 'root' })
export class AccountTypeService {
  private readonly http = inject(HttpClient);
  // Attention : la route backend est en PascalCase.
  private readonly url = `${inject(API_BASE_URL)}/AccountType`;

  list(): Observable<AccountType[]> {
    return this.http.get<AccountType[]>(`${this.url}/list`);
  }

  getById(id: number): Observable<AccountType> {
    return this.http.get<AccountType>(`${this.url}/${id}`);
  }

  create(payload: AccountTypeRequest): Observable<AccountType> {
    return this.http.post<AccountType>(this.url, payload);
  }

  update(id: number, payload: AccountTypeRequest): Observable<AccountType> {
    return this.http.put<AccountType>(`${this.url}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
