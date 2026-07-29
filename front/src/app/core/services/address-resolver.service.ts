import { Injectable, inject } from '@angular/core';
import { Observable, firstValueFrom } from 'rxjs';
import {
  CityService,
  CountryService,
  PostalCodeCityService,
  PostalCodeService,
  AddressService,
} from '../api';
import { Address, City, Country, PostalCode } from '../models/api.models';

/** Adresse « à plat » telle que saisie dans un formulaire. */
export interface FlatAddress {
  number: string;
  street: string;
  complement: string;
  postalCode: string;
  city: string;
  country: string;
}

/** Journal des opérations réalisées, affiché à l'utilisateur après coup. */
export interface PersistLog {
  readonly step: string;
  readonly created: boolean;
}

export interface PersistResult {
  readonly address: Address;
  readonly log: PersistLog[];
}

/**
 * Orchestre la persistance d'une adresse.
 *
 * Le MCD impose une chaîne complète : une `Address` référence un `PostalCode`
 * **et** une `City`, et le backend (`AddressService.create`) refuse la création
 * si le lien `PostalCodeCity` correspondant n'existe pas. Une `City` référence
 * elle-même un `Country`.
 *
 * Ce service applique donc une stratégie « find-or-create » sur toute la
 * chaîne — Country → PostalCode → City → PostalCodeCity → Address — de sorte
 * qu'une adresse choisie dans l'autocomplétion BAN soit enregistrable en une
 * seule action utilisateur.
 *
 * Note : côté backend, `cntName`, `cityName` et `pCodeName` passent par un
 * `LowercaseConverter`. Toutes les comparaisons sont donc insensibles à la
 * casse et aux accents parasites d'espacement.
 */
@Injectable({ providedIn: 'root' })
export class AddressResolverService {
  private readonly countries = inject(CountryService);
  private readonly postalCodes = inject(PostalCodeService);
  private readonly cities = inject(CityService);
  private readonly links = inject(PostalCodeCityService);
  private readonly addresses = inject(AddressService);

  /**
   * Crée l'adresse (et tout ce qui manque en amont) puis renvoie l'entité
   * persistée. Si `addId` est fourni, l'adresse existante est mise à jour.
   */
  async persist(flat: FlatAddress, addId?: number | null): Promise<PersistResult> {
    const log: PersistLog[] = [];

    const country = await this.findOrCreateCountry(flat.country || 'France', log);
    const postalCode = await this.findOrCreatePostalCode(flat.postalCode, log);
    const city = await this.findOrCreateCity(flat.city, country.cntId, log);
    await this.ensureLink(postalCode.pCodeId, city.cityId, log);

    const payload = {
      addNumber: flat.number?.trim() || '',
      addStreet: flat.street?.trim() || '',
      addComplement: flat.complement?.trim() || '',
      pCodeId: postalCode.pCodeId,
      cityId: city.cityId,
    };

    const address = addId
      ? await firstValueFrom(this.addresses.update(addId, payload))
      : await firstValueFrom(this.addresses.create(payload));

    log.push({ step: `Adresse « ${payload.addNumber} ${payload.addStreet} »`, created: !addId });

    return { address, log };
  }

  /* ---------------------------------------------------------------- */

  private async findOrCreateCountry(name: string, log: PersistLog[]): Promise<Country> {
    const wanted = norm(name);
    const all = await firstValueFrom(this.countries.list());
    const found = all.find((c) => norm(c.cntName) === wanted);
    if (found) {
      log.push({ step: `Pays « ${found.cntName} »`, created: false });
      return found;
    }
    const created = await firstValueFrom(this.countries.create({ cntName: name.trim() }));
    log.push({ step: `Pays « ${created.cntName} »`, created: true });
    return created;
  }

  private async findOrCreatePostalCode(code: string, log: PersistLog[]): Promise<PostalCode> {
    const wanted = norm(code);
    if (!wanted) throw new Error('Le code postal est obligatoire.');

    const all = await firstValueFrom(this.postalCodes.list());
    const found = all.find((p) => norm(p.pCodeName) === wanted);
    if (found) {
      log.push({ step: `Code postal ${found.pCodeName}`, created: false });
      return found;
    }
    const created = await firstValueFrom(
      this.postalCodes.create({ pCodeName: code.trim() }),
    );
    log.push({ step: `Code postal ${created.pCodeName}`, created: true });
    return created;
  }

  private async findOrCreateCity(name: string, cntId: number, log: PersistLog[]): Promise<City> {
    const wanted = norm(name);
    if (!wanted) throw new Error('La ville est obligatoire.');

    const all = await firstValueFrom(this.cities.list());
    const found = all.find((c) => norm(c.cityName) === wanted);
    if (found) {
      log.push({ step: `Ville « ${found.cityName} »`, created: false });
      return found;
    }
    const created = await firstValueFrom(
      this.cities.create({ cityName: name.trim(), cntId }),
    );
    log.push({ step: `Ville « ${created.cityName} »`, created: true });
    return created;
  }

  /**
   * Garantit l'existence du lien `PostalCodeCity`, sans lequel le backend
   * refuse la création de l'adresse.
   */
  private async ensureLink(pCodeId: number, cityId: number, log: PersistLog[]): Promise<void> {
    const all = await firstValueFrom(this.links.list());
    const exists = all.some(
      (l) => l.postalCode?.pCodeId === pCodeId && l.city?.cityId === cityId,
    );
    if (exists) {
      log.push({ step: 'Association code postal / ville', created: false });
      return;
    }
    await firstValueFrom(this.links.create({ pCodeId, cityId }));
    log.push({ step: 'Association code postal / ville', created: true });
  }

  /** Met à plat une `Address` renvoyée par l'API, pour pré-remplir un formulaire. */
  static flatten(address: Address | null | undefined): FlatAddress {
    return {
      number: address?.addNumber ?? '',
      street: address?.addStreet ?? '',
      complement: address?.addComplement ?? '',
      postalCode: address?.postalCode?.pCodeName ?? '',
      city: address?.city?.cityName ?? '',
      country: address?.city?.country?.cntName ?? 'France',
    };
  }

  /** Représentation sur une ligne, pour les tableaux et les documents. */
  static format(address: Address | null | undefined): string {
    if (!address) return '—';
    const line1 = [address.addNumber, address.addStreet].filter(Boolean).join(' ').trim();
    const line2 = [address.postalCode?.pCodeName, address.city?.cityName]
      .filter(Boolean)
      .join(' ')
      .trim();
    const parts = [line1, address.addComplement, line2].filter((p) => !!p && p.length > 0);
    return parts.length ? titleize(parts.join(', ')) : '—';
  }
}

/** Normalise pour comparaison : minuscules, sans accents, espaces compactés. */
function norm(value: string | null | undefined): string {
  return (value ?? '')
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .trim()
    .toLowerCase()
    .replace(/\s+/g, ' ');
}

/** Le backend stocke en minuscules : on re-capitalise pour l'affichage. */
function titleize(value: string): string {
  return value.replace(/(^|[\s'’-])([a-zà-ÿ])/g, (_, sep, ch) => sep + ch.toUpperCase());
}

/** Utilitaire pour les composants qui veulent l'Observable brut. */
export type AddressStream = Observable<Address[]>;
