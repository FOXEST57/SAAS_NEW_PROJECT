import { ChangeDetectionStrategy, Component, OnInit, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { statusMeta } from '../../core/models/document-status';
import { CommerceStore } from '../../core/services/commerce-store.service';
import { FunnelComponent } from '../../shared/charts/funnel.component';
import { MarginMeterComponent } from '../../shared/charts/margin-meter.component';
import { StackedColumnsComponent } from '../../shared/charts/stacked-columns.component';
import { CapitalizePipe, EurPipe, FrDatePipe, RefPipe } from '../../shared/pipes/format.pipes';
import { EmptyStateComponent } from '../../shared/ui/empty-state.component';
import { IconComponent } from '../../shared/ui/icon.component';
import { PageHeaderComponent } from '../../shared/ui/page-header.component';
import { StatusBadgeComponent } from '../../shared/ui/status-badge.component';
import { StatTileComponent } from './stat-tile.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    RouterLink,
    PageHeaderComponent,
    EmptyStateComponent,
    IconComponent,
    StatusBadgeComponent,
    StatTileComponent,
    StackedColumnsComponent,
    FunnelComponent,
    MarginMeterComponent,
    CapitalizePipe,
    EurPipe,
    FrDatePipe,
    RefPipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <app-page-header
      title="Tableau de bord"
      subtitle="Activité commerciale, rentabilité et actions à mener."
    >
      <button type="button" class="btn-secondary" (click)="store.reload()" [disabled]="store.loading()">
        <app-icon name="refresh" [size]="16" /> Actualiser
      </button>
      <a routerLink="/documents/nouveau" class="btn-primary">
        <app-icon name="plus" [size]="16" /> Nouveau document
      </a>
    </app-page-header>

    @if (store.offline()) {
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

    <!-- Bandeau d'actions : ce qui appelle une décision aujourd'hui -->
    @if (store.actionCount() > 0) {
      <a
        routerLink="/a-traiter"
        class="mb-5 flex items-center gap-4 rounded-xl border border-amber-200 bg-amber-50 px-4 py-3.5 transition-colors hover:bg-amber-100 dark:border-amber-500/30 dark:bg-amber-500/10 dark:hover:bg-amber-500/15"
      >
        <span
          class="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-amber-100 text-amber-700 dark:bg-amber-500/20 dark:text-amber-300"
        >
          <app-icon name="bell" [size]="19" />
        </span>
        <div class="min-w-0 flex-1">
          <p class="font-medium text-amber-900 dark:text-amber-200">
            {{ store.actionCount() }} document(s) demandent une action
          </p>
          <p class="mt-0.5 text-[13px] text-amber-800 dark:text-amber-300/90">{{ actionSummary() }}</p>
        </div>
        <span class="shrink-0 text-amber-700 dark:text-amber-300">
          <app-icon name="arrowRight" [size]="18" />
        </span>
      </a>
    }

    <!-- Indicateurs -->
    <div class="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
      <app-stat-tile
        label="CA facturé (12 mois)"
        [value]="store.revenue() | eur"
        icon="euro"
        tone="bg-emerald-50 text-emerald-600 dark:bg-emerald-500/10 dark:text-emerald-400"
        [trend]="store.monthlyRevenue()"
        sparkTone="good"
        [hint]="revenueHint()"
      />
      <app-stat-tile
        label="Marge brute dégagée"
        [value]="store.revenueMargin() | eur"
        icon="target"
        tone="bg-brand-50 text-brand-600 dark:bg-brand-500/10 dark:text-brand-400"
        [hint]="marginHint()"
      />
      <app-stat-tile
        label="Pipeline en cours"
        [value]="store.pipelineValue() | eur"
        icon="trendUp"
        tone="bg-violet-50 text-violet-600 dark:bg-violet-500/10 dark:text-violet-400"
        [hint]="pipelineHint()"
      />
      <app-stat-tile
        label="Encours client"
        [value]="store.outstanding() | eur"
        icon="wallet"
        tone="bg-heat-50 text-heat-600 dark:bg-heat-500/10 dark:text-heat-400"
        [hint]="outstandingHint()"
        [hintClass]="store.overdue().length ? 'text-red-600 dark:text-red-400' : 'muted'"
      />
    </div>

    <div class="mt-5 grid items-start gap-5 lg:grid-cols-3">
      <!-- Saisonnalité -->
      <div class="card lg:col-span-2">
        <div class="flex flex-wrap items-start justify-between gap-3 border-b border-ink-200 p-5 dark:border-ink-800">
          <div>
            <h2 class="panel-title">Saisonnalité climatisation / chauffage</h2>
            <p class="mt-0.5 text-[13px] muted">
              Chiffre d'affaires facturé par mois, ventilé par famille de produits.
            </p>
          </div>
          <span class="badge-neutral">12 mois glissants</span>
        </div>
        <div class="viz-surface rounded-b-xl p-5">
          @if (store.loading()) {
            <div class="skeleton h-64 w-full"></div>
          } @else {
            <app-stacked-columns [data]="store.monthlyByFamily()" />
          }
        </div>
      </div>

      <!-- Entonnoir + marge -->
      <div class="space-y-5">
        <div class="card">
          <div class="border-b border-ink-200 p-5 dark:border-ink-800">
            <h2 class="panel-title">Entonnoir commercial</h2>
            <p class="mt-0.5 text-[13px] muted">Montants cumulés atteignant chaque étape.</p>
          </div>
          <div class="p-5">
            @if (store.loading()) {
              <div class="skeleton h-40 w-full"></div>
            } @else {
              <app-funnel [steps]="store.funnel()" />
            }
          </div>
        </div>

        <div class="card card-pad">
          <h2 class="mb-3 panel-title">Rentabilité</h2>
          <app-margin-meter
            label="Taux de marge sur le facturé"
            [rate]="store.marginRate()"
            [target]="store.marginTarget()"
            [coverage]="store.costCoverage()"
          />
          <p class="mt-3 border-t border-ink-200 pt-3 text-[12.5px] muted dark:border-ink-800">
            Calculé à partir du meilleur prix d'achat référencé chez vos fournisseurs.
            Les articles sans référence fournisseur sont exclus plutôt que comptés à coût nul.
          </p>
        </div>
      </div>
    </div>

    <div class="mt-5 grid items-start gap-5 lg:grid-cols-3">
      <!-- Documents récents -->
      <div class="card lg:col-span-2">
        <div class="flex items-center justify-between border-b border-ink-200 p-5 dark:border-ink-800">
          <h2 class="panel-title">Documents récents</h2>
          <a routerLink="/pipeline" class="btn-ghost btn-sm">
            Voir le pipeline <app-icon name="arrowRight" [size]="14" />
          </a>
        </div>

        @if (store.loading()) {
          <div class="space-y-3 p-5">
            @for (i of [1, 2, 3, 4]; track i) {
              <div class="skeleton h-12 w-full"></div>
            }
          </div>
        } @else if (recent().length === 0) {
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
                  <th class="w-28 whitespace-nowrap text-right">Total TTC</th>
                  <th class="w-24 whitespace-nowrap text-right">Marge</th>
                  <th class="w-28">Date</th>
                </tr>
              </thead>
              <tbody>
                @for (d of recent(); track d.kind + ':' + d.id) {
                  <tr>
                    <td>
                      <a
                        [routerLink]="['/documents', d.kind, d.id]"
                        class="font-mono text-[13px] font-medium text-brand-700 hover:underline dark:text-brand-400"
                      >
                        {{ d.reference | ref }}
                      </a>
                    </td>
                    <td class="truncate">
                      @if (d.customer) {
                        {{ d.customer.ctmFirstName | capitalize }}
                        {{ d.customer.ctmLastName | capitalize }}
                      } @else {
                        <span class="italic muted">Client non identifié</span>
                      }
                    </td>
                    <td><app-status-badge [status]="d.status" /></td>
                    <td class="num whitespace-nowrap text-right font-semibold">
                      {{ d.totals.totalTtc | eur }}
                    </td>
                    <td class="num whitespace-nowrap text-right" [class]="marginClass(d.totals.marginRate)">
                      {{ d.totals.marginRate === null ? '—' : pct(d.totals.marginRate) }}
                    </td>
                    <td class="text-[13px] muted">{{ d.date | frDate }}</td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        }
      </div>

      <!-- Classements -->
      <div class="space-y-5">
        <div class="card">
          <div class="border-b border-ink-200 p-5 dark:border-ink-800">
            <h2 class="panel-title">Meilleurs clients</h2>
            <p class="mt-0.5 text-[13px] muted">Sur le chiffre d'affaires facturé.</p>
          </div>
          <div class="p-5">
            @if (store.topCustomers().length === 0) {
              <p class="text-[13px] muted">Aucune facture émise pour l'instant.</p>
            } @else {
              <ol class="space-y-3">
                @for (c of store.topCustomers(); track c.customer?.ctmId; let i = $index) {
                  <li class="flex items-center gap-3">
                    <span
                      class="flex h-7 w-7 shrink-0 items-center justify-center rounded-full bg-ink-100 text-[12px] font-semibold text-ink-600 dark:bg-ink-800 dark:text-ink-300"
                      >{{ i + 1 }}</span
                    >
                    <div class="min-w-0 flex-1">
                      <p class="truncate text-[13px] font-medium">
                        {{ c.customer?.ctmFirstName | capitalize }}
                        {{ c.customer?.ctmLastName | capitalize }}
                      </p>
                      <p class="text-[12px] muted">{{ c.count }} facture(s)</p>
                    </div>
                    <span class="num shrink-0 text-[13px] font-semibold">{{ c.total | eur }}</span>
                  </li>
                }
              </ol>
            }
          </div>
        </div>

        <div class="card">
          <div class="border-b border-ink-200 p-5 dark:border-ink-800">
            <h2 class="panel-title">Articles les plus vendus</h2>
          </div>
          <div class="p-5">
            @if (store.topArticles().length === 0) {
              <p class="text-[13px] muted">Aucune vente enregistrée.</p>
            } @else {
              <ol class="space-y-3">
                @for (a of store.topArticles(); track a.article?.artId) {
                  <li class="flex items-center gap-3">
                    <span
                      class="flex h-8 w-8 shrink-0 items-center justify-center rounded-lg bg-ink-100 text-ink-500 dark:bg-ink-800 dark:text-ink-400"
                    >
                      <app-icon name="package" [size]="15" />
                    </span>
                    <div class="min-w-0 flex-1">
                      <p class="truncate text-[13px] font-medium">{{ a.article?.artName | capitalize }}</p>
                      <p class="text-[12px] muted">{{ a.qty }} unité(s)</p>
                    </div>
                    <span class="num shrink-0 text-[13px] font-semibold">{{ a.total | eur }}</span>
                  </li>
                }
              </ol>
            }
          </div>
        </div>
      </div>
    </div>
  `,
})
export class DashboardComponent implements OnInit {
  protected readonly store = inject(CommerceStore);

  protected readonly recent = computed(() =>
    [...this.store.documents()]
      // Les id ne sont plus comparables entre eux : chaque entité a sa propre
      // séquence (`crtId`, `quoteId`, `cmfId`, `invoiceId`). On trie par date.
      .sort((a, b) => (b.date?.getTime() ?? 0) - (a.date?.getTime() ?? 0))
      .slice(0, 8),
  );

  ngOnInit(): void {
    this.store.load();
  }

  protected actionSummary(): string {
    const parts: string[] = [];
    const stale = this.store.staleQuotes().length;
    const invoice = this.store.toInvoice().length;
    const late = this.store.overdue().length;

    if (stale) parts.push(`${stale} devis sans réponse`);
    if (invoice) parts.push(`${invoice} commande(s) à facturer`);
    if (late) parts.push(`${late} facture(s) échue(s)`);
    return parts.join(' · ');
  }

  protected revenueHint(): string {
    const count = this.store.documents().filter((d) => statusMeta(d.status).isRevenue).length;
    return `${count} facture(s) émise(s)`;
  }

  protected marginHint(): string {
    const rate = this.store.marginRate();
    if (rate === null) return 'Aucun prix d’achat référencé';
    return `${this.pct(rate)} du chiffre d'affaires`;
  }

  protected pipelineHint(): string {
    const committed = this.store.committedValue();
    if (committed === 0) return 'Aucune commande ferme';
    return `dont ${new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR', maximumFractionDigits: 0 }).format(committed)} de commandes fermes`;
  }

  protected outstandingHint(): string {
    const late = this.store.overdue().length;
    return late ? `${late} facture(s) échue(s) à relancer` : 'Aucun retard de règlement';
  }

  protected pct(v: number): string {
    return `${new Intl.NumberFormat('fr-FR', { maximumFractionDigits: 1 }).format(v * 100)} %`;
  }

  protected marginClass(rate: number | null): string {
    if (rate === null) return 'muted';
    if (rate >= this.store.marginTarget()) return 'text-emerald-700 dark:text-emerald-400';
    if (rate >= this.store.marginTarget() * 0.6) return 'text-amber-700 dark:text-amber-400';
    return 'text-red-700 dark:text-red-400';
  }
}
