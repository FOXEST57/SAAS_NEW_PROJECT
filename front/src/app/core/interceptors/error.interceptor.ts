import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { ToastService } from '../services/toast.service';
import { SILENT_ERRORS } from './http-context';

/**
 * Traduit les erreurs HTTP en notifications lisibles.
 *
 * Le backend renvoie un `GlobalExceptionInterceptorDTO`
 * (`{ status, error, message }`) pour les erreurs métier, et un objet de
 * validation Spring (`{ champ: message }`) pour les erreurs `@Valid`.
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const toast = inject(ToastService);

  return next(req).pipe(
    catchError((err: HttpErrorResponse) => {
      // Les appels vers l'API Adresse sont gérés localement (autocomplétion).
      if (req.url.includes('api-adresse.data.gouv.fr')) {
        return throwError(() => err);
      }

      // L'appelant a demandé à formuler lui-même le message.
      if (req.context.get(SILENT_ERRORS)) {
        return throwError(() => err);
      }

      toast.error(titleFor(err), describe(err));
      return throwError(() => err);
    }),
  );
};

function titleFor(err: HttpErrorResponse): string {
  switch (err.status) {
    case 0:
      return 'Backend injoignable';
    case 400:
      return 'Données invalides';
    case 404:
      return 'Ressource introuvable';
    case 409:
      return 'Conflit de données';
    case 500:
      return 'Erreur serveur';
    default:
      return `Erreur ${err.status}`;
  }
}

/** Extrait un message lisible quelle que soit la forme du corps d'erreur. */
export function describe(err: HttpErrorResponse): string {
  if (err.status === 0) {
    return "L'API Spring Boot ne répond pas. Vérifiez qu'elle tourne sur http://localhost:8080.";
  }

  const body = err.error;

  if (typeof body === 'string' && body.trim()) return body;

  if (body && typeof body === 'object') {
    // Format GlobalExceptionInterceptorDTO
    if (typeof body.message === 'string' && body.message.trim()) return body.message;

    // Format des erreurs de validation : { champ: "message", ... }
    const entries = Object.entries(body).filter(
      ([, v]) => typeof v === 'string',
    ) as [string, string][];
    if (entries.length) {
      return entries.map(([field, msg]) => `${field} : ${msg}`).join(' · ');
    }
  }

  return err.message || 'Une erreur inattendue est survenue.';
}
