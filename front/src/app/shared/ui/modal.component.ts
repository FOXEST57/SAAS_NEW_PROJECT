import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  HostListener,
  Input,
  Output,
} from '@angular/core';
import { IconComponent } from './icon.component';

/**
 * Fenêtre modale générique.
 *
 * Utilisation :
 * ```html
 * <app-modal title="Nouveau client" (closed)="close()">
 *   <form>…</form>
 *   <div footer>…boutons…</div>
 * </app-modal>
 * ```
 */
@Component({
  selector: 'app-modal',
  standalone: true,
  imports: [IconComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="fixed inset-0 z-50 flex items-start justify-center overflow-y-auto p-4 sm:p-6">
      <!-- Voile -->
      <div
        class="fixed inset-0 bg-ink-950/40 backdrop-blur-[2px] animate-fade-in"
        (click)="requestClose()"
        aria-hidden="true"
      ></div>

      <!-- Panneau -->
      <div
        class="relative z-10 my-4 w-full animate-scale-in rounded-xl border border-ink-200 bg-white shadow-pop dark:border-ink-700 dark:bg-ink-900"
        [class]="widthClass"
        role="dialog"
        aria-modal="true"
      >
        <div
          class="flex items-start justify-between gap-4 border-b border-ink-200 px-5 py-4 dark:border-ink-800"
        >
          <div class="min-w-0">
            <h2 class="text-base font-semibold">{{ title }}</h2>
            @if (subtitle) {
              <p class="mt-0.5 text-[13px] muted">{{ subtitle }}</p>
            }
          </div>
          <button type="button" class="btn-icon -mr-1 -mt-1" (click)="requestClose()" aria-label="Fermer">
            <app-icon name="close" [size]="18" />
          </button>
        </div>

        <div class="max-h-[calc(100vh-16rem)] overflow-y-auto px-5 py-5">
          <ng-content />
        </div>

        <div
          class="flex flex-wrap items-center justify-end gap-2 border-t border-ink-200 bg-ink-50/60 px-5 py-3.5 dark:border-ink-800 dark:bg-ink-950/40"
        >
          <ng-content select="[footer]" />
        </div>
      </div>
    </div>
  `,
})
export class ModalComponent {
  @Input({ required: true }) title = '';
  @Input() subtitle?: string;
  /** Largeur maximale du panneau (classe Tailwind). */
  @Input() widthClass = 'max-w-2xl';
  @Output() closed = new EventEmitter<void>();

  @HostListener('document:keydown.escape')
  requestClose(): void {
    this.closed.emit();
  }
}
