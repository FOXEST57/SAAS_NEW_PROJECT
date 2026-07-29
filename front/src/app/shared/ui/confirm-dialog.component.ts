import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ConfirmService } from '../../core/services/confirm.service';
import { IconComponent } from './icon.component';

/** Hôte unique de la boîte de confirmation, monté dans le shell. */
@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  imports: [IconComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    @if (confirm.pending(); as c) {
      <div class="fixed inset-0 z-[60] flex items-center justify-center p-4">
        <div
          class="fixed inset-0 animate-fade-in bg-ink-950/50 backdrop-blur-[2px]"
          (click)="answer(false)"
          aria-hidden="true"
        ></div>

        <div
          class="relative z-10 w-full max-w-md animate-scale-in rounded-xl border border-ink-200 bg-white p-5 shadow-pop dark:border-ink-700 dark:bg-ink-900"
          role="alertdialog"
          aria-modal="true"
        >
          <div class="flex gap-4">
            <div
              class="flex h-10 w-10 shrink-0 items-center justify-center rounded-full"
              [class]="
                c.danger
                  ? 'bg-red-100 text-red-600 dark:bg-red-500/15 dark:text-red-400'
                  : 'bg-brand-100 text-brand-600 dark:bg-brand-500/15 dark:text-brand-400'
              "
            >
              <app-icon [name]="c.danger ? 'alert' : 'info'" [size]="20" />
            </div>
            <div class="min-w-0 pt-0.5">
              <h3 class="text-base font-semibold">{{ c.title }}</h3>
              <p class="mt-1.5 text-sm muted">{{ c.message }}</p>
            </div>
          </div>

          <div class="mt-6 flex justify-end gap-2">
            <button type="button" class="btn-secondary" (click)="answer(false)">
              {{ c.cancelLabel || 'Annuler' }}
            </button>
            <button
              type="button"
              [class]="c.danger ? 'btn-danger' : 'btn-primary'"
              (click)="answer(true)"
              autofocus
            >
              {{ c.confirmLabel || 'Confirmer' }}
            </button>
          </div>
        </div>
      </div>
    }
  `,
})
export class ConfirmDialogComponent {
  protected readonly confirm = inject(ConfirmService);

  answer(value: boolean): void {
    this.confirm.answer(value);
  }
}
