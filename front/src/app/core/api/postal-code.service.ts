import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { PostalCode, PostalCodeRequest } from '../models/api.models';
import { API_BASE_URL } from './api.config';

/** Miroir de `PostalCodeController` (`/postalcode`). */
@Injectable({ providedIn: 'root' })
export class PostalCodeService {
  private readonly http = inject(HttpClient);
  private readonly url = `${inject(API_BASE_URL)}/postalcode`;

  list(): Observable<PostalCode[]> {
    return this.http.get<PostalCode[]>(`${this.url}/list`);
  }

  getById(pCodeId: number): Observable<PostalCode> {
    return this.http.get<PostalCode>(`${this.url}/${pCodeId}`);
  }

  create(payload: PostalCodeRequest): Observable<PostalCode> {
    return this.http.post<PostalCode>(this.url, payload);
  }

  update(pCodeId: number, payload: PostalCodeRequest): Observable<PostalCode> {
    return this.http.put<PostalCode>(`${this.url}/${pCodeId}`, payload);
  }

  delete(pCodeId: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${pCodeId}`);
  }
}
