import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { AbstractControl } from '@angular/forms';
import { IconComponent } from './icon.component';

/**
 * Affiche le message d'erreur d'un contrôle de formulaire dès qu'il a été
 * touché ou que le formulaire a été soumis.
 */
@Component({
  selector: 'app-field-error',
  standalone: true,
  imports: [IconComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    @if (message()) {
      <p class="field-error">
        <app-icon name="alert" [size]="13" class="mt-px" />
        <span>{{ message() }}</span>
      </p>
    }
  `,
})
export class FieldErrorComponent {
  @Input({ required: true }) control!: AbstractControl | null;
  /** Nom lisible du champ, réutilisé dans les messages génériques. */
  @Input() label = 'Ce champ';
  /** Force l'affichage même si le contrôle n'a pas été touché. */
  @Input() submitted = false;

  message(): string | null {
    const c = this.control;
    if (!c || !c.errors) return null;
    if (!this.submitted && !c.touched && !c.dirty) return null;

    const e = c.errors;
    if (e['required']) return `${this.label} est obligatoire.`;
    if (e['email']) return 'Adresse e-mail invalide.';
    if (e['minlength'])
      return `${this.label} doit contenir au moins ${e['minlength'].requiredLength} caractères.`;
    if (e['maxlength'])
      return `${this.label} ne doit pas dépasser ${e['maxlength'].requiredLength} caractères.`;
    if (e['min']) return `La valeur minimale est ${e['min'].min}.`;
    if (e['max']) return `La valeur maximale est ${e['max'].max}.`;
    if (e['pattern']) return `${this.label} n'a pas le format attendu.`;
    if (e['phone']) return 'Numéro de téléphone invalide (format attendu : +33612345678).';
    if (e['postalCode']) return 'Code postal invalide (5 chiffres attendus).';
    if (e['custom']) return String(e['custom']);
    return `${this.label} est invalide.`;
  }
}
