import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

/** En-tête de page : titre, sous-titre et zone d'actions à droite. */
@Component({
  selector: 'app-page-header',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <header class="mb-6 flex flex-wrap items-end justify-between gap-4">
      <div class="min-w-0">
        <h1 class="text-xl font-semibold tracking-tight sm:text-2xl">{{ title }}</h1>
        @if (subtitle) {
          <p class="mt-1 max-w-2xl text-sm muted">{{ subtitle }}</p>
        }
      </div>
      <div class="flex shrink-0 flex-wrap items-center gap-2">
        <ng-content />
      </div>
    </header>
  `,
})
export class PageHeaderComponent {
  @Input({ required: true }) title = '';
  @Input() subtitle?: string;
}
