import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { IconComponent } from '../../shared/ui/icon.component';

@Component({
  selector: 'app-not-found',
  standalone: true,
  imports: [RouterLink, IconComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="flex min-h-[60vh] flex-col items-center justify-center text-center">
      <span
        class="mb-5 flex h-14 w-14 items-center justify-center rounded-full bg-ink-100 text-ink-400 dark:bg-ink-800 dark:text-ink-500"
      >
        <app-icon name="search" [size]="24" />
      </span>
      <p class="text-3xl font-semibold tracking-tight">404</p>
      <h1 class="mt-1 text-lg font-medium">Page introuvable</h1>
      <p class="mt-2 max-w-sm text-sm muted">
        Le lien que vous avez suivi ne correspond à aucun écran de l'application.
      </p>
      <a routerLink="/tableau-de-bord" class="btn-primary mt-6">
        <app-icon name="dashboard" [size]="16" /> Retour au tableau de bord
      </a>
    </div>
  `,
})
export class NotFoundComponent {}
