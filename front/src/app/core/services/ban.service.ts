import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

/**
 * Propriétés d'une entité renvoyée par l'API Adresse (BAN).
 * @see https://adresse.data.gouv.fr/api-doc/adresse
 */
export interface BanProperties {
  label: string;
  score: number;
  id: string;
  /** `housenumber` | `street` | `locality` | `municipality` */
  type: string;
  name: string;
  postcode: string;
  citycode: string;
  city: string;
  context: string;
  housenumber?: string;
  street?: string;
  district?: string;
  x?: number;
  y?: number;
}

export interface BanFeature {
  type: 'Feature';
  geometry: { type: 'Point'; coordinates: [number, number] };
  properties: BanProperties;
}

interface BanFeatureCollection {
  type: 'FeatureCollection';
  features: BanFeature[];
}

/** Adresse normalisée, prête à être injectée dans un formulaire. */
export interface ResolvedAddressParts {
  /** Numéro de voie (ex. « 12 », « 33 bis »). */
  number: string;
  /** Libellé de voie (ex. « rue de la Paix »). */
  street: string;
  postalCode: string;
  city: string;
  country: string;
  /** Libellé complet renvoyé par la BAN, pour affichage. */
  label: string;
  /** Coordonnées WGS84 [longitude, latitude]. */
  coordinates: [number, number] | null;
}

/**
 * Client de l'API Adresse de l'État français (Base Adresse Nationale).
 *
 * Service public, gratuit, sans clé d'API ni quota bloquant.
 * Utilisé pour l'autocomplétion des adresses clients / fournisseurs /
 * fabricants.
 */
@Injectable({ providedIn: 'root' })
export class BanService {
  private readonly http = inject(HttpClient);
  private readonly base = environment.banApiUrl;

  /**
   * Recherche plein texte. La BAN exige au moins 3 caractères.
   *
   * @param query      texte saisi par l'utilisateur
   * @param limit      nombre de suggestions (max 20 côté API)
   * @param postcode   filtre facultatif sur le code postal
   */
  search(query: string, limit = 8, postcode?: string): Observable<BanFeature[]> {
    const q = query?.trim() ?? '';
    if (q.length < 3) return of([]);

    let params = new HttpParams().set('q', q).set('limit', String(limit)).set('autocomplete', '1');
    if (postcode) params = params.set('postcode', postcode);

    return this.http.get<BanFeatureCollection>(`${this.base}/search/`, { params }).pipe(
      map((res) => res?.features ?? []),
      // Le service peut être momentanément indisponible : la saisie manuelle
      // reste possible, on ne casse donc pas le formulaire.
      catchError(() => of([])),
    );
  }

  /** Recherche inverse à partir de coordonnées GPS. */
  reverse(lon: number, lat: number): Observable<BanFeature[]> {
    const params = new HttpParams().set('lon', String(lon)).set('lat', String(lat));
    return this.http.get<BanFeatureCollection>(`${this.base}/reverse/`, { params }).pipe(
      map((res) => res?.features ?? []),
      catchError(() => of([])),
    );
  }

  /** Convertit une suggestion BAN en champs exploitables par le formulaire. */
  static toParts(feature: BanFeature): ResolvedAddressParts {
    const p = feature.properties;

    // Pour un type « street » ou « municipality », la BAN ne renvoie pas de
    // numéro : on retombe sur `name` comme libellé de voie.
    const street = p.street ?? (p.type === 'housenumber' ? p.name : p.name) ?? '';
    const number = p.housenumber ?? '';

    return {
      number,
      // Si `name` contient déjà le numéro, on le retire du libellé de voie.
      street: number && street.startsWith(number) ? street.slice(number.length).trim() : street,
      postalCode: p.postcode ?? '',
      city: p.city ?? '',
      country: 'France',
      label: p.label ?? '',
      coordinates: feature.geometry?.coordinates ?? null,
    };
  }
}
