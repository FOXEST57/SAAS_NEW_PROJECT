import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
  ArticleResponseMakerReference,
  Maker,
  MakerReference,
  MakerReferenceRequest,
  MakerReferenceUpdate,
} from '../models/api.models';
import { API_BASE_URL } from './api.config';

/** Miroir de `MakerReferenceController` (`/maker-reference`). */
@Injectable({ providedIn: 'root' })
export class MakerReferenceService {
  private readonly http = inject(HttpClient);
  private readonly url = `${inject(API_BASE_URL)}/maker-reference`;

  list(): Observable<MakerReference[]> {
    return this.http.get<MakerReference[]>(`${this.url}/list`);
  }

  getById(artId: number, mkrId: number): Observable<MakerReference> {
    return this.http.get<MakerReference>(`${this.url}/${artId}/${mkrId}`);
  }

  /** Fabricants référençant un article donné. */
  listMakersOfArticle(artId: number): Observable<Maker[]> {
    return this.http.get<Maker[]>(`${this.url}/list-maker/${artId}`);
  }

  /** Articles produits par un fabricant donné. */
  listArticlesOfMaker(mkrId: number): Observable<ArticleResponseMakerReference[]> {
    return this.http.get<ArticleResponseMakerReference[]>(`${this.url}/list-article/${mkrId}`);
  }

  create(payload: MakerReferenceRequest): Observable<MakerReference> {
    return this.http.post<MakerReference>(this.url, payload);
  }

  update(
    artId: number,
    mkrId: number,
    payload: MakerReferenceUpdate,
  ): Observable<MakerReference> {
    return this.http.put<MakerReference>(`${this.url}/${artId}/${mkrId}`, payload);
  }

  delete(artId: number, mkrId: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${artId}/${mkrId}`);
  }
}
