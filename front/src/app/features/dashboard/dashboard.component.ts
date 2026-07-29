import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import {
  ArticleService,
  CartService,
  CustomerService,
  OrderLineService,
  SupplierService,
} from '../../core/api';
import { Article, Cart, Customer, Supplier } from '../../core/models/api.models';
import { DocumentStatus, normalizeStatus, statusMeta } from '../../core/models/document-status';
import { CapitalizePipe, EurPipe, FrDatePipe, RefPipe } from '../../shared/pipes/format.pipes';
import { EmptyStateComponent } from '../../shared/ui/empty-state.component';
import { IconComponent } from '../../shared/ui/icon.component';
import { PageHeaderComponent } from '../../shared/ui/page-header.component';
import { StatusBadgeComponent } from '../../shared/ui/status-badge.component';
import { computeTotals } from '../carts/document-totals';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    RouterLink,
    PageHeaderComponent,
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
      title="Tableau de bord"
      subtitle="Vue d'ensemble de votre activité de vente et d'installation."
    >
      <button type="button" class="btn-secondary" (click)="load()">
        <app-icon name="refresh" [size]="16" /> Actualiser
      </button>
      <a routerLink="/documents/nouveau" class="btn-primary">
        <app-icon name="plus" [size]="16" /> Nouveau document
      </a>
    </app-page-header>

    @if (offline()) {
      <div
        class="mb-5 flex items-start gap-3 rounded-lg border border-red-200 bg-red-50 px-4 py-3.5 text-[13px] text-red-800 dark:border-red-500/30 dark:bg-red-500/10 dark:text-red-300"
      >
        <app-icon name="alert" [size]="17" class="mt-0.5" />
        <div>
          <p class="font-semibold">Backend injoignable</p>
          <p class="mt-0.5">
            Démarrez l'API Spring Boot puis actualisez. En développement, le front proxifie
            <code class="font-mono">/api</code> vers <code class="font-mono">http://localhost:8080</code>.
          </p>
        </div>
      </div>
    }

    <!-- Indicateurs -->
    <div class="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
      <div class="card card-pad">
        <div class="flex items-start justify-between">
          <div>
            <p class="text-[13px] muted">Chiffre d'affaires facturé</p>
            <p class="kpi-value mt-1.5">{{ invoicedRevenue() | eur }}</p>
            <p class="mt-1 text-[12.5px] muted">{{ countBy('FACTURE') + countBy('PAYEE') }} facture(s)</p>
          </div>
          <span
            class="flex h-10 w-10 items-center justify-center rounded-lg bg-emerald-50 text-emerald-600 dark:bg-emerald-500/10 dark:text-emerald-400"
          >
            <app-icon name="euro" [size]="19" />
          </span>
        </div>
      </div>

      <div class="card card-pad">
        <div class="flex items-start justify-between">
          <div>
            <p class="text-[13px] muted">Devis en attente</p>
            <p class="kpi-value mt-1.5">{{ pendingQuotes() | eur }}</p>
            <p class="mt-1 text-[12.5px] muted">{{ countBy('DEVIS') }} devis à relancer</p>
          </div>
          <span
            class="flex h-10 w-10 items-center justify-center rounded-lg bg-brand-50 text-brand-600 dark:bg-brand-500/10 dark:text-brand-400"
          >
            <app-icon name="file" [size]="19" />
          </span>
        </div>
      </div>

      <div class="card card-pad">
        <div class="flex items-start justify-between">
          <div>
            <p class="text-[13px] muted">Clients</p>
            <p class="kpi-value mt-1.5">{{ customers().length }}</p>
            <p class="mt-1 text-[12.5px] muted">{{ suppliers().length }} fournisseur(s)</p>
          </div>
          <span
            class="flex h-10 w-10 items-center justify-center rounded-lg bg-ink-100 text-ink-600 dark:bg-ink-800 dark:text-ink-300"
          >
            <app-icon name="users" [size]="19" />
          </span>
        </div>
      </div>

      <div class="card card-pad">
        <div class="flex items-start justify-between">
          <div>
            <p class="text-[13px] muted">Valeur du stock TTC</p>
            <p class="kpi-value mt-1.5">{{ stockValue() | eur }}</p>
            <p class="mt-1 text-[12.5px]" [class]="outOfStock() > 0 ? 'text-red-600 dark:text-red-400' : 'muted'">
              {{ outOfStock() }} article(s) en rupture
            </p>
          </div>
          <span
            class="flex h-10 w-10 items-center justify-center rounded-lg bg-heat-50 text-heat-600 dark:bg-heat-500/10 dark:text-heat-400"
          >
            <app-icon name="package" [size]="19" />
          </span>
        </div>
      </div>
    </div>

    <div class="mt-5 grid gap-5 lg:grid-cols-3">
      <!-- Documents récents -->
      <div class="card lg:col-span-2">
        <div class="flex items-center justify-between border-b border-ink-200 p-4 dark:border-ink-800">
          <h2 class="panel-title">Documents récents</h2>
          <a routerLink="/documents" class="btn-ghost btn-sm">
            Tout voir <app-icon name="arrowRight" [size]="14" />
          </a>
        </div>

        @if (loading()) {
          <div class="space-y-3 p-5">
            @for (i of [1, 2, 3]; track i) {
              <div class="skeleton h-12 w-full"></div>
            }
          </div>
        } @else if (recentCarts().length === 0) {
          <app-empty-state icon="invoice" title="Aucun document" message="Créez votre premier devis.">
            <a routerLink="/documents/nouveau" class="btn-primary">
              <app-icon name="plus" [size]="16" /> Nouveau document
            </a>
          </app-empty-state>
        } @else {
          <div class="table-wrap">
            <table class="table">
              <thead>
                <tr>
                  <th class="w-40">Référence</th>
                  <th>Client</th>
                  <th class="w-28">Statut</th>
                  <th class="w-32 text-right">Total TTC</th>
                  <th class="w-32">Date</th>
                </tr>
              </thead>
              <tbody>
                @for (c of recentCarts(); track c.crtId) {
                  <tr>
                    <td>
                      <a
                        [routerLink]="['/documents', c.crtId]"
                        class="font-mono text-[13px] font-medium text-brand-700 hover:underline dark:text-brand-400"
                      >
                        {{ c.crtRef | ref }}
                      </a>
                    </td>
                    <td class="truncate">
                      {{ c.customer?.ctmFirstName | capitalize }} {{ c.customer?.ctmLastName | capitalize }}
                    </td>
                    <td><app-status-badge [status]="c.crtStatus" /></td>
                    <td class="num text-right font-semibold">{{ totalOf(c.crtId) | eur }}</td>
                    <td class="text-[13px] muted">{{ c.crtCreateDate | frDate }}</td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        }
      </div>

      <!-- Répartition & alertes -->
      <div class="space-y-5">
        <div class="card card-pad">
          <h2 class="mb-4 panel-title">Répartition par statut</h2>
          @if (carts().length === 0) {
            <p class="text-[13px] muted">Aucun document à afficher.</p>
          } @else {
            <div class="space-y-3">
              @for (s of distribution(); track s.status) {
                <div>
                  <div class="mb-1 flex items-center justify-between text-[13px]">
                    <span class="font-medium">{{ label(s.status) }}</span>
                    <span class="num muted">{{ s.count }}</span>
                  </div>
                  <div class="h-1.5 overflow-hidden rounded-full bg-ink-100 dark:bg-ink-800">
                    <div class="h-full rounded-full" [class]="barColor(s.status)" [style.width.%]="s.pct"></div>
                  </div>
                </div>
              }
            </div>
          }
        </div>

        <div class="card card-pad">
          <h2 class="mb-1 panel-title">Alertes de stock</h2>
          <p class="mb-4 text-[13px] muted">Articles à réapprovisionner.</p>

          @if (lowStock().length === 0) {
            <p class="text-[13px] muted">Aucune alerte — tous les stocks sont confortables.</p>
          } @else {
            <ul class="space-y-2.5">
              @for (a of lowStock(); track a.artId) {
                <li class="flex items-center gap-3">
                  <span
                    class="flex h-8 w-8 shrink-0 items-center justify-center rounded-lg"
                    [class]="a.artStock <= 0 ? 'bg-red-50 text-red-600 dark:bg-red-500/10 dark:text-red-400' : 'bg-amber-50 text-amber-600 dark:bg-amber-500/10 dark:text-amber-400'"
                  >
                    <app-icon name="alert" [size]="15" />
                  </span>
                  <div class="min-w-0 flex-1">
                    <p class="truncate text-[13px] font-medium">{{ a.artName | capitalize }}</p>
                    <p class="truncate font-mono text-[11.5px] muted">{{ a.artReference | ref }}</p>
                  </div>
                  <span [class]="a.artStock <= 0 ? 'badge-danger num' : 'badge-warn num'">
                    {{ a.artStock }}
                  </span>
                </li>
              }
            </ul>
            <a routerLink="/articles" class="btn-ghost btn-sm mt-4 w-full">
              Gérer le catalogue <app-icon name="arrowRight" [size]="14" />
            </a>
          }
        </div>
      </div>
    </div>
  `,
})
export class DashboardComponent implements OnInit {
  private readonly cartApi = inject(CartService);
  private readonly lineApi = inject(OrderLineService);
  private readonly articleApi = inject(ArticleService);
  private readonly customerApi = inject(CustomerService);
  private readonly supplierApi = inject(SupplierService);

  protected readonly carts = signal<Cart[]>([]);
  protected readonly articles = signal<Article[]>([]);
  protected readonly customers = signal<Customer[]>([]);
  protected readonly suppliers = signal<Supplier[]>([]);
  protected readonly totals = signal<Record<number, number>>({});
  protected readonly loading = signal(true);
  protected readonly offline = signal(false);

  protected readonly recentCarts = computed(() =>
    [...this.carts()].sort((a, b) => (b.crtId ?? 0) - (a.crtId ?? 0)).slice(0, 8),
  );

  protected readonly invoicedRevenue = computed(() =>
    this.carts()
      .filter((c) => ['FACTURE', 'PAYEE'].includes(normalizeStatus(c.crtStatus)))
      .reduce((sum, c) => sum + this.totalOf(c.crtId), 0),
  );

  protected readonly pendingQuotes = computed(() =>
    this.carts()
      .filter((c) => normalizeStatus(c.crtStatus) === 'DEVIS')
      .reduce((sum, c) => sum + this.totalOf(c.crtId), 0),
  );

  protected readonly stockValue = computed(() =>
    this.articles().reduce(
      (sum, a) => sum + Number(a.artPriceTTC ?? 0) * Number(a.artStock ?? 0),
      0,
    ),
  );

  protected readonly outOfStock = computed(
    () => this.articles().filter((a) => Number(a.artStock ?? 0) <= 0).length,
  );

  protected readonly lowStock = computed(() =>
    this.articles()
      .filter((a) => Number(a.artStock ?? 0) < 5)
      .sort((a, b) => Number(a.artStock ?? 0) - Number(b.artStock ?? 0))
      .slice(0, 6),
  );

  protected readonly distribution = computed(() => {
    const total = this.carts().length || 1;
    const statuses: DocumentStatus[] = ['PANIER', 'DEVIS', 'FACTURE', 'PAYEE', 'ANNULE'];
    return statuses
      .map((status) => {
        const count = this.countBy(status);
        return { status, count, pct: Math.round((count / total) * 100) };
      })
      .filter((s) => s.count > 0);
  });

  ngOnInit(): void {
    this.load();
  }

  countBy(status: DocumentStatus): number {
    return this.carts().filter((c) => normalizeStatus(c.crtStatus) === status).length;
  }

  totalOf(crtId: number): number {
    return this.totals()[crtId] ?? 0;
  }

  label(status: DocumentStatus): string {
    return statusMeta(status).label;
  }

  barColor(status: DocumentStatus): string {
    return {
      PANIER: 'bg-ink-400',
      DEVIS: 'bg-brand-500',
      FACTURE: 'bg-amber-500',
      PAYEE: 'bg-emerald-500',
      ANNULE: 'bg-red-500',
    }[status];
  }

  async load(): Promise<void> {
    this.loading.set(true);
    this.offline.set(false);
    try {
      const [carts, articles, customers, suppliers] = await Promise.all([
        firstValueFrom(this.cartApi.list()),
        firstValueFrom(this.articleApi.list()).catch(() => [] as Article[]),
        firstValueFrom(this.customerApi.list()).catch(() => [] as Customer[]),
        firstValueFrom(this.supplierApi.list()).catch(() => [] as Supplier[]),
      ]);
      this.carts.set(carts);
      this.articles.set(articles);
      this.customers.set(customers);
      this.suppliers.set(suppliers);

      const catalog = new Map(articles.map((a) => [a.artId, a]));
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
      this.offline.set(true);
      this.carts.set([]);
    } finally {
      this.loading.set(false);
    }
  }
}
