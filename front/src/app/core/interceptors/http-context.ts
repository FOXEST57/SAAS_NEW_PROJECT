import { HttpContext, HttpContextToken } from '@angular/common/http';

/**
 * Demande à l'intercepteur de ne pas afficher de notification d'erreur.
 *
 * Utile lorsque l'appelant sait mieux que l'intercepteur quoi dire : la
 * synchronisation des lignes de document, par exemple, préfère un message
 * consolidé plutôt qu'une notification par ligne en échec.
 */
export const SILENT_ERRORS = new HttpContextToken<boolean>(() => false);

export function silent(): HttpContext {
  return new HttpContext().set(SILENT_ERRORS, true);
}
