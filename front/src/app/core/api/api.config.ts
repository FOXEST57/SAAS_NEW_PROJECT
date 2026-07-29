import { InjectionToken } from '@angular/core';
import { environment } from '../../../environments/environment';

/** URL de base de l'API Spring Boot (proxifiée en développement). */
export const API_BASE_URL = new InjectionToken<string>('API_BASE_URL', {
  providedIn: 'root',
  factory: () => environment.apiBaseUrl,
});
