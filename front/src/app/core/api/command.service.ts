import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Command, CommandRequest, PatchCommandStatus } from '../models/api.models';
import { API_BASE_URL } from './api.config';

/** Miroir de `CommandController` (`/command`). */
@Injectable({ providedIn: 'root' })
export class CommandService {
  private readonly http = inject(HttpClient);
  private readonly url = `${inject(API_BASE_URL)}/command`;

  list(): Observable<Command[]> {
    return this.http.get<Command[]>(`${this.url}/list`);
  }

  getById(id: number): Observable<Command> {
    return this.http.get<Command>(`${this.url}/${id}`);
  }

  /** Crée la commande à partir d'un devis accepté. */
  create(payload: CommandRequest): Observable<Command> {
    return this.http.post<Command>(this.url, payload);
  }

  /** Route unique, sans id dans l'URL : l'id voyage dans le corps (`cmdId`). */
  patchStatus(payload: PatchCommandStatus): Observable<Command> {
    return this.http.patch<Command>(this.url, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
