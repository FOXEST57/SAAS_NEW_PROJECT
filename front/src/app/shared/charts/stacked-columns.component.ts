import {
  ChangeDetectionStrategy,
  Component,
  Input,
  computed,
  inject,
  signal,
} from '@angular/core';
import { ThemeService } from '../../core/services/theme.service';
import { SERIES } from './viz-tokens';

export interface ColumnSegment {
  readonly key: 'clim' | 'chauffage' | 'autre';
  readonly label: string;
  readonly value: number;
}

export interface ColumnDatum {
  /** Libellé court affiché sous la colonne (ex. « janv. »). */
  readonly label: string;
  /** Libellé complet, utilisé dans l'infobulle et le tableau. */
  readonly fullLabel: string;
  readonly segments: readonly ColumnSegment[];
}

const W = 720;
const H = 260;
const PAD = { top: 16, right: 8, bottom: 30, left: 52 };

/**
 * Colonnes empilées : répartition d'une grandeur dans le temps, ventilée par
 * famille de produits.
 *
 * Forme choisie parce que la donnée fait deux métiers à la fois — évolution
 * dans le temps **et** part de chaque famille. Deux séries seulement, donc la
 * couleur catégorielle est confortable et les libellés directs suffisent.
 *
 * Un seul axe des ordonnées, jamais deux : les deux séries partagent l'euro
 * comme unité.
 */
