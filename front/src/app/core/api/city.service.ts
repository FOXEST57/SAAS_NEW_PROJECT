import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { City, CityRequest } from '../models/api.models';
import { API_BASE_URL } from './api.config';

/** Miroir de `CityController` (`/city`). */
@Injectable({ providedIn: 'root' })
export class CityService {
  private readonly http = inject(HttpClient);
  private readonly url = `${inject(API_BASE_URL)}/city`;

  list(): Observable<City[]> {
    return this.http.get<City[]>(`${this.url}/list`);
  }

  getById(cityId: number): Observable<City> {
    return this.http.get<City>(`${this.url}/${cityId}`);
  }

  create(payload: CityRequest): Observable<City> {
    return this.http.post<City>(this.url, payload);
  }

  update(cityId: number, payload: CityRequest): Observable<City> {
    return this.http.put<City>(`${this.url}/${cityId}`, payload);
  }

  delete(cityId: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${cityId}`);
  }
}
