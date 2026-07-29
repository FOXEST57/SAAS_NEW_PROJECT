import { ChangeDetectionStrategy, Component, Input, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { NavigationHistoryService } from '../../core/services/navigation-history.service';
import { IconComponent } from './icon.component';

/**
 * Lien de retour qui ramène l'utilisateur d'où il vient réellement.
 *
 * Le libellé annonce la destination — « Retour au pipeline », « Retour aux
 * devis & factures » — plutôt qu'un « Retour » muet : on sait où l'on va avant
 * de cliquer.
 */
@Component({
  selector: 'app-back-link',
  standalone: true,
  imports: [RouterLink, IconComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <a [routerLink]="target().url" class="btn-ghost">
      <app-icon name="arrowLeft" [size]="16" />
      Retour {{ target().label }}
    </a>
  `,
})
export class BackLinkComponent {
  private readonly history = inject(NavigationHistoryService);
  private readonly _fallbackUrl = signal('/tableau-de-bord');
  private readonly _fallbackLabel = signal<string | undefined>(undefined);

  /** Destination si l'utilisateur arrive directement, sans historique. */
  @Input()
  set fallbackUrl(value: string) {
    this._fallbackUrl.set(value);
  }

  @Input()
  set fallbackLabel(value: string | undefined) {
    this._fallbackLabel.set(value);
  }

  protected readonly target = computed(() =>
    this.history.target(this._fallbackUrl(), this._fallbackLabel()),
  );
}
