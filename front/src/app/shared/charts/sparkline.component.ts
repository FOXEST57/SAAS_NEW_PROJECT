import { ChangeDetectionStrategy, Component, Input, computed, inject, signal } from '@angular/core';
import { ThemeService } from '../../core/services/theme.service';

/**
 * Micro-courbe d'accompagnement d'un indicateur.
 *
 * Une seule série : pas de légende, le titre de la tuile la nomme. Aucun axe,
 * aucune graduation — la sparkline donne la forme de la tendance, la valeur
 * chiffrée est portée par le nombre à côté.
 */
@Component({
  selector: 'app-sparkline',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <svg
      viewBox="0 0 120 32"
      preserveAspectRatio="none"
      class="h-8 w-full"
      role="img"
      [attr.aria-label]="ariaLabel"
    >
      @if (path(); as p) {
        <path [attr.d]="p.area" [attr.fill]="fillColor()" opacity="0.14" />
        <path
          [attr.d]="p.line"
          fill="none"
          [attr.stroke]="strokeColor()"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
        />
        <circle [attr.cx]="p.lastX" [attr.cy]="p.lastY" r="2.5" [attr.fill]="strokeColor()" />
      }
    </svg>
  `,
})
export class SparklineComponent {
  private readonly theme = inject(ThemeService);
  private readonly _values = signal<readonly number[]>([]);

  @Input({ required: true })
  set values(v: readonly number[] | null | undefined) {
    this._values.set(v ?? []);
  }

  /** Teinte de la courbe : suit la couleur de l'indicateur qu'elle accompagne. */
  @Input() tone: 'brand' | 'heat' | 'good' = 'brand';
  @Input() ariaLabel = 'Tendance sur la période';

  protected readonly path = computed(() => {
    const values = this._values();
    if (values.length < 2) return null;

    const min = Math.min(...values);
    const max = Math.max(...values);
    const span = max - min || 1;
    const stepX = 120 / (values.length - 1);

    const points = values.map((v, i) => ({
      x: i * stepX,
      // 3px de marge haute et basse pour que le trait ne soit jamais rogné.
      y: 29 - ((v - min) / span) * 26,
    }));

    const line = points.map((p, i) => `${i === 0 ? 'M' : 'L'}${p.x.toFixed(1)},${p.y.toFixed(1)}`).join(' ');
    const area = `${line} L120,32 L0,32 Z`;
    const last = points[points.length - 1];

    return { line, area, lastX: last.x, lastY: last.y };
  });

  protected strokeColor(): string {
    const dark = this.theme.isDark();
    return {
      brand: dark ? '#3987e5' : '#2579eb',
      heat: dark ? '#d95926' : '#ef6008',
      good: dark ? '#34d399' : '#047857',
    }[this.tone];
  }

  protected fillColor(): string {
    return this.strokeColor();
  }
}
