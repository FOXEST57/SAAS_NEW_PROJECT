import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Command, CommandRequest, CommandStatusPatch } from '../models/api.models';
import { API_BASE_URL } from './api.config';

/**
 * Miroir de `CommandController` (`/command`).
 *
 * Deux particularités du contrôleur, reprises telles quelles ici :
 * - la création et le changement de statut sont mappés sur la racine
 *   (`POST /command`, `PATCH /command`), sans segment supplémentaire ;
 * - le `PATCH` ne prend pas d'identifiant dans l'URL : `cmdId` voyage dans le
 *   corps de la requête, avec le nouveau statut.
 */
@Injectable({ providedIn: 'root' })
export class CommandService {
  private readonly http = inject(HttpClient);
  private readonly url = `${inject(API_BASE_URL)}/command`;

  list(): Observable<Command[]> {
    return this.http.get<Command[]>(`${this.url}/list`);
  }

  getById(cmdId: number): Observable<Command> {
    return this.http.get<Command>(`${this.url}/${cmdId}`);
  }

  /**
   * Crée le bon de commande d'un devis accepté.
   *
   * Le serveur refuse une seconde commande sur le même devis
   * (`ResourceAlreadyExistException`, HTTP 409) et impose lui-même le statut
   * `CREATED` : le client ne choisit ni l'un ni l'autre.
   */
  create(payload: CommandRequest): Observable<Command> {
    return this.http.post<Command>(this.url, payload);
  }

  patchStatus(payload: CommandStatusPatch): Observable<Command> {
    return this.http.patch<Command>(this.url, payload);
  }

  delete(cmdId: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${cmdId}`);
  }
}
