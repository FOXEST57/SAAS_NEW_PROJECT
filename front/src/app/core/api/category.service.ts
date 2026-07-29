import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Category, CategoryRequest } from '../models/api.models';
import { API_BASE_URL } from './api.config';

/** Miroir de `CategoryController` (`/category`). */
@Injectable({ providedIn: 'root' })
export class CategoryService {
  private readonly http = inject(HttpClient);
  private readonly url = `${inject(API_BASE_URL)}/category`;

  list(): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.url}/list`);
  }

  getById(id: number): Observable<Category> {
    return this.http.get<Category>(`${this.url}/${id}`);
  }

  create(payload: CategoryRequest): Observable<Category> {
    return this.http.post<Category>(this.url, payload);
  }

  update(id: number, payload: CategoryRequest): Observable<Category> {
    return this.http.put<Category>(`${this.url}/${id}`, payload);
  }

  delete(id: number): Observable<Category> {
    return this.http.delete<Category>(`${this.url}/${id}`);
  }
}
