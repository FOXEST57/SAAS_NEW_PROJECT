import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Cart, CartRequest } from '../models/api.models';
import { API_BASE_URL } from './api.config';

/** Miroir de `CartController` (`/cart`). */
@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly http = inject(HttpClient);
  private readonly url = `${inject(API_BASE_URL)}/cart`;

  list(): Observable<Cart[]> {
    return this.http.get<Cart[]>(`${this.url}/list`);
  }

  getById(id: number): Observable<Cart> {
    return this.http.get<Cart>(`${this.url}/${id}`);
  }

  create(payload: CartRequest): Observable<Cart> {
    return this.http.post<Cart>(this.url, payload);
  }

  update(id: number, payload: CartRequest): Observable<Cart> {
    return this.http.put<Cart>(`${this.url}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
