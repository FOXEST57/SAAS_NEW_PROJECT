import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

/**
 * Réserve une route aux utilisateurs connectés.
 *
 * L'adresse demandée est transmise à l'écran de connexion afin d'y revenir
 * une fois l'identification faite : ouvrir un lien vers un devis précis doit
 * mener à ce devis, pas au tableau de bord.
 */
export const authGuard: CanActivateFn = (_route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.isAuthenticated()) return true;

  return router.createUrlTree(['/connexion'], {
    queryParams: state.url && state.url !== '/' ? { suite: state.url } : undefined,
  });
};

/**
 * Empêche un utilisateur déjà connecté de revenir sur l'écran de connexion.
 */
export const guestGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return auth.isAuthenticated() ? router.createUrlTree(['/tableau-de-bord']) : true;
};
