import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Article, ArticleRequest, ArticleUpdate } from '../models/api.models';
import { silent } from '../interceptors/http-context';
import { API_BASE_URL } from './api.config';

/** Miroir de `ArticleController` (`/article`). */
@Injectable({ providedIn: 'root' })
export class ArticleService {
  private readonly http = inject(HttpClient);
  private readonly url = `${inject(API_BASE_URL)}/article`;

  list(): Observable<Article[]> {
    return this.http.get<Article[]>(`${this.url}/list`);
  }

  getById(id: number): Observable<Article> {
    return this.http.get<Article>(`${this.url}/${id}`);
  }

  /**
   * @param quiet laisse l'appelant formuler le message d'erreur, plutôt que
   * d'ajouter une notification générique à celle qu'il produira lui-même.
   */
  create(payload: ArticleRequest, quiet = false): Observable<Article> {
    return this.http.post<Article>(this.url, payload, quiet ? { context: silent() } : {});
  }

  /** `PUT /article/{id}` attend un `ArticleUpdateDTO` (sans les fournisseurs). */
  update(id: number, payload: ArticleUpdate): Observable<Article> {
    return this.http.put<Article>(`${this.url}/${id}`, payload);
  }

  delete(id: number): Observable<Article> {
    return this.http.delete<Article>(`${this.url}/${id}`);
  }
}
