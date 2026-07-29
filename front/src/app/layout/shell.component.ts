import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { LoadingService } from '../core/interceptors/loading.interceptor';
import { ThemeService } from '../core/services/theme.service';
import { ConfirmDialogComponent } from '../shared/ui/confirm-dialog.component';
import { IconComponent } from '../shared/ui/icon.component';
import { ToastHostComponent } from '../shared/ui/toast-host.component';

interface NavItem {
  readonly path: string;
  readonly label: string;
  readonly icon: string;
}

interface NavGroup {
  readonly label: string;
  readonly items: readonly NavItem[];
}

/** Structure de navigation, calquée sur le domaine métier. */
const NAV: readonly NavGroup[] = [
  {
    label: 'Pilotage',
    items: [
      { path: '/tableau-de-bord', label: 'Tableau de bord', icon: 'dashboard' },
      { path: '/documents', label: 'Devis & factures', icon: 'invoice' },
    ],
  },
  {
    label: 'Catalogue',
    items: [
      { path: '/articles', label: 'Articles', icon: 'package' },
      { path: '/categories', label: 'Catégories', icon: 'tag' },
      { path: '/tva', label: 'Taux de TVA', icon: 'percent' },
      { path: '/references', label: 'Références', icon: 'link' },
    ],
  },
  {
    label: 'Tiers',
    items: [
      { path: '/clients', label: 'Clients', icon: 'users' },
      { path: '/types-de-compte', label: 'Types de compte', icon: 'settings' },
      { path: '/fournisseurs', label: 'Fournisseurs', icon: 'truck' },
      { path: '/fabricants', label: 'Fabricants', icon: 'factory' },
    ],
  },
  {
    label: 'Référentiel géographique',
    items: [
      { path: '/adresses', label: 'Adresses', icon: 'mapPin' },
      { path: '/villes', label: 'Villes', icon: 'building' },
      { path: '/codes-postaux', label: 'Codes postaux', icon: 'boxes' },
      { path: '/pays', label: 'Pays', icon: 'globe' },
      { path: '/associations-cp-ville', label: 'Associations CP / ville', icon: 'layers' },
    ],
  },
];

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    IconComponent,
    ToastHostComponent,
    ConfirmDialogComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="flex min-h-full">
      <!-- Barre de progression globale -->
      @if (loading.isLoading()) {
        <div class="no-print fixed inset-x-0 top-0 z-[80] h-0.5 overflow-hidden bg-brand-100 dark:bg-ink-800">
          <div class="h-full w-1/3 animate-[slide-in-right_1s_ease-in-out_infinite] bg-brand-600"></div>
        </div>
      }

      <!-- Voile mobile -->
      @if (mobileOpen()) {
        <div
          class="no-print fixed inset-0 z-40 bg-ink-950/40 lg:hidden"
          (click)="mobileOpen.set(false)"
          aria-hidden="true"
        ></div>
      }

      <!-- Barre latérale -->
      <aside
        class="no-print fixed inset-y-0 left-0 z-50 flex w-64 shrink-0 flex-col border-r border-ink-200 bg-white transition-transform duration-200 dark:border-ink-800 dark:bg-ink-900 lg:sticky lg:top-0 lg:h-screen lg:translate-x-0"
        [class.-translate-x-full]="!mobileOpen()"
      >
        <div class="flex h-16 shrink-0 items-center gap-2.5 border-b border-ink-200 px-5 dark:border-ink-800">
          <span
            class="flex h-8 w-8 items-center justify-center rounded-lg bg-gradient-to-br from-brand-500 to-brand-700 text-white shadow-sm"
          >
            <app-icon name="snowflake" [size]="17" />
          </span>
          <div class="min-w-0 leading-tight">
            <p class="truncate text-[15px] font-semibold tracking-tight">Klimafact</p>
            <p class="truncate text-[11px] muted">Climatisation & Chauffage</p>
          </div>
          <button
            type="button"
            class="btn-icon ml-auto lg:hidden"
            (click)="mobileOpen.set(false)"
            aria-label="Fermer le menu"
          >
            <app-icon name="close" [size]="18" />
          </button>
        </div>

        <nav class="flex-1 overflow-y-auto px-3 pb-6">
          @for (group of nav; track group.label) {
            <p class="nav-section">{{ group.label }}</p>
            <ul class="space-y-0.5">
              @for (item of group.items; track item.path) {
                <li>
                  <a
                    [routerLink]="item.path"
                    routerLinkActive="nav-link-active"
                    class="nav-link"
                    (click)="mobileOpen.set(false)"
                  >
                    <app-icon [name]="item.icon" [size]="17" />
                    <span class="truncate">{{ item.label }}</span>
                  </a>
                </li>
              }
            </ul>
          }
        </nav>

        <div class="border-t border-ink-200 p-3 dark:border-ink-800">
          <a routerLink="/documents/nouveau" class="btn-primary w-full" (click)="mobileOpen.set(false)">
            <app-icon name="plus" [size]="16" />
            Nouveau document
          </a>
        </div>
      </aside>

      <!-- Contenu -->
      <div class="flex min-w-0 flex-1 flex-col">
        <header
          class="no-print sticky top-0 z-30 flex h-16 shrink-0 items-center gap-3 border-b border-ink-200 bg-white/85 px-4 backdrop-blur dark:border-ink-800 dark:bg-ink-900/85 sm:px-6"
        >
          <button
            type="button"
            class="btn-icon lg:hidden"
            (click)="mobileOpen.set(true)"
            aria-label="Ouvrir le menu"
          >
            <app-icon name="menu" [size]="20" />
          </button>

          <div class="ml-auto flex items-center gap-1.5">
            <a
              href="http://localhost:8080/swagger-ui/index.html"
              target="_blank"
              rel="noopener"
              class="btn-ghost btn-sm hidden sm:inline-flex"
              title="Documentation de l'API"
            >
              <app-icon name="external" [size]="15" />
              API
            </a>
            <button
              type="button"
              class="btn-icon"
              (click)="theme.toggle()"
              [attr.aria-label]="theme.isDark() ? 'Passer en thème clair' : 'Passer en thème sombre'"
            >
              <app-icon [name]="theme.isDark() ? 'sun' : 'moon'" [size]="18" />
            </button>
            <div class="ml-1.5 flex items-center gap-2.5 border-l border-ink-200 pl-3 dark:border-ink-800">
              <span
                class="flex h-8 w-8 items-center justify-center rounded-full bg-ink-100 text-[12px] font-semibold text-ink-600 dark:bg-ink-800 dark:text-ink-300"
                >KF</span
              >
              <div class="hidden leading-tight sm:block">
                <p class="text-[13px] font-medium">Espace commercial</p>
                <p class="text-[11px] muted">Klimafact SARL</p>
              </div>
            </div>
          </div>
        </header>

        <main class="min-w-0 flex-1 px-4 py-6 sm:px-6 lg:px-8">
          <router-outlet />
        </main>
      </div>
    </div>

    <app-toast-host />
    <app-confirm-dialog />
  `,
})
export class ShellComponent {
  protected readonly theme = inject(ThemeService);
  protected readonly loading = inject(LoadingService);
  protected readonly nav = NAV;
  protected readonly mobileOpen = signal(false);
}
