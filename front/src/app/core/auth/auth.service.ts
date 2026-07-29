import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { API_BASE_URL } from '../api/api.config';
import { silent } from '../interceptors/http-context';
import { AuthUser, LoginRequest, Role, isRole, rankOf } from './auth.models';

const STORAGE_KEY = 'klimafact.token';

/**
 * Session de l'utilisateur connecté.
 *
 * Deux particularités du backend dictent la forme de ce service :
 *
 * 1. `POST /logIn` renvoie le jeton **brut, en `text/plain`** — et non un objet
 *    JSON. D'où le `responseType: 'text'` : laisser Angular tenter un
 *    `JSON.parse` ferait échouer toute connexion réussie.
 *
 * 2. Il n'existe pas de route `/me`. Le jeton est donc la seule source
 *    d'information sur la session : on lit son contenu côté client pour savoir
 *    qui est connecté et avec quel rôle.
 *
 * Ce décodage est un **confort d'affichage, pas une preuve**. La charge utile
 * d'un JWT est de la base64, lisible et falsifiable par quiconque ; seule la
 * signature vérifiée côté serveur fait foi. Rien de ce qui est décidé ici ne
 * doit tenir lieu de contrôle de sécurité.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly base = inject(API_BASE_URL);

  private readonly _token = signal<string | null>(readStoredToken());

  /** Jeton courant, ou `null` hors session. */
  readonly token = this._token.asReadonly();

  /** Utilisateur déduit du jeton. `null` si absent ou illisible. */
  readonly user = computed<AuthUser | null>(() => {
    const token = this._token();
    return token ? decodeUser(token) : null;
  });

  readonly isAuthenticated = computed(() => this.user() !== null);

  /** Vrai si l'utilisateur détient au moins le rôle demandé. */
  hasAtLeast(role: Role): boolean {
    const current = this.user();
    return current ? rankOf(current.role) >= rankOf(role) : false;
  }

  login(credentials: LoginRequest): Observable<string> {
    return this.http
      .post(`${this.base}/logIn`, credentials, {
        responseType: 'text',
        // L'écran de connexion affiche son propre message sous le formulaire :
        // une notification globale par-dessus ferait doublon.
        context: silent(),
      })
      .pipe(tap((token) => this.store(token.trim())));
  }

  /**
   * Ferme la session et renvoie vers l'écran de connexion.
   *
   * Le jeton n'ayant pas de durée de vie côté backend et aucune route de
   * révocation n'existant, la déconnexion se limite à oublier le jeton
   * localement. Un jeton copié avant la déconnexion resterait valable
   * indéfiniment.
   */
  logout(options: { readonly redirectTo?: string } = {}): void {
    this._token.set(null);
    safeRemove(STORAGE_KEY);
    void this.router.navigate(['/connexion'], {
      queryParams: options.redirectTo ? { suite: options.redirectTo } : undefined,
    });
  }

  private store(token: string): void {
    this._token.set(token);
    safeWrite(STORAGE_KEY, token);
  }
}

/* ------------------------------------------------------------------ */
/* Lecture du jeton                                                     */
/* ------------------------------------------------------------------ */

/**
 * Extrait l'utilisateur de la charge utile du JWT.
 *
 * Renvoie `null` dès que le jeton n'a pas la forme attendue : un jeton
 * corrompu doit déconnecter proprement, pas faire planter l'application au
 * démarrage.
 */
function decodeUser(token: string): AuthUser | null {
  const parts = token.split('.');
  if (parts.length !== 3) return null;

  try {
    const payload = JSON.parse(base64UrlDecode(parts[1])) as Record<string, unknown>;
    const email = typeof payload['sub'] === 'string' ? payload['sub'] : null;
    if (!email) return null;

    const rawRole = typeof payload['role'] === 'string' ? payload['role'] : '';
    const normalized = rawRole.toLowerCase().replace(/[\s_-]/g, '');

    return {
      email,
      // Un libellé inconnu retombe sur le rôle le moins privilégié : mieux vaut
      // sous-estimer les droits que les surestimer.
      role: isRole(normalized) ? normalized : 'user',
      rawRole,
    };
  } catch {
    return null;
  }
}

/** Base64 « URL-safe », telle que produite par les JWT. */
function base64UrlDecode(segment: string): string {
  const base64 = segment.replace(/-/g, '+').replace(/_/g, '/');
  const padded = base64.padEnd(base64.length + ((4 - (base64.length % 4)) % 4), '=');
  const binary = atob(padded);

  // `atob` renvoie des octets : ce détour restitue l'UTF-8 (accents des noms).
  const bytes = Uint8Array.from(binary, (c) => c.charCodeAt(0));
  return new TextDecoder().decode(bytes);
}

/* ------------------------------------------------------------------ */
/* Stockage                                                             */
/* ------------------------------------------------------------------ */

/*
 * Le stockage peut être indisponible — navigation privée sur certains
 * navigateurs, cookies tiers bloqués dans une iframe. L'application doit alors
 * continuer de fonctionner le temps de la session, en mémoire seulement.
 */

function readStoredToken(): string | null {
  try {
    return localStorage.getItem(STORAGE_KEY);
  } catch {
    return null;
  }
}

function safeWrite(key: string, value: string): void {
  try {
    localStorage.setItem(key, value);
  } catch {
    /* session non persistée : sans gravité */
  }
}

function safeRemove(key: string): void {
  try {
    localStorage.removeItem(key);
  } catch {
    /* idem */
  }
}
