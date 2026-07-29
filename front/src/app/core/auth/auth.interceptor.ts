import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

/**
 * Joint le jeton à chaque appel vers l'API.
 *
 * Le format `Bearer <jeton>` est imposé par `JwtFilter`, qui fait un
 * `substring(7)` aveugle sur l'en-tête : tout autre préfixe produirait un
 * jeton tronqué et une erreur serveur.
 *
 * La connexion elle-même est exclue — y joindre un jeton n'aurait pas de sens,
 * et un jeton périmé encore en mémoire empêcherait de s'en refaire un.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const token = auth.token();

  const isAuthCall = req.url.includes('/logIn') || req.url.includes('/signUp');
  const isExternal = !req.url.startsWith('/') && !req.url.includes('localhost:8080');

  if (!token || isAuthCall || isExternal) {
    return next(req);
  }

  return next(
    req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }),
  );
};
