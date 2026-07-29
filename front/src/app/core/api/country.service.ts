import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Country, CountryRequest } from '../models/api.models';
import { API_BASE_URL } from './api.config';

/** Miroir de `CountryController` (`/country`). */
@Injectable({ providedIn: 'root' })
export class CountryService {
  private readonly http = inject(HttpClient);
  private readonly url = `${inject(API_BASE_URL)}/country`;

  list(): Observable<Country[]> {
    return this.http.get<Country[]>(`${this.url}/list`);
  }

  getById(cntId: number): Observable<Country> {
    return this.http.get<Country>(`${this.url}/${cntId}`);
  }

  create(payload: CountryRequest): Observable<Country> {
    return this.http.post<Country>(this.url, payload);
  }

  update(cntId: number, payload: CountryRequest): Observable<Country> {
    return this.http.put<Country>(`${this.url}/${cntId}`, payload);
  }

  delete(cntId: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${cntId}`);
  }
}
