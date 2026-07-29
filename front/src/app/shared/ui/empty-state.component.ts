import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { IconComponent } from './icon.component';

/** Message affiché lorsqu'une liste est vide ou qu'un filtre ne renvoie rien. */
@Component({
  selector: 'app-empty-state',
  standalone: true,
  imports: [IconComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="flex flex-col items-center justify-center px-6 py-14 text-center">
      <div
        class="mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-ink-100 text-ink-400 dark:bg-ink-800 dark:text-ink-500"
      >
        <app-icon [name]="icon" [size]="22" />
      </div>
      <p class="text-sm font-medium text-ink-800 dark:text-ink-200">{{ title }}</p>
      @if (message) {
        <p class="mt-1 max-w-sm text-sm muted">{{ message }}</p>
      }
      <div class="mt-5">
        <ng-content />
      </div>
    </div>
  `,
})
export class EmptyStateComponent {
  @Input() icon = 'inbox';
  @Input({ required: true }) title = '';
  @Input() message?: string;
}
