import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { SparklineComponent } from '../../shared/charts/sparkline.component';
import { IconComponent } from '../../shared/ui/icon.component';

/**
 * Tuile d'indicateur : une valeur, sa variation, sa tendance.
 *
 * Une valeur unique n'est pas un graphique — c'est un nombre mis en scène. La
 * sparkline apporte la forme de la tendance, jamais un axe ni une graduation.
 */
@Component({
  selector: 'app-stat-tile',
  standalone: true,
  imports: [IconComponent, SparklineComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="card card-pad flex h-full flex-col">
      <div class="flex items-start justify-between gap-3">
        <div class="min-w-0">
          <p class="text-[13px] muted">{{ label }}</p>
          <p class="kpi-value mt-1.5 truncate">{{ value }}</p>
        </div>
        <span class="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg" [class]="tone">
          <app-icon [name]="icon" [size]="19" />
        </span>
      </div>

      @if (trend?.length) {
        <div class="mt-3">
          <app-sparkline [values]="trend!" [tone]="sparkTone" [ariaLabel]="label + ' — tendance sur 12 mois'" />
        </div>
      }

      <p class="mt-auto flex items-center gap-1.5 pt-2 text-[12.5px]" [class]="hintClass">
        @if (delta !== null && delta !== undefined) {
          <app-icon [name]="delta >= 0 ? 'trendUp' : 'trendDown'" [size]="13" />
        }
        <span>{{ hint }}</span>
      </p>
    </div>
  `,
})
export class StatTileComponent {
  @Input({ required: true }) label = '';
  @Input({ required: true }) value = '';
  @Input() icon = 'euro';
  @Input() tone = 'bg-ink-100 text-ink-600 dark:bg-ink-800 dark:text-ink-300';
  @Input() hint = '';
  @Input() hintClass = 'muted';
  @Input() trend: number[] | null = null;
  @Input() sparkTone: 'brand' | 'heat' | 'good' = 'brand';
  /** Variation par rapport à la période précédente, en fraction. */
  @Input() delta: number | null = null;
}
