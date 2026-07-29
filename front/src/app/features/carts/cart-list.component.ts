import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { ArticleService, CartService, OrderLineService } from '../../core/api';
import { Article, Cart } from '../../core/models/api.models';
import {
  DOCUMENT_STATUS_LIST,
  DocumentStatus,
  normalizeStatus,
} from '../../core/models/document-status';
import { ConfirmService } from '../../core/services/confirm.service';
import { ToastService } from '../../core/services/toast.service';
import { CapitalizePipe, EurPipe, FrDatePipe, RefPipe } from '../../shared/pipes/format.pipes';
import { EmptyStateComponent } from '../../shared/ui/empty-state.component';
import { IconComponent } from '../../shared/ui/icon.component';
import { PageHeaderComponent } from '../../shared/ui/page-header.component';
import { SearchInputComponent } from '../../shared/ui/search-input.component';
import { StatusBadgeComponent } from '../../shared/ui/status-badge.component';
import { computeTotals } from './document-totals';

@Component({
  selector: 'app-cart-list',
  standalone: true,
  imports: [
    RouterLink,
    PageHeaderComponent,
    SearchInputComponent,
    EmptyStateComponent,
    IconComponent,
    StatusBadgeComponent,
    CapitalizePipe,
    EurPipe,
    FrDatePipe,
    RefPipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <app-page-header
      title="Devis & factures"
      subtitle="Tous vos documents commerciaux, du panier de travail à la facture réglée."
    >
      <button type="button" class="btn-secondary" (click)="load()">
        <app-icon name="refresh" [size]="16" /> Actualiser
      </button>
      <a routerLink="/documents/nouveau" class="btn-primary">
        <app-icon name="plus" [size]="16" /> Nouveau document
      </a>
    </app-page-header>

    <!-- Filtres par statut -->
    <div class="mb-5 flex flex-wrap gap-2">
      <button
        type="button"
        class="rounded-lg border px-3 py-1.5 text-sm font-medium transition-colors"
        [class]="statusFilter() === null ? chipActive : chipIdle"
        (click)="statusFilter.set(null)"
      >
        Tous <span class="ml-1.5 num opacity-70">{{ items().length }}</span>
      </button>
      @for (s of statuses; track s.value) {
        <button
          type="button"
          class="rounded-lg border px-3 py-1.5 text-sm font-medium transition-colors"
          [class]="statusFilter() === s.value ? chipActive : chipIdle"
          (click)="statusFilter.set(s.value)"
        >
          {{ s.label }} <span class="ml-1.5 num opacity-70">{{ countBy(s.value) }}</span>
        </button>
      }
    </div>

    <div class="card">
      <div class="flex flex-wrap items-center justify-between gap-3 border-b border-ink-200 p-4 dark:border-ink-800">
        <app-search-input [value]="search()" (valueChange)="search.set($event)" placeholder="Référence ou client…" />
        <p class="text-[13px] muted">{{ filtered().length }} / {{ items().length }} documents</p>
      </div>

      @if (loading()) {
        <div class="space-y-3 p-5">
          @for (i of [1, 2, 3, 4]; track i) {
            <div class="skeleton h-14 w-full"></div>
          }
        </div>
      } @else if (filtered().length === 0) {
        <app-empty-state
          icon="invoice"
          title="Aucun document"
          message="Créez un panier, transformez-le en devis puis en facture."
        >
          <a routerLink="/documents/nouveau" class="btn-primary">
            <app-icon name="plus" [size]="16" /> Nouveau document
          </a>
        </app-empty-state>
      } @else {
        <div class="table-wrap">
          <table class="table">
            <thead>
              <tr>
                <th class="w-44">Référence</th>
                <th>Client</th>
                <th class="w-32">Statut</th>
                <th class="w-24 text-center">Lignes</th>
                <th class="w-32 text-right">Total TTC</th>
                <th class="w-36">Dernière modif.</th>
                <th class="w-32 text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              @for (c of filtered(); track c.crtId) {
                <tr>
                  <td>
                    <a
                      [routerLink]="['/documents', c.crtId]"
                      class="font-mono text-[13px] font-medium text-brand-700 hover:underline dark:text-brand-400"
                    >
                      {{ c.crtRef | ref }}
                    </a>
                  </td>
                  <td>
                    @if (c.customer) {
                      <p class="font-medium">
                        {{ c.customer.ctmFirstName | capitalize }} {{ c.customer.ctmLastName | capitalize }}
                      </p>
                      <p class="truncate text-[12.5px] muted">{{ c.customer.ctmEmail }}</p>
                    } @else {
                      <span class="text-[13px] muted">Client supprimé</span>
                    }
                  </td>
                  <td><app-status-badge [status]="c.crtStatus" /></td>
                  <td class="num text-center">{{ c.orderLines?.length ?? 0 }}</td>
                  <td class="num text-right font-semibold">{{ totalOf(c.crtId) | eur }}</td>
                  <td class="text-[13px] muted">{{ c.crtLastModifieDate || c.crtCreateDate | frDate }}</td>
                  <td>
                    <div class="flex justify-end gap-1">
                      <a [routerLink]="['/documents', c.crtId]" class="btn-icon" title="Ouvrir">
                        <app-icon name="edit" [size]="16" />
                      </a>
                      <a
                        [routerLink]="['/documents', c.crtId, 'impression']"
                        class="btn-icon"
                        title="Aperçu / impression"
                      >
                        <app-icon name="print" [size]="16" />
                      </a>
                      <button
                        type="button"
                        class="btn-icon hover:bg-red-50 hover:text-red-600 dark:hover:bg-red-500/10"
                        (click)="remove(c)"
                        title="Supprimer"
                      >
                        <app-icon name="trash" [size]="16" />
                      </button>
                    </div>
                  </td>
                </tr>
              }
            </tbody>
          </table>
        </div>
      }
    </div>
  `,
})
export class CartListComponent implements OnInit {
  private readonly api = inject(CartService);
  private readonly lineApi = inject(OrderLineService);
  private readonly articleApi = inject(ArticleService);
  private readonly toast = inject(ToastService);
  private readonly confirm = inject(ConfirmService);

  protected readonly chipActive =
    'border-brand-600 bg-brand-600 text-white dark:border-brand-500 dark:bg-brand-600';
  protected readonly chipIdle =
    'border-ink-200 bg-white text-ink-700 hover:bg-ink-50 dark:border-ink-700 dark:bg-ink-900 dark:text-ink-300 dark:hover:bg-ink-800';

  protected readonly statuses = DOCUMENT_STATUS_LIST;
  protected readonly items = signal<Cart[]>([]);
  protected readonly loading = signal(true);
  protected readonly search = signal('');
  protected readonly statusFilter = signal<DocumentStatus | null>(null);
  /** Total TTC calculé par document, indexé par `crtId`. */
  protected readonly totals = signal<Record<number, number>>({});

  protected readonly filtered = computed(() => {
    const q = this.search().trim().toLowerCase();
    const status = this.statusFilter();

    return this.items()
      .filter((c) => {
        if (status && normalizeStatus(c.crtStatus) !== status) return false;
        if (!q) return true;
        const haystack = [
          c.crtRef,
          c.customer?.ctmFirstName,
          c.customer?.ctmLastName,
          c.customer?.ctmEmail,
        ]
          .filter(Boolean)
          .join(' ')
          .toLowerCase();
        return haystack.includes(q);
      })
      .sort((a, b) => (b.crtId ?? 0) - (a.crtId ?? 0));
  });

  ngOnInit(): void {
    this.load();
  }

  countBy(status: DocumentStatus): number {
    return this.items().filter((c) => normalizeStatus(c.crtStatus) === status).length;
  }

  totalOf(crtId: number): number {
    return this.totals()[crtId] ?? 0;
  }

  async load(): Promise<void> {
    this.loading.set(true);
    try {
      const [carts, articles] = await Promise.all([
        firstValueFrom(this.api.list()),
        firstValueFrom(this.articleApi.list()).catch(() => [] as Article[]),
      ]);
      this.items.set(carts);

      const catalog = new Map(articles.map((a) => [a.artId, a]));

      // Les totaux exigent le détail des lignes : une requête par document.
      const entries = await Promise.all(
        carts.map(async (cart) => {
          try {
            const lines = await firstValueFrom(this.lineApi.listByCart(cart.crtId));
            return [cart.crtId, computeTotals(lines, catalog).totalTtc] as const;
          } catch {
            return [cart.crtId, 0] as const;
          }
        }),
      );
      this.totals.set(Object.fromEntries(entries));
    } catch {
      this.items.set([]);
    } finally {
      this.loading.set(false);
    }
  }

  async remove(cart: Cart): Promise<void> {
    const ok = await this.confirm.askDelete(`le document « ${cart.crtRef?.toUpperCase()} »`);
    if (!ok) return;
    try {
      await firstValueFrom(this.api.delete(cart.crtId));
      this.toast.success('Document supprimé');
      await this.load();
    } catch {
      /* déjà notifié */
    }
  }
}
