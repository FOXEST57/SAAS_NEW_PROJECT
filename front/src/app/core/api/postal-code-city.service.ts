import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { PostalCodeCity, PostalCodeCityRequest } from '../models/api.models';
import { API_BASE_URL } from './api.config';

/** Miroir de `PostalCodeCityController` (`/postalcodecity`). */
@Injectable({ providedIn: 'root' })
export class PostalCodeCityService {
  private readonly http = inject(HttpClient);
  private readonly url = `${inject(API_BASE_URL)}/postalcodecity`;

  list(): Observable<PostalCodeCity[]> {
    return this.http.get<PostalCodeCity[]>(`${this.url}/list`);
  }

  getById(pCodeId: number, cityId: number): Observable<PostalCodeCity> {
    return this.http.get<PostalCodeCity>(`${this.url}/${pCodeId}/${cityId}`);
  }

  create(payload: PostalCodeCityRequest): Observable<PostalCodeCity> {
    return this.http.post<PostalCodeCity>(this.url, payload);
  }

  update(
    pCodeId: number,
    cityId: number,
    payload: PostalCodeCityRequest,
  ): Observable<PostalCodeCity> {
    return this.http.put<PostalCodeCity>(`${this.url}/${pCodeId}/${cityId}`, payload);
  }

  delete(pCodeId: number, cityId: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${pCodeId}/${cityId}`);
  }
}
