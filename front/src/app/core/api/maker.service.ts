import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Maker, MakerRequest } from '../models/api.models';
import { API_BASE_URL } from './api.config';

/** Miroir de `MakerController` (`/maker`). */
@Injectable({ providedIn: 'root' })
export class MakerService {
  private readonly http = inject(HttpClient);
  private readonly url = `${inject(API_BASE_URL)}/maker`;

  list(): Observable<Maker[]> {
    return this.http.get<Maker[]>(`${this.url}/list`);
  }

  getById(id: number): Observable<Maker> {
    return this.http.get<Maker>(`${this.url}/${id}`);
  }

  create(payload: MakerRequest): Observable<Maker> {
    return this.http.post<Maker>(this.url, payload);
  }

  update(id: number, payload: MakerRequest): Observable<Maker> {
    return this.http.put<Maker>(`${this.url}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
