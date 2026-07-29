import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

/**
 * Validateur de numéro de téléphone aligné sur le `@ValidPhoneNumber` du
 * backend (basé sur libphonenumber).
 *
 * Accepte le format international (`+33612345678`) et le format national
 * français (`0612345678`, avec ou sans séparateurs) — ce dernier étant
 * normalisé avant envoi par `toE164()`.
 */
export const phoneValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const raw = (control.value ?? '').toString().trim();
  if (!raw) return null; // `required` s'en charge

  const compact = raw.replace(/[\s.\-()]/g, '');
  const international = /^\+[1-9]\d{7,14}$/;
  const frenchNational = /^0[1-9]\d{8}$/;

  return international.test(compact) || frenchNational.test(compact) ? null : { phone: true };
};

/**
 * Normalise un numéro français au format E.164 attendu par le backend.
 * `06 12 34 56 78` → `+33612345678`.
 */
export function toE164(value: string, countryCode = '33'): string {
  const compact = (value ?? '').replace(/[\s.\-()]/g, '');
  if (compact.startsWith('+')) return compact;
  if (compact.startsWith('00')) return `+${compact.slice(2)}`;
  if (/^0[1-9]\d{8}$/.test(compact)) return `+${countryCode}${compact.slice(1)}`;
  return compact;
}

/** Code postal : 4 à 10 caractères alphanumériques (tolère l'international). */
export const postalCodeValidator: ValidatorFn = (
  control: AbstractControl,
): ValidationErrors | null => {
  const raw = (control.value ?? '').toString().trim();
  if (!raw) return null;
  return /^[0-9A-Za-z\- ]{4,10}$/.test(raw) ? null : { postalCode: true };
};

/** Interdit une chaîne composée uniquement d'espaces (miroir de `@NotBlank`). */
export const notBlankValidator: ValidatorFn = (
  control: AbstractControl,
): ValidationErrors | null => {
  const raw = control.value;
  if (raw === null || raw === undefined || raw === '') return null;
  return raw.toString().trim().length > 0 ? null : { required: true };
};
