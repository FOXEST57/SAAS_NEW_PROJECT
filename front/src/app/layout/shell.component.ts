import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { LoadingService } from '../core/interceptors/loading.interceptor';
import { CommerceStore } from '../core/services/commerce-store.service';
import { ROLE_LABELS, AuthService } from '../core/auth';
import { ThemeService } from '../core/services/theme.service';
import { ConfirmDialogComponent } from '../shared/ui/confirm-dialog.component';
import { IconComponent } from '../shared/ui/icon.component';
import { ToastHostComponent } from '../shared/ui/toast-host.component';

interface NavItem {
  readonly path: string;
  readonly label: string;
  readonly icon: string;
  /** Pastille dynamique : nombre de documents à traiter ou d'achats à passer. */
  readonly badge?: 'actions' | 'supply';
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
      { path: '/pipeline', label: 'Pipeline', icon: 'columns' },
      { path: '/a-traiter', label: 'À traiter', icon: 'bell', badge: 'actions' },
      { path: '/approvisionnement', label: 'Approvisionnement', icon: 'truck', badge: 'supply' },
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
                    @if (item.badge === 'actions' && store.actionCount() > 0) {
                      <span
                        class="num ml-auto rounded-full bg-amber-100 px-1.5 text-[11px] font-semibold text-amber-800 dark:bg-amber-500/20 dark:text-amber-300"
                        >{{ store.actionCount() }}</span
                      >
                    }
                    @if (item.badge === 'supply' && store.firmSupplyNeeds().length > 0) {
                      <span
                        class="num ml-auto rounded-full bg-heat-100 px-1.5 text-[11px] font-semibold text-heat-800 dark:bg-heat-500/20 dark:text-heat-300"
                        >{{ store.firmSupplyNeeds().length }}</span
                      >
                    }
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
            <!-- Utilisateur connecté -->
            <div
              class="relative ml-1.5 border-l border-ink-200 pl-3 dark:border-ink-800"
              (keydown.escape)="menuOpen.set(false)"
            >
              <button
                type="button"
                class="flex items-center gap-2.5 rounded-lg py-1 pl-1 pr-1.5 transition hover:bg-ink-100 dark:hover:bg-ink-800"
                (click)="menuOpen.set(!menuOpen())"
                [attr.aria-expanded]="menuOpen()"
                aria-haspopup="menu"
              >
                <span
                  class="flex h-8 w-8 items-center justify-center rounded-full bg-brand-600 text-[12px] font-semibold text-white"
                  >{{ initials() }}</span
                >
                <div class="hidden leading-tight text-left sm:block">
                  <p class="max-w-[13rem] truncate text-[13px] font-medium">{{ displayName() }}</p>
                  <p class="text-[11px] muted">{{ roleLabel() }}</p>
                </div>
                <app-icon name="chevronDown" [size]="14" class="hidden text-ink-400 sm:block" />
              </button>

              @if (menuOpen()) {
                <!-- Capteur de clic extérieur -->
                <div class="fixed inset-0 z-40" (click)="menuOpen.set(false)"></div>
                <div
                  class="absolute right-0 z-50 mt-2 w-60 overflow-hidden rounded-xl border border-ink-200 bg-white shadow-lg dark:border-ink-800 dark:bg-ink-900"
                  role="menu"
                >
                  <div class="border-b border-ink-100 px-4 py-3 dark:border-ink-800">
                    <p class="truncate text-[13px] font-medium">{{ displayName() }}</p>
                    <p class="truncate text-[11.5px] muted">{{ auth.user()?.email }}</p>
                    <span
                      class="mt-2 inline-flex items-center rounded-full bg-ink-100 px-2 py-0.5 text-[11px] font-medium text-ink-600 dark:bg-ink-800 dark:text-ink-300"
                      >{{ roleLabel() }}</span
                    >
                  </div>
                  <button
                    type="button"
                    role="menuitem"
                    class="flex w-full items-center gap-2.5 px-4 py-2.5 text-left text-[13px] transition hover:bg-ink-50 dark:hover:bg-ink-800"
                    (click)="signOut()"
                  >
                    <app-icon name="external" [size]="15" class="text-ink-400" />
                    Se déconnecter
                  </button>
                </div>
              }
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
  protected readonly auth = inject(AuthService);

  /** Menu du compte, dans l'en-tête. */
  protected readonly menuOpen = signal(false);

  /**
   * Le backend n'expose ni prénom ni nom dans le jeton : seule l'adresse
   * e-mail y figure. On en tire un nom lisible plutôt que d'afficher
   * l'adresse brute, tout en la conservant dans le menu déroulant.
   */
  protected readonly displayName = computed(() => {
    const email = this.auth.user()?.email ?? '';
    const local = email.split('@')[0];
    if (!local) return 'Utilisateur';
    return local
      .split(/[.\-_]+/)
      .filter(Boolean)
      .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
      .join(' ');
  });

  protected readonly initials = computed(() => {
    const parts = this.displayName().split(' ').filter(Boolean);
    if (parts.length === 0) return '?';
    if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
  });

  protected readonly roleLabel = computed(() => {
    const user = this.auth.user();
    if (!user) return '';
    // Un libellé absent de la nomenclature est affiché tel quel : mieux vaut
    // montrer la valeur réelle de la base qu'un intitulé inventé.
    return ROLE_LABELS[user.role] ?? user.rawRole;
  });

  protected signOut(): void {
    this.menuOpen.set(false);
    this.auth.logout();
  }
  protected readonly loading = inject(LoadingService);
  protected readonly store = inject(CommerceStore);
  protected readonly nav = NAV;
  protected readonly mobileOpen = signal(false);
}
