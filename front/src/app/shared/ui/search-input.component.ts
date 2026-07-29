import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { IconComponent } from './icon.component';

/** Champ de recherche avec icône et bouton d'effacement. */
@Component({
  selector: 'app-search-input',
  standalone: true,
  imports: [IconComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="relative w-full sm:w-72">
      <span class="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-ink-400">
        <app-icon name="search" [size]="16" />
      </span>
      <input
        type="search"
        class="input pl-9 pr-8"
        [placeholder]="placeholder"
        [value]="value"
        (input)="onInput($event)"
        [attr.aria-label]="placeholder"
      />
      @if (value) {
        <button
          type="button"
          class="absolute right-2 top-1/2 -translate-y-1/2 rounded p-1 text-ink-400 hover:text-ink-700 dark:hover:text-ink-200"
          (click)="clear()"
          aria-label="Effacer"
        >
          <app-icon name="close" [size]="14" />
        </button>
      }
    </div>
  `,
})
export class SearchInputComponent {
  @Input() placeholder = 'Rechercher…';
  @Input() value = '';
  @Output() valueChange = new EventEmitter<string>();

  onInput(event: Event): void {
    this.valueChange.emit((event.target as HTMLInputElement).value);
  }

  clear(): void {
    this.valueChange.emit('');
  }
}