@Component({
  selector: 'app-stacked-columns',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <figure class="m-0">
      <!-- Légende : toujours présente dès deux séries -->
      <figcaption class="mb-3 flex flex-wrap items-center gap-x-4 gap-y-1.5">
        @for (s of legend(); track s.key) {
          <span class="inline-flex items-center gap-1.5 text-[12.5px] text-ink-600 dark:text-ink-300">
            <span class="h-2.5 w-2.5 shrink-0 rounded-[2px]" [style.background]="s.color"></span>
            {{ s.label }}
          </span>
        }
        <button
          type="button"
          class="ml-auto text-[12px] text-ink-500 underline-offset-2 hover:underline dark:text-ink-400"
          (click)="showTable.set(!showTable())"
        >
          {{ showTable() ? 'Voir le graphique' : 'Voir les données' }}
        </button>
      </figcaption>

      @if (showTable()) {
        <!-- Vue tabulaire : l'identité ne dépend jamais de la seule couleur -->
        <div class="table-wrap max-h-64 overflow-y-auto rounded-lg border border-ink-200 dark:border-ink-800">
          <table class="table text-[13px]">
            <thead>
              <tr>
                <th>Période</th>
                @for (s of legend(); track s.key) {
                  <th class="text-right">{{ s.label }}</th>
                }
                <th class="text-right">Total</th>
              </tr>
            </thead>
            <tbody>
              @for (d of data; track d.label) {
                <tr>
                  <td>{{ d.fullLabel }}</td>
                  @for (s of legend(); track s.key) {
                    <td class="num text-right">{{ format(valueOf(d, s.key)) }}</td>
                  }
                  <td class="num text-right font-semibold">{{ format(totalOf(d)) }}</td>
                </tr>
              }
            </tbody>
          </table>
        </div>
      } @else {
        <div class="relative">
          <svg
            [attr.viewBox]="'0 0 ' + W + ' ' + H"
            class="h-auto w-full"
            role="img"
            [attr.aria-label]="ariaLabel()"
            (mouseleave)="active.set(null)"
          >
            <!-- Grille, volontairement discrète -->
            @for (t of ticks(); track t.value) {
              <line
                [attr.x1]="pad.left"
                [attr.x2]="W - pad.right"
                [attr.y1]="t.y"
                [attr.y2]="t.y"
                [attr.stroke]="gridColor()"
                stroke-width="1"
              />
              <text
                [attr.x]="pad.left - 8"
                [attr.y]="t.y + 4"
                text-anchor="end"
                [attr.fill]="mutedColor()"
                font-size="11"
              >
                {{ t.label }}
              </text>
            }

            <!-- Colonnes -->
            @for (col of columns(); track col.label; let i = $index) {
              <g>
                @for (seg of col.rects; track seg.key) {
                  <rect
                    [attr.x]="col.x"
                    [attr.y]="seg.y"
                    [attr.width]="col.width"
                    [attr.height]="seg.height"
                    [attr.fill]="seg.color"
                    [attr.rx]="seg.top ? 4 : 0"
                    [attr.opacity]="active() === null || active() === i ? 1 : 0.35"
                  />
                }
                <!-- Cible de survol : toute la colonne, plus large que les marques -->
                <rect
                  [attr.x]="col.x - gap / 2"
                  [attr.y]="pad.top"
                  [attr.width]="col.width + gap"
                  [attr.height]="H - pad.top - pad.bottom"
                  fill="transparent"
                  (mouseenter)="active.set(i)"
                />
                <text
                  [attr.x]="col.x + col.width / 2"
                  [attr.y]="H - pad.bottom + 16"
                  text-anchor="middle"
                  [attr.fill]="active() === i ? strongColor() : mutedColor()"
                  font-size="11"
                >
                  {{ col.label }}
                </text>
              </g>
            }

            <!-- Ligne de base -->
            <line
              [attr.x1]="pad.left"
              [attr.x2]="W - pad.right"
              [attr.y1]="H - pad.bottom"
              [attr.y2]="H - pad.bottom"
              [attr.stroke]="axisColor()"
              stroke-width="1"
            />
          </svg>

          <!-- Infobulle en HTML : plus lisible et plus simple à positionner -->
          @if (activeColumn(); as col) {
            <div
              class="pointer-events-none absolute top-2 z-10 min-w-[9rem] -translate-x-1/2 rounded-lg border border-ink-200 bg-white px-3 py-2 text-[12.5px] shadow-pop dark:border-ink-700 dark:bg-ink-900"
              [style.left.%]="col.centerPct"
            >
              <p class="mb-1 font-semibold text-ink-900 dark:text-ink-100">{{ col.fullLabel }}</p>
              @for (s of col.breakdown; track s.key) {
                <p class="flex items-center gap-2 text-ink-600 dark:text-ink-300">
                  <span class="h-2 w-2 shrink-0 rounded-[2px]" [style.background]="s.color"></span>
                  <span class="flex-1">{{ s.label }}</span>
                  <span class="num font-medium text-ink-900 dark:text-ink-100">{{ format(s.value) }}</span>
                </p>
              }
              <p class="mt-1 flex justify-between border-t border-ink-200 pt-1 font-semibold dark:border-ink-700">
                <span>Total</span>
                <span class="num">{{ format(col.total) }}</span>
              </p>
            </div>
          }
        </div>
      }
    </figure>
  `,
})
export class StackedColumnsComponent {
  private readonly theme = inject(ThemeService);

  @Input({ required: true }) data: readonly ColumnDatum[] = [];
  /** Séries à afficher, dans l'ordre fixe d'attribution des couleurs. */
  @Input() series: readonly { key: 'clim' | 'chauffage' | 'autre'; label: string }[] = [
    { key: 'clim', label: 'Climatisation' },
    { key: 'chauffage', label: 'Chauffage' },
  ];

  protected readonly W = W;
  protected readonly H = H;
  protected readonly pad = PAD;
  protected readonly gap = 6;
  protected readonly active = signal<number | null>(null);
  protected readonly showTable = signal(false);

  private readonly dark = computed(() => this.theme.isDark());

  protected readonly legend = computed(() =>
    this.series.map((s) => ({ ...s, color: this.colorOf(s.key) })),
  );

  private readonly maxValue = computed(() => {
    const max = Math.max(0, ...this.data.map((d) => this.totalOf(d)));
    if (max === 0) return 1;
    // Arrondi à un palier lisible plutôt qu'à la valeur brute.
    const magnitude = Math.pow(10, Math.floor(Math.log10(max)));
    return Math.ceil(max / (magnitude / 2)) * (magnitude / 2);
  });

  protected readonly ticks = computed(() => {
    const max = this.maxValue();
    const plot = H - PAD.top - PAD.bottom;
    return [0, 0.25, 0.5, 0.75, 1].map((f) => ({
      value: max * f,
      y: PAD.top + plot * (1 - f),
      label: this.compact(max * f),
    }));
  });

  protected readonly columns = computed(() => {
    const n = this.data.length || 1;
    const plotW = W - PAD.left - PAD.right;
    const plotH = H - PAD.top - PAD.bottom;
    const slot = plotW / n;
    const width = Math.max(6, Math.min(46, slot - this.gap));
    const max = this.maxValue();

    return this.data.map((d, i) => {
      const x = PAD.left + slot * i + (slot - width) / 2;
      let cursor = H - PAD.bottom;

      // On empile du bas vers le haut, dans l'ordre fixe des séries.
      const stack = this.series
        .map((s) => ({ ...s, value: this.valueOf(d, s.key) }))
        .filter((s) => s.value > 0);

      const rects = stack.map((s, idx) => {
        const raw = (s.value / max) * plotH;
        // 2px de surface entre segments : la séparation reste lisible même
        // quand deux valeurs voisines ont des couleurs proches.
        const height = Math.max(1, raw - (idx < stack.length - 1 ? 2 : 0));
        const y = cursor - height;
        cursor -= raw;
        return {
          key: s.key,
          color: this.colorOf(s.key),
          y,
          height,
          top: idx === stack.length - 1,
        };
      });

      return {
        label: d.label,
        fullLabel: d.fullLabel,
        x,
        width,
        rects,
        centerPct: ((x + width / 2) / W) * 100,
      };
    });
  });

  protected readonly activeColumn = computed(() => {
    const i = this.active();
    if (i === null) return null;
    const d = this.data[i];
    const col = this.columns()[i];
    if (!d || !col) return null;
    return {
      fullLabel: d.fullLabel,
      centerPct: col.centerPct,
      total: this.totalOf(d),
      breakdown: this.series.map((s) => ({
        key: s.key,
        label: s.label,
        color: this.colorOf(s.key),
        value: this.valueOf(d, s.key),
      })),
    };
  });

  protected ariaLabel(): string {
    const total = this.data.reduce((s, d) => s + this.totalOf(d), 0);
    return `Répartition par période et par famille de produits, total ${this.format(total)}. Le détail chiffré est disponible via le bouton « Voir les données ».`;
  }

  protected valueOf(d: ColumnDatum, key: string): number {
    return d.segments.find((s) => s.key === key)?.value ?? 0;
  }

  protected totalOf(d: ColumnDatum): number {
    return this.series.reduce((s, serie) => s + this.valueOf(d, serie.key), 0);
  }

  private colorOf(key: 'clim' | 'chauffage' | 'autre'): string {
    return SERIES[key][this.dark() ? 'dark' : 'light'];
  }

  protected gridColor(): string {
    return this.dark() ? '#3a4354' : '#eceef2';
  }

  protected axisColor(): string {
    return this.dark() ? '#51617b' : '#d5dae3';
  }

  protected mutedColor(): string {
    return this.dark() ? '#8596ae' : '#667894';
  }

  protected strongColor(): string {
    return this.dark() ? '#eceef2' : '#22272f';
  }

  protected format(v: number): string {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'EUR',
      maximumFractionDigits: 0,
    }).format(v);
  }

  /** Format compact pour les graduations : 12 k€ plutôt que 12 000 €. */
  private compact(v: number): string {
    if (v === 0) return '0';
    if (Math.abs(v) >= 1000) {
      return `${new Intl.NumberFormat('fr-FR', { maximumFractionDigits: 1 }).format(v / 1000)} k`;
    }
    return new Intl.NumberFormat('fr-FR', { maximumFractionDigits: 0 }).format(v);
  }
}
