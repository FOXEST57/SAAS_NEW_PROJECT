/** Identifiants soumis à `POST /logIn`. */
export interface LoginRequest {
  ctmEmail: string;
  password: string;
}

/**
 * Rôles portés par `accountType.accTypeLibelle`.
 *
 * Les quatre valeurs correspondent aux annotations `isUser`, `isSuperUser`,
 * `isAdmin` et `isSuperAdmin` du backend. Le libellé étant une donnée libre en
 * base, un rôle inconnu reste possible : il est alors traité comme le rôle le
 * moins privilégié plutôt que rejeté.
 */
export type Role = 'user' | 'superuser' | 'admin' | 'superadmin';

export const ROLES: readonly Role[] = ['user', 'superuser', 'admin', 'superadmin'];

/** Rang du rôle, du moins au plus privilégié. Sert aux comparaisons `auMoins`. */
const RANK: Record<Role, number> = { user: 0, superuser: 1, admin: 2, superadmin: 3 };

export function rankOf(role: Role): number {
  return RANK[role];
}

export function isRole(value: string): value is Role {
  return (ROLES as readonly string[]).includes(value);
}

/** Libellé lisible, pour l'affichage. */
export const ROLE_LABELS: Record<Role, string> = {
  user: 'Utilisateur',
  superuser: 'Utilisateur avancé',
  admin: 'Administrateur',
  superadmin: 'Super-administrateur',
};

/**
 * Utilisateur connecté, reconstitué à partir du JWT.
 *
 * Le backend n'expose pas de route `/me` : le jeton est la seule source
 * d'information sur la session. Il porte l'adresse e-mail en `sub` et le
 * libellé du type de compte dans un claim `role`.
 */
export interface AuthUser {
  readonly email: string;
  readonly role: Role;
  /** Libellé brut du claim, conservé tel quel si le rôle est inconnu. */
  readonly rawRole: string;
}
