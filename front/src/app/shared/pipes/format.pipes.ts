import { Pipe, PipeTransform } from '@angular/core';
import { Address } from '../../core/models/api.models';
import { AddressResolverService } from '../../core/services/address-resolver.service';

const EUR = new Intl.NumberFormat('fr-FR', {
  style: 'currency',
  currency: 'EUR',
  minimumFractionDigits: 2,
  maximumFractionDigits: 2,
});

/** Montant en euros, format français (1 234,56 €). */
@Pipe({ name: 'eur', standalone: true })
export class EurPipe implements PipeTransform {
  transform(value: number | string | null | undefined): string {
    const n = typeof value === 'string' ? Number(value) : value;
    if (n === null || n === undefined || Number.isNaN(n)) return '—';
    return EUR.format(n);
  }
}

/**
 * Taux de TVA en pourcentage.
 * Le backend stocke une fraction (0.2) : on gère aussi le cas où un taux
 * aurait été saisi en points (20) pour rester tolérant.
 */
@Pipe({ name: 'tauxPct', standalone: true })
export class TauxPctPipe implements PipeTransform {
  transform(value: number | string | null | undefined): string {
    const n = typeof value === 'string' ? Number(value) : value;
    if (n === null || n === undefined || Number.isNaN(n)) return '—';
    const pct = n <= 1 ? n * 100 : n;
    return `${new Intl.NumberFormat('fr-FR', { maximumFractionDigits: 2 }).format(pct)} %`;
  }
}

/** Date longue française, à partir d'un `LocalDateTime` sérialisé. */
@Pipe({ name: 'frDate', standalone: true })
export class FrDatePipe implements PipeTransform {
  transform(value: string | null | undefined, withTime = false): string {
    if (!value) return '—';
    const d = new Date(value);
    if (Number.isNaN(d.getTime())) return '—';
    return d.toLocaleDateString('fr-FR', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
      ...(withTime ? { hour: '2-digit', minute: '2-digit' } : {}),
    });
  }
}

/** Adresse complète sur une ligne. */
@Pipe({ name: 'addressLine', standalone: true })
export class AddressLinePipe implements PipeTransform {
  transform(value: Address | null | undefined): string {
    return AddressResolverService.format(value);
  }
}

/**
 * Recapitalise les libellés stockés en minuscules par le backend
 * (`LowercaseConverter`).
 */
@Pipe({ name: 'capitalize', standalone: true })
export class CapitalizePipe implements PipeTransform {
  transform(value: string | null | undefined): string {
    if (!value) return '—';
    return value.replace(/(^|[\s'’\-/])([\p{Ll}])/gu, (_, sep: string, ch: string) => sep + ch.toUpperCase());
  }
}

/** Référence de document affichée en majuscules. */
@Pipe({ name: 'ref', standalone: true })
export class RefPipe implements PipeTransform {
  transform(value: string | null | undefined): string {
    return (value ?? '—').toUpperCase();
  }
}
