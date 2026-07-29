import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ToastKind, ToastService } from '../../core/services/toast.service';
import { IconComponent } from './icon.component';

/** Pile de notifications, en bas à droite. */
@Component({
  selector: 'app-toast-host',
  standalone: true,
  imports: [IconComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div
      class="pointer-events-none fixed bottom-4 right-4 z-[70] flex w-full max-w-sm flex-col gap-2"
      aria-live="polite"
    >
      @for (t of toasts.toasts(); track t.id) {
        <div
          class="pointer-events-auto flex animate-slide-in-right items-start gap-3 rounded-lg border bg-white p-3.5 shadow-pop dark:bg-ink-900"
          [class]="border(t.kind)"
        >
          <span [class]="iconColor(t.kind)">
            <app-icon [name]="iconName(t.kind)" [size]="18" />
          </span>
          <div class="min-w-0 flex-1">
            <p class="text-sm font-medium text-ink-900 dark:text-ink-100">{{ t.title }}</p>
            @if (t.detail) {
              <p class="mt-0.5 break-words text-[13px] muted">{{ t.detail }}</p>
            }
          </div>
          <button
            type="button"
            class="btn-icon -mr-1 -mt-1 h-7 w-7"
            (click)="toasts.dismiss(t.id)"
            aria-label="Fermer"
          >
            <app-icon name="close" [size]="15" />
          </button>
        </div>
      }
    </div>
  `,
})
export class ToastHostComponent {
  protected readonly toasts = inject(ToastService);

  iconName(kind: ToastKind): string {
    return { success: 'checkCircle', error: 'alert', warning: 'alert', info: 'info' }[kind];
  }

  iconColor(kind: ToastKind): string {
    return {
      success: 'text-emerald-600 dark:text-emerald-400',
      error: 'text-red-600 dark:text-red-400',
      warning: 'text-amber-600 dark:text-amber-400',
      info: 'text-brand-600 dark:text-brand-400',
    }[kind];
  }

  border(kind: ToastKind): string {
    return {
      success: 'border-emerald-200 dark:border-emerald-500/30',
      error: 'border-red-200 dark:border-red-500/30',
      warning: 'border-amber-200 dark:border-amber-500/30',
      info: 'border-brand-200 dark:border-brand-500/30',
    }[kind];
  }
}
