import { HttpInterceptorFn } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { finalize } from 'rxjs';

/** Compteur global de requêtes HTTP en vol, pour la barre de progression. */
@Injectable({ providedIn: 'root' })
export class LoadingService {
  private readonly count = signal(0);
  readonly isLoading = signal(false);

  start(): void {
    this.count.update((n) => n + 1);
    this.isLoading.set(true);
  }

  stop(): void {
    this.count.update((n) => Math.max(0, n - 1));
    this.isLoading.set(this.count() > 0);
  }
}

export const loadingInterceptor: HttpInterceptorFn = (req, next) => {
  const loading = inject(LoadingService);
  loading.start();
  return next(req).pipe(finalize(() => loading.stop()));
};
