import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { Inventory, InventoryRequest } from '../models/api.models';
import { API_BASE_URL } from './api.config';

/** Miroir de `InventoryController` (`/inventory`). */
@Injectable({ providedIn: 'root' })
export class InventoryService {
  private readonly http = inject(HttpClient);
  private readonly url = `${inject(API_BASE_URL)}/inventory`;

  list(): Observable<Inventory[]> {
    return this.http.get<Inventory[]>(`${this.url}/list`);
  }

  getById(invId: number): Observable<Inventory> {
    return this.http.get<Inventory>(`${this.url}/${invId}`);
  }

  /**
   * Relevés d'un article donné.
   *
   * `IInventoryService.findByArticleId()` existe côté serveur mais **aucune
   * route ne l'expose**. On récupère donc tout puis on filtre ici. C'est
   * correct sur un historique de taille raisonnable, et cela deviendra coûteux
   * à mesure que les relevés s'accumulent — la route est demandée dans
   * `backend-patch/Inventory-blocages.patch`.
   */
  listByArticle(articleId: number): Observable<Inventory[]> {
    return this.list().pipe(
      map((all) => all.filter((inv) => inv.article?.artId === articleId)),
    );
  }

  create(payload: InventoryRequest): Observable<Inventory> {
    return this.http.post<Inventory>(this.url, payload);
  }

  update(invId: number, payload: InventoryRequest): Observable<Inventory> {
    return this.http.put<Inventory>(`${this.url}/${invId}`, payload);
  }

  delete(invId: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${invId}`);
  }
}
