import { ChangeDetectionStrategy, Component, Input, computed, inject } from '@angular/core';
import { ThemeService } from '../../core/services/theme.service';
import { FUNNEL_RAMP } from './viz-tokens';

export interface FunnelStep {
  readonly key: string;
  readonly label: string;
  /** Montant cumulé à cette étape. */
  readonly value: number;
  /** Nombre de documents à cette étape. */
  readonly count: number;
}

/**
 * Entonnoir commercial.
 *
 * Les étapes ont un **ordre intrinsèque** : inverser Devis et Facture
 * changerait le sens. C'est donc un encodage ordinal — une seule teinte, du
 * clair au foncé — et non catégoriel. Le lecteur voit la progression dans la
 * couleur elle-même.
 *
 * Barres horizontales : les libellés d'étape sont longs, et le taux de
 * conversion se lit naturellement de haut en bas.
 */
@Component({
  selector: 'app-funnel',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <figure class="m-0 space-y-2.5">
      @for (row of rows(); track row.key; let i = $index) {
        <div class="group">
          <div class="mb-1 flex items-baseline justify-between gap-3">
            <span class="flex items-center gap-2 text-[13px] font-medium text-ink-800 dark:text-ink-200">
              <span class="h-2.5 w-2.5 shrink-0 rounded-[2px]" [style.background]="row.color"></span>
              {{ row.label }}
              <span class="text-[12px] font-normal text-ink-500 dark:text-ink-400">
                {{ row.count }} atteints
              </span>
            </span>
            <!-- Libellé direct : jamais de valeur lisible à la seule couleur -->
            <span class="num shrink-0 text-[13px] font-semibold text-ink-900 dark:text-ink-100">
              {{ format(row.value) }}
            </span>
          </div>

          <div class="relative h-2.5 overflow-hidden rounded-full" [style.background]="trackColor()">
            <div
              class="h-full rounded-full transition-[width] duration-500 ease-out"
              [style.width.%]="row.pct"
              [style.background]="row.color"
            ></div>
          </div>

          @if (row.conversion !== null) {
            <p class="mt-1 text-[11.5px] text-ink-500 dark:text-ink-400">
              {{ row.conversion }} % de l'étape précédente
            </p>
          }
        </div>
      } @empty {
        <p class="py-6 text-center text-[13px] text-ink-500 dark:text-ink-400">
          Aucun document dans le pipeline.
        </p>
      }
    </figure>
  `,
})
export class FunnelComponent {
  private readonly theme = inject(ThemeService);

  @Input({ required: true }) steps: readonly FunnelStep[] = [];

  protected readonly rows = computed(() => {
    const ramp = FUNNEL_RAMP[this.theme.isDark() ? 'dark' : 'light'];
    const max = Math.max(1, ...this.steps.map((s) => s.value));

    return this.steps.map((s, i) => {
      const previous = i > 0 ? this.steps[i - 1].value : null;
      return {
        ...s,
        color: ramp[Math.min(i, ramp.length - 1)],
        pct: Math.max(1.5, (s.value / max) * 100),
        conversion:
          previous && previous > 0 ? Math.round((s.value / previous) * 100) : null,
      };
    });
  });

  protected trackColor(): string {
    return this.theme.isDark() ? '#3a4354' : '#eceef2';
  }

  protected format(v: number): string {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'EUR',
      maximumFractionDigits: 0,
    }).format(v);
  }
}
