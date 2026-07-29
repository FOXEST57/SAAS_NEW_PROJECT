import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
  ArticleResponseSupplier,
  Supplier,
  SupplierReference,
  SupplierReferenceRequest,
  SupplierReferenceUpdate,
} from '../models/api.models';
import { API_BASE_URL } from './api.config';

/** Miroir de `SupplierReferenceController` (`/supplier-reference`). */
@Injectable({ providedIn: 'root' })
export class SupplierReferenceService {
  private readonly http = inject(HttpClient);
  private readonly url = `${inject(API_BASE_URL)}/supplier-reference`;

  list(): Observable<SupplierReference[]> {
    return this.http.get<SupplierReference[]>(`${this.url}/list`);
  }

  getById(articleId: number, supplierId: number): Observable<SupplierReference> {
    return this.http.get<SupplierReference>(`${this.url}/${articleId}/${supplierId}`);
  }

  /** Fournisseurs référençant un article donné. */
  listSuppliersOfArticle(articleId: number): Observable<Supplier[]> {
    return this.http.get<Supplier[]>(`${this.url}/list-supplier/${articleId}`);
  }

  /** Articles proposés par un fournisseur donné. */
  listArticlesOfSupplier(supplierId: number): Observable<ArticleResponseSupplier[]> {
    return this.http.get<ArticleResponseSupplier[]>(`${this.url}/list-article/${supplierId}`);
  }

  create(payload: SupplierReferenceRequest): Observable<SupplierReference> {
    return this.http.post<SupplierReference>(this.url, payload);
  }

  update(
    articleId: number,
    supplierId: number,
    payload: SupplierReferenceUpdate,
  ): Observable<SupplierReference> {
    return this.http.put<SupplierReference>(`${this.url}/${articleId}/${supplierId}`, payload);
  }

  delete(articleId: number, supplierId: number): Observable<SupplierReference> {
    return this.http.delete<SupplierReference>(`${this.url}/${articleId}/${supplierId}`);
  }
}
