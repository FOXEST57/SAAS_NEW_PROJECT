import { Injectable, signal } from '@angular/core';

export interface ConfirmOptions {
  title: string;
  message: string;
  confirmLabel?: string;
  cancelLabel?: string;
  danger?: boolean;
}

interface PendingConfirm extends ConfirmOptions {
  resolve: (value: boolean) => void;
}

/**
 * Boîte de dialogue de confirmation centralisée.
 * Le composant `ConfirmDialog` (monté une fois dans le shell) l'observe.
 */
@Injectable({ providedIn: 'root' })
export class ConfirmService {
  readonly pending = signal<PendingConfirm | null>(null);

  ask(options: ConfirmOptions): Promise<boolean> {
    return new Promise<boolean>((resolve) => {
      this.pending.set({ ...options, resolve });
    });
  }

  /** Raccourci pour les suppressions. */
  askDelete(what: string): Promise<boolean> {
    return this.ask({
      title: 'Confirmer la suppression',
      message: `Voulez-vous vraiment supprimer ${what} ? Cette action est définitive.`,
      confirmLabel: 'Supprimer',
      danger: true,
    });
  }

  answer(value: boolean): void {
    const current = this.pending();
    this.pending.set(null);
    current?.resolve(value);
  }
}
