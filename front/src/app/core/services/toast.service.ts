import { Injectable, signal } from '@angular/core';

export type ToastKind = 'success' | 'error' | 'info' | 'warning';

export interface Toast {
  readonly id: number;
  readonly kind: ToastKind;
  readonly title: string;
  readonly detail?: string;
}

/** File de notifications éphémères affichée par `ToastHost`. */
@Injectable({ providedIn: 'root' })
export class ToastService {
  private seq = 0;
  readonly toasts = signal<Toast[]>([]);

  success(title: string, detail?: string): void {
    this.push('success', title, detail, 3500);
  }

  info(title: string, detail?: string): void {
    this.push('info', title, detail, 3500);
  }

  warning(title: string, detail?: string): void {
    this.push('warning', title, detail, 5000);
  }

  error(title: string, detail?: string): void {
    this.push('error', title, detail, 7000);
  }

  dismiss(id: number): void {
    this.toasts.update((list) => list.filter((t) => t.id !== id));
  }

  private push(kind: ToastKind, title: string, detail: string | undefined, ttl: number): void {
    const id = ++this.seq;
    this.toasts.update((list) => [...list, { id, kind, title, detail }]);
    setTimeout(() => this.dismiss(id), ttl);
  }
}
