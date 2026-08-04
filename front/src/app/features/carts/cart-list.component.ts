import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { CartService, CommandService, InvoiceService, QuoteService } from '../../core/api';
import { DOCUMENT_STATUS_LIST, DocumentStatus } from '../../core/models/document-status';
import { ValuedDocument } from '../../core/models/document-math';
import { CommerceStore } from '../../core/services/commerce-store.service';
import { ConfirmService } from '../../core/services/confirm.service';
import { ToastService } from '../../core/services/toast.service';
import { CapitalizePipe, EurPipe, FrDatePipe, RefPipe } from '../../shared/pipes/format.pipes';
import { EmptyStateComponent } from '../../shared/ui/empty-state.component';
import { IconComponent } from '../../shared/ui/icon.component';
import { PageHeaderComponent } from '../../shared/ui/page-header.component';
import { SearchInputComponent } from '../../shared/ui/search-input.component';
import { StatusBadgeComponent } from '../../shared/ui/status-badge.component';

/**
 * Liste de tous les documents commerciaux, toutes étapes confondues.
 *
 * Depuis l'introduction de `Quote` / `Command` / `Invoice`, ces documents ne
 * sont plus tous des `Cart` : la source unique est désormais `CommerceStore`,
 * qui compose déjà le portefeuille à partir des quatre entités (voir
 * `commerce-store.service.ts`). Cet écran ne fait plus sa propre requête.
 */
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
      <button type="button" class="btn-secondary" (click)="store.reload()" [disabled]="store.loading()">
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

      @if (store.loading()) {
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
              @for (d of filtered(); track d.kind + ':' + d.id) {
                <tr>
                  <td>
                    <a
                      [routerLink]="['/documents', d.kind, d.id]"
                      class="font-mono text-[13px] font-medium text-brand-700 hover:underline dark:text-brand-400"
                    >
                      {{ d.reference | ref }}
                    </a>
                  </td>
                  <td>
                    @if (d.customer) {
                      <p class="font-medium">
                        {{ d.customer.ctmFirstName | capitalize }} {{ d.customer.ctmLastName | capitalize }}
                      </p>
                      <p class="truncate text-[12.5px] muted">{{ d.customer.ctmEmail }}</p>
                    } @else {
                      <span class="text-[13px] italic muted">Client non identifié</span>
                    }
                  </td>
                  <td><app-status-badge [status]="d.status" /></td>
                  <td class="num text-center">{{ d.totals.lines.length }}</td>
                  <td class="num text-right font-semibold">{{ d.totals.totalTtc | eur }}</td>
                  <td class="text-[13px] muted">{{ d.date | frDate }}</td>
                  <td>
                    <div class="flex justify-end gap-1">
                      <a [routerLink]="['/documents', d.kind, d.id]" class="btn-icon" title="Ouvrir">
                        <app-icon name="edit" [size]="16" />
                      </a>
                      <a
                        [routerLink]="['/documents', d.kind, d.id, 'impression']"
                        class="btn-icon"
                        title="Aperçu / impression"
                      >
                        <app-icon name="print" [size]="16" />
                      </a>
                      <button
                        type="button"
                        class="btn-icon hover:bg-red-50 hover:text-red-600 dark:hover:bg-red-500/10"
                        (click)="remove(d)"
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
  protected readonly store = inject(CommerceStore);
  private readonly cartApi = inject(CartService);
  private readonly quoteApi = inject(QuoteService);
  private readonly commandApi = inject(CommandService);
  private readonly invoiceApi = inject(InvoiceService);
  private readonly toast = inject(ToastService);
  private readonly confirm = inject(ConfirmService);

  protected readonly chipActive =
    'border-brand-600 bg-brand-600 text-white dark:border-brand-500 dark:bg-brand-600';
  protected readonly chipIdle =
    'border-ink-200 bg-white text-ink-700 hover:bg-ink-50 dark:border-ink-700 dark:bg-ink-900 dark:text-ink-300 dark:hover:bg-ink-800';

  protected readonly statuses = DOCUMENT_STATUS_LIST;
  protected readonly search = signal('');
  protected readonly statusFilter = signal<DocumentStatus | null>(null);

  protected readonly items = computed(() => this.store.documents());

  protected readonly filtered = computed(() => {
    const q = this.search().trim().toLowerCase();
    const status = this.statusFilter();

    return this.items()
      .filter((d) => {
        if (status && d.status !== status) return false;
        if (!q) return true;
        const haystack = [d.reference, d.customer?.ctmFirstName, d.customer?.ctmLastName, d.customer?.ctmEmail]
          .filter(Boolean)
          .join(' ')
          .toLowerCase();
        return haystack.includes(q);
      })
      .sort((a, b) => (b.id ?? 0) - (a.id ?? 0));
  });

  ngOnInit(): void {
    this.store.load();
  }

  countBy(status: DocumentStatus): number {
    return this.items().filter((d) => d.status === status).length;
  }

  async remove(doc: ValuedDocument): Promise<void> {
    const ok = await this.confirm.askDelete(`le document « ${doc.reference?.toUpperCase()} »`);
    if (!ok) return;
    try {
      switch (doc.kind) {
        case 'cart':
          await firstValueFrom(this.cartApi.delete(doc.id));
          break;
        case 'quote':
          await firstValueFrom(this.quoteApi.delete(doc.id));
          break;
        case 'command':
          await firstValueFrom(this.commandApi.delete(doc.id));
          break;
        case 'invoice':
          await firstValueFrom(this.invoiceApi.delete(doc.id));
          break;
      }
      this.toast.success('Document supprimé');
      await this.store.reload();
    } catch {
      /* déjà notifié */
    }
  }
}
