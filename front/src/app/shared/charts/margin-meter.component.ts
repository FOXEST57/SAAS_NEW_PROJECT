import { ChangeDetectionStrategy, Component, Input, computed, inject, signal } from '@angular/core';
import { ThemeService } from '../../core/services/theme.service';
import { IconComponent } from '../ui/icon.component';
import { STATUS_COLORS } from './viz-tokens';

/**
 * Jauge de taux de marge, comparé à un seuil cible.
 *
 * Un ratio unique confronté à une limite appelle une jauge, pas un camembert à
 * deux parts. La couleur d'état est réservée et toujours doublée d'une icône et
 * d'un libellé — jamais l'état par la seule couleur.
 */
@Component({
  selector: 'app-margin-meter',
  standalone: true,
  imports: [IconComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="space-y-2">
      <div class="flex items-baseline justify-between gap-3">
        <span class="text-[13px] text-ink-500 dark:text-ink-400">{{ label }}</span>
        <span class="flex items-center gap-1.5">
          <span [style.color]="state().color"><app-icon [name]="state().icon" [size]="14" /></span>
          <span class="num text-lg font-semibold text-ink-950 dark:text-white">
            {{ current() === null ? '—' : percent(current()!) }}
          </span>
        </span>
      </div>

      <div class="relative h-2 overflow-hidden rounded-full" [style.background]="trackColor()">
        <div
          class="h-full rounded-full transition-[width] duration-500 ease-out"
          [style.width.%]="fill()"
          [style.background]="state().color"
        ></div>
        <!-- Repère du seuil -->
        <div
          class="absolute top-0 h-full w-0.5"
          [style.left.%]="targetPct()"
          [style.background]="markerColor()"
          [attr.title]="'Seuil : ' + percent(target)"
        ></div>
      </div>

      <p class="text-[12px] text-ink-500 dark:text-ink-400">
        <span [style.color]="state().color" class="font-medium">{{ state().label }}</span>
        · seuil {{ percent(target) }}
        @if (coverage !== null && coverage < 0.999) {
          · coût connu sur {{ percent(coverage) }} du CA
        }
      </p>
    </div>
  `,
})
export class MarginMeterComponent {
  private readonly theme = inject(ThemeService);

  private readonly _rate = signal<number | null>(null);

  @Input() label = 'Taux de marge';
  /** Seuil de rentabilité attendu, en fraction (0.25 = 25 %). */
  @Input() target = 0.25;
  /** Part du chiffre d'affaires pour laquelle un coût d'achat est connu. */
  @Input() coverage: number | null = null;
  /** Échelle haute de la jauge. */
  @Input() ceiling = 0.6;

  @Input({ required: true })
  set rate(value: number | null) {
    this._rate.set(value);
  }

  protected readonly current = this._rate.asReadonly();

  protected readonly fill = computed(() => {
    const r = this._rate();
    if (r === null) return 0;
    return Math.max(0, Math.min(100, (r / this.ceiling) * 100));
  });

  protected readonly targetPct = computed(() =>
    Math.max(0, Math.min(100, (this.target / this.ceiling) * 100)),
  );

  protected readonly state = computed(() => {
    const mode = this.theme.isDark() ? 'dark' : 'light';
    const r = this._rate();

    if (r === null) {
      return {
        color: this.theme.isDark() ? '#8596ae' : '#667894',
        icon: 'info',
        label: 'Coût inconnu',
      };
    }
    if (r >= this.target) {
      return { color: STATUS_COLORS.good[mode], icon: 'checkCircle', label: 'Au-dessus du seuil' };
    }
    if (r >= this.target * 0.6) {
      return { color: STATUS_COLORS.warning[mode], icon: 'alert', label: 'Marge faible' };
    }
    return { color: STATUS_COLORS.critical[mode], icon: 'alert', label: 'Sous le seuil' };
  });

  protected trackColor(): string {
    return this.theme.isDark() ? '#3a4354' : '#eceef2';
  }

  protected markerColor(): string {
    return this.theme.isDark() ? '#b0bbcb' : '#51617b';
  }

  protected percent(v: number): string {
    return `${new Intl.NumberFormat('fr-FR', { maximumFractionDigits: 1 }).format(v * 100)} %`;
  }
}
