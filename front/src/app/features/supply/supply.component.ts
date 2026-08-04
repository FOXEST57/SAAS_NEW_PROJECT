import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { environment } from '../../../environments/environment';
import { SupplyGroup } from '../../core/models/supply';
import { CommerceStore } from '../../core/services/commerce-store.service';
import { CapitalizePipe, EurPipe, RefPipe } from '../../shared/pipes/format.pipes';
import { EmptyStateComponent } from '../../shared/ui/empty-state.component';
import { IconComponent } from '../../shared/ui/icon.component';
import { PageHeaderComponent } from '../../shared/ui/page-header.component';

/**
 * Besoins d'approvisionnement, regroupés par fournisseur.
 *
 * Un devis peut porter sur du matériel non stocké — c'est le cas nominal en
 * CVC. Dès qu'il devient une commande, l'engagement est pris et il faut
 * acheter. Cet écran répond à une seule question : **qu'est-ce que je commande,
 * chez qui, et pour combien ?**
 *
 * Le regroupement se fait par fournisseur parce que c'est l'unité de commande
 * réelle : on passe une commande par fournisseur, pas une par article.
 */
@Component({
  selector: 'app-supply',
  standalone: true,
  imports: [
    RouterLink,
    PageHeaderComponent,
    EmptyStateComponent,
    IconComponent,
    CapitalizePipe,
    EurPipe,
    RefPipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <app-page-header
      title="Approvisionnement"
      subtitle="Ce qu'il faut commander pour honorer vos engagements clients, regroupé par fournisseur."
    >
      <label
        class="no-print flex cursor-pointer items-center gap-2 rounded-lg border border-ink-200 bg-white px-3 py-2 text-sm dark:border-ink-700 dark:bg-ink-900"
      >
        <input
          type="checkbox"
          class="h-4 w-4 rounded border-ink-300 text-brand-600 focus:ring-brand-500 dark:border-ink-600 dark:bg-ink-800"
          [checked]="store.forecastSupply()"
          (change)="store.forecastSupply.set(!store.forecastSupply())"
        />
        Inclure les devis
      </label>
      <button
        type="button"
        class="btn-secondary no-print"
        (click)="store.reload()"
        [disabled]="store.loading()"
      >
        <app-icon name="refresh" [size]="16" /> Actualiser
      </button>
      @if (groups().length > 0) {
        <button type="button" class="btn-primary no-print" (click)="print()">
          <app-icon name="print" [size]="16" /> Imprimer les bons
        </button>
      }
    </app-page-header>

    @if (store.loading()) {
      <div class="space-y-4">
        @for (i of [1, 2, 3]; track i) {
          <div class="skeleton h-40 w-full"></div>
        }
      </div>
    } @else if (groups().length === 0) {
      <div class="card">
        <app-empty-state
          icon="checkCircle"
          title="Rien à commander"
          [message]="
            store.forecastSupply()
              ? 'Votre stock couvre l’ensemble de vos commandes et devis en cours.'
              : 'Votre stock couvre toutes vos commandes fermes. Cochez « Inclure les devis » pour anticiper.'
          "
        />
      </div>
    } @else {
      <!-- Synthèse -->
      <div class="no-print mb-5 grid gap-4 sm:grid-cols-3">
        <div class="card card-pad">
          <p class="text-[13px] muted">Montant à engager</p>
          <p class="kpi-value mt-1.5">{{ store.supplyTotal() | eur }}</p>
          <p class="mt-1 text-[12.5px] muted">{{ groups().length }} fournisseur(s)</p>
        </div>
        <div class="card card-pad">
          <p class="text-[13px] muted">Articles à commander</p>
          <p class="kpi-value mt-1.5">{{ totalItems() }}</p>
          <p class="mt-1 text-[12.5px] muted">{{ store.supplyNeeds().length }} référence(s)</p>
        </div>
        <div class="card card-pad">
          <p class="text-[13px] muted">Sans fournisseur</p>
          <p
            class="kpi-value mt-1.5"
            [class]="store.orphanNeeds().length ? 'text-red-600 dark:text-red-400' : ''"
          >
            {{ store.orphanNeeds().length }}
          </p>
          <p class="mt-1 text-[12.5px] muted">
            @if (store.orphanNeeds().length) {
              À référencer pour pouvoir commander
            } @else {
              Toutes les références sont sourcées
            }
          </p>
        </div>
      </div>

      <!-- Un bon de commande par fournisseur -->
      <div class="space-y-5">
        @for (group of groups(); track group.supplierName) {
          <section class="card print-block">
            <header
              class="flex flex-wrap items-start justify-between gap-3 border-b border-ink-200 p-5 dark:border-ink-800"
            >
              <div class="flex items-start gap-3">
                <span
                  class="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg"
                  [class]="
                    group.supplierName === noSupplier
                      ? 'bg-red-50 text-red-600 dark:bg-red-500/10 dark:text-red-400'
                      : 'bg-brand-50 text-brand-600 dark:bg-brand-500/10 dark:text-brand-400'
                  "
                >
                  <app-icon [name]="group.supplierName === noSupplier ? 'alert' : 'truck'" [size]="19" />
                </span>
                <div>
                  <h2 class="panel-title">{{ group.supplierName | capitalize }}</h2>
                  <p class="mt-0.5 text-[13px] muted">
                    {{ group.itemCount }} article(s) · {{ group.lines.length }} référence(s)
                    @if (group.firm) {
                      · <span class="font-medium text-heat-700 dark:text-heat-400">engagement ferme</span>
                    }
                  </p>
                </div>
              </div>

              <div class="text-right">
                <p class="num text-lg font-semibold">{{ group.total | eur }}</p>
                <p class="text-[12px] muted">coût d'achat HT</p>
              </div>
            </header>

            @if (group.supplierName === noSupplier) {
              <p
                class="border-b border-ink-200 bg-red-50 px-5 py-3 text-[13px] text-red-800 dark:border-ink-800 dark:bg-red-500/10 dark:text-red-300"
              >
                Ces articles n'ont aucune référence fournisseur : impossible de savoir où
                commander ni à quel prix.
                <a routerLink="/references" class="font-medium underline no-print">
                  Référencez-les
                </a>
                pour qu'ils rejoignent un bon de commande.
              </p>
            }

            <div class="table-wrap">
              <table class="table table-dense">
                <thead>
                  <tr>
                    <th class="min-w-[180px]">Article</th>
                    <th class="w-[80px] whitespace-nowrap text-center">Dû</th>
                    <th class="w-[80px] whitespace-nowrap text-center">Stock</th>
                    <th class="w-[92px] whitespace-nowrap text-center">À commander</th>
                    <th class="w-[92px] whitespace-nowrap text-right">P.U. achat</th>
                    <th class="w-[96px] whitespace-nowrap text-right">Total</th>
                  </tr>
                </thead>
                <tbody>
                  @for (line of group.lines; track line.reference) {
                    <tr>
                      <td>
                        <p class="font-medium leading-snug">{{ line.name | capitalize }}</p>
                        <p class="whitespace-nowrap font-mono text-[12px] muted">
                          {{ line.reference | ref }}
                        </p>
                        <!-- L'origine du besoin : indispensable pour arbitrer -->
                        <ul class="mt-1 space-y-0.5">
                          @for (src of line.sources; track src.kind + ':' + src.id) {
                            <li class="text-[11.5px] muted">
                              <a
                                [routerLink]="['/documents', src.kind, src.id]"
                                class="font-mono text-brand-700 hover:underline dark:text-brand-400"
                              >
                                {{ src.reference | ref }}
                              </a>
                              · {{ src.customer | capitalize }} · {{ src.quantity }} u.
                              @if (!src.firm) {
                                <span class="italic">(devis)</span>
                              }
                            </li>
                          }
                        </ul>
                      </td>
                      <td class="num text-center">{{ line.required }}</td>
                      <td class="num text-center muted">{{ line.available }}</td>
                      <td class="num text-center">
                        <span class="badge-heat num">{{ line.missing }}</span>
                      </td>
                      <td class="num whitespace-nowrap text-right">
                        {{ line.unitCost === null ? '—' : (line.unitCost | eur) }}
                      </td>
                      <td class="num whitespace-nowrap text-right font-semibold">
                        {{ line.totalCost === null ? '—' : (line.totalCost | eur) }}
                      </td>
                    </tr>
                  }
                </tbody>
              </table>
            </div>

            <footer
              class="flex flex-wrap items-center justify-between gap-3 border-t border-ink-200 px-5 py-3.5 dark:border-ink-800"
            >
              <p class="text-[12px] muted">
                Bon de commande à établir par {{ company.name }} — {{ company.address }}
              </p>
              <p class="num text-[15px] font-semibold">
                Total HT&nbsp;: {{ group.total | eur }}
              </p>
            </footer>
          </section>
        }
      </div>

      <p class="no-print mt-5 text-[12.5px] muted">
        Le stock disponible est affecté en priorité aux commandes fermes, de la plus
        ancienne à la plus récente. Un même article présent sur deux commandes n'est donc
        jamais compté deux fois comme disponible.
      </p>
    }
  `,
})
export class SupplyComponent implements OnInit {
  protected readonly store = inject(CommerceStore);
  protected readonly company = environment.company;
  protected readonly noSupplier = 'Fournisseur à référencer';

  protected readonly groups = computed<SupplyGroup[]>(() => this.store.supplyGroups());

  protected readonly totalItems = computed(() =>
    this.store.supplyNeeds().reduce((s, n) => s + n.missing, 0),
  );

  ngOnInit(): void {
    this.store.load();
  }

  protected print(): void {
    window.print();
  }
}
