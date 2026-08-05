import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { LowerCasePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { CartService, QuoteService } from '../../core/api';
import {
  Cart,
  QUOTE_STATUSES,
  QUOTE_STATUS_LABELS,
  Quote,
  QuoteLine,
  QuoteStatus,
  isQuoteEditable,
} from '../../core/models/api.models';
import {
  QuoteTotals,
  isExpired,
  ownTotals,
  rootNumber,
  totalsGap,
} from '../../core/models/quote-math';
import { ConfirmService } from '../../core/services/confirm.service';
import { ToastService } from '../../core/services/toast.service';
import { CapitalizePipe, EurPipe, FrDatePipe, RefPipe, TauxPctPipe } from '../../shared/pipes/format.pipes';
import { EmptyStateComponent } from '../../shared/ui/empty-state.component';
import { IconComponent } from '../../shared/ui/icon.component';
import { PageHeaderComponent } from '../../shared/ui/page-header.component';
import { SearchInputComponent } from '../../shared/ui/search-input.component';

/**
 * Devis émis.
 *
 * À distinguer de « Devis & factures », qui suit les *affaires* au fil de leur
 * cycle. Ici on regarde les **documents** : ce qui a été effectivement transmis
 * au client, à quel prix, et ce qu'il en est advenu.
 *
 * Les devis sont regroupés par dossier — toutes les révisions d'une même
 * affaire portent le numéro d'origine —, la version courante en tête et les
 * précédentes dépliables. C'est la lecture qui compte au quotidien : « où en
 * est ce devis », pas « combien de documents existent ».
 */
@Component({
  selector: 'app-quote-list',
  standalone: true,
  imports: [
    RouterLink,
    LowerCasePipe,
    PageHeaderComponent,
    SearchInputComponent,
    EmptyStateComponent,
    IconComponent,
    CapitalizePipe,
    EurPipe,
    FrDatePipe,
    RefPipe,
    TauxPctPipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <app-page-header
      title="Devis émis"
      subtitle="Les documents transmis aux clients, avec leurs prix figés."
    />

    <!--
      Sans identifiant, aucune route de mutation n'est atteignable. On le dit
      une fois, clairement, plutôt que de laisser des boutons qui échouent.
    -->
    @if (missingIds() > 0) {
      <div
        class="mb-5 flex items-start gap-3 rounded-xl border border-red-200 bg-red-50 p-4 dark:border-red-500/30 dark:bg-red-500/10"
      >
        <span class="mt-0.5 shrink-0 text-red-600 dark:text-red-400">
          <app-icon name="alert" [size]="17" />
        </span>
        <div>
          <p class="text-[13px] font-medium text-red-900 dark:text-red-200">
            {{ missingIds() }} devis sans identifiant
          </p>
          <p class="mt-1 text-[12.5px] text-red-800 dark:text-red-300/90">
            <code class="font-mono">QuoteDTO</code> ne renvoie pas
            <code class="font-mono">quoteId</code> est absent de ces réponses, alors que toutes
            les routes de mutation sont adressées par identifiant. Changer un statut ou supprimer
            un devis est donc impossible pour ces documents.
          </p>
        </div>
      </div>
    }

    <!-- Repères -->
    <div class="mb-5 grid gap-3 sm:grid-cols-2 lg:grid-cols-5">
      <div class="card card-pad">
        <p class="text-[11px] font-semibold uppercase tracking-wider muted">Transmis, en attente</p>
        <p class="mt-1 text-2xl font-bold num text-brand-600">{{ pendingCount() }}</p>
        <p class="text-[12px] muted">{{ pendingAmount() | eur }} en jeu</p>
      </div>
      <div class="card card-pad">
        <p class="text-[11px] font-semibold uppercase tracking-wider muted">Brouillons</p>
        <p class="mt-1 text-2xl font-bold num text-ink-400">{{ draftCount() }}</p>
        <p class="text-[12px] muted">pas encore transmis</p>
      </div>
      <div class="card card-pad">
        <p class="text-[11px] font-semibold uppercase tracking-wider muted">Acceptés</p>
        <p class="mt-1 text-2xl font-bold num text-emerald-600">{{ acceptedCount() }}</p>
        <p class="text-[12px] muted">{{ acceptedAmount() | eur }} signés</p>
      </div>
      <div class="card card-pad">
        <p class="text-[11px] font-semibold uppercase tracking-wider muted">Taux d'acceptation</p>
        <p class="mt-1 text-2xl font-bold num">{{ winRate() }}</p>
        <p class="text-[12px] muted">sur les devis tranchés</p>
      </div>
      <div class="card card-pad">
        <p class="text-[11px] font-semibold uppercase tracking-wider muted">À relancer</p>
        <p class="mt-1 text-2xl font-bold num" [class.text-amber-600]="expiringCount() > 0">
          {{ expiringCount() }}
        </p>
        <p class="text-[12px] muted">expirés sans réponse</p>
      </div>
    </div>

    <div class="mb-4 flex flex-wrap items-center gap-3">
      <app-search-input
        class="min-w-[16rem] flex-1"
        placeholder="Numéro de devis, article, panier…"
        (valueChange)="search.set($event)"
      />
      <select class="input w-auto" [value]="statusFilter()" (change)="setStatus($event)">
        <option value="">Tous les statuts</option>
        @for (s of statuses; track s) {
          <option [value]="s">{{ statusLabels[s] }}</option>
        }
      </select>
    </div>

    @if (loading()) {
      <div class="space-y-2">
        @for (i of [1, 2, 3, 4]; track i) {
          <div class="skeleton h-24 w-full"></div>
        }
      </div>
    } @else if (folders().length === 0) {
      <app-empty-state
        icon="invoice"
        title="Aucun devis émis"
        message="Les devis se créent depuis un document, une fois le chiffrage arrêté."
      />
    } @else {
      <div class="space-y-3">
        @for (folder of folders(); track folder.root) {
          <div class="card overflow-hidden">
            <!-- Version courante -->
            <div class="flex flex-wrap items-start gap-4 p-4">
              <div class="min-w-0 flex-1">
                <div class="flex flex-wrap items-center gap-2">
                  <span class="font-mono text-[13px] font-semibold">
                    {{ folder.current.qotNumber | ref }}
                  </span>
                  <span [class]="badgeClass(folder.current.qotStatus)">
                    {{ statusLabels[folder.current.qotStatus] }}
                  </span>
                  @if (folder.versions.length > 1) {
                    <span
                      class="inline-flex items-center rounded-full bg-ink-100 px-2 py-0.5 text-[11px] font-medium text-ink-600 dark:bg-ink-800 dark:text-ink-300"
                      >version {{ folder.versions.length }}</span
                    >
                  }
                  @if (isStale(folder.current)) {
                    <span
                      class="inline-flex items-center gap-1 rounded-full bg-amber-50 px-2 py-0.5 text-[11px] font-medium text-amber-700 dark:bg-amber-500/10 dark:text-amber-400"
                    >
                      <app-icon name="clock" [size]="11" /> expiré
                    </span>
                  }
                </div>

                <p class="mt-1.5 text-[12.5px] muted">
                  Émis le {{ folder.current.qotCreatedDate | frDate }}
                  @if (folder.current.expirationDate) {
                    · valable jusqu'au {{ folder.current.expirationDate | frDate }}
                  }
                  @if (cartRefOf(folder.current); as ref) {
                    · document {{ ref | ref }}
                  }
                </p>

                <p class="mt-1 text-[12.5px] muted">
                  {{ own(folder.current).lineCount }} ligne(s) ·
                  {{ own(folder.current).itemCount }} article(s)
                </p>
              </div>

              <div class="text-right">
                <p class="text-lg font-bold num">{{ own(folder.current).totalTTC | eur }}</p>
                <p class="text-[12px] muted">{{ own(folder.current).totalHT | eur }} HT</p>

                <!--
                  Le serveur ajoute les totaux du parent à ceux de l'enfant. Si
                  la révision reprend toutes les lignes, ce cumul double le
                  montant. On affiche l'écart plutôt que de choisir.
                -->
                @if (gap(folder.current) !== 0) {
                  <p
                    class="mt-1 text-[11.5px] text-amber-700 dark:text-amber-400"
                    title="L'API additionne les totaux du devis parent à ceux de ce devis."
                  >
                    API : {{ folder.current.totalTTC | eur }}
                  </p>
                }
              </div>

              <div class="flex shrink-0 items-center gap-1.5">
                @if (folder.current.cartId) {
                  <a
                    [routerLink]="cartLink(folder.current)"
                    class="btn-ghost btn-sm"
                    title="Ouvrir le document d'origine"
                  >
                    <app-icon name="cart" [size]="15" />
                  </a>
                }
                <button
                  type="button"
                  class="btn-icon"
                  (click)="toggle(folder.root)"
                  [attr.aria-expanded]="expanded() === folder.root"
                  aria-label="Voir le détail"
                >
                  <app-icon
                    [name]="expanded() === folder.root ? 'chevronDown' : 'chevronRight'"
                    [size]="16"
                  />
                </button>
              </div>
            </div>

            @if (expanded() === folder.root) {
              <!-- Lignes figées -->
              <div class="border-t border-ink-100 bg-ink-50/50 px-4 py-3 dark:border-ink-800/70 dark:bg-ink-800/25">
                <p class="mb-2 text-[11px] font-semibold uppercase tracking-wider muted">
                  Lignes figées à l'émission
                </p>
                <table class="w-full text-[12.5px]">
                  <thead>
                    <tr class="text-left text-[11px] uppercase tracking-wider muted">
                      <th class="pb-1.5 pr-3 font-semibold">Désignation</th>
                      <th class="pb-1.5 pr-3 text-right font-semibold">P.U. HT</th>
                      <th class="pb-1.5 pr-3 text-center font-semibold">Qté</th>
                      <th class="pb-1.5 pr-3 text-right font-semibold">TVA</th>
                      <th class="pb-1.5 text-right font-semibold">Total TTC</th>
                    </tr>
                  </thead>
                  <tbody>
                    @for (line of folder.current.qotLines ?? []; track line.qotLnId) {
                      <tr class="border-t border-ink-100 dark:border-ink-800/70">
                        <td class="py-1.5 pr-3">
                          <span class="font-medium">{{ line.articleName | capitalize }}</span>
                          <span class="ml-2 font-mono text-[11px] muted">{{ line.articleRef | ref }}</span>
                        </td>
                        <td class="py-1.5 pr-3 text-right num">{{ line.qotLnPriceHT | eur }}</td>
                        <td class="py-1.5 pr-3 text-center">
                          <!--
                            La quantité n'est modifiable que sur un devis en
                            attente. Le serveur applique la même règle et
                            répond 409 sinon : l'interface l'anticipe plutôt
                            que de proposer un geste voué à l'échec.
                          -->
                          @if (editable(folder.current) && folder.current.quoteId) {
                            <input
                              type="number"
                              min="0"
                              step="1"
                              class="w-16 rounded-md border border-ink-200 bg-white px-2 py-1 text-center text-[12.5px] tabular-nums focus:border-brand-500 focus:outline-none dark:border-ink-700 dark:bg-ink-900"
                              [value]="line.qotLnQuantity"
                              [disabled]="savingLine() === line.qotLnId"
                              (change)="changeQuantity(folder.current, line, $event)"
                              [attr.aria-label]="'Quantité de ' + line.articleName"
                            />
                          } @else {
                            <span class="num">{{ line.qotLnQuantity }}</span>
                          }
                        </td>
                        <td class="py-1.5 pr-3 text-right num muted">{{ line.tvaRate | tauxPct }}</td>
                        <td class="py-1.5 text-right num font-medium">{{ line.totalTTC | eur }}</td>
                      </tr>
                    } @empty {
                      <tr>
                        <td colspan="5" class="py-3 text-center muted">
                          Ce devis ne comporte aucune ligne.
                        </td>
                      </tr>
                    }
                  </tbody>
                </table>
              </div>

              <!-- Versions précédentes -->
              @if (folder.versions.length > 1) {
                <div class="border-t border-ink-100 px-4 py-3 dark:border-ink-800/70">
                  <p class="mb-2 text-[11px] font-semibold uppercase tracking-wider muted">
                    Versions précédentes
                  </p>
                  <ul class="space-y-1.5">
                    @for (previous of folder.versions.slice(1); track previous.qotNumber) {
                      <li class="flex flex-wrap items-center gap-3 text-[12.5px]">
                        <span class="font-mono">{{ previous.qotNumber | ref }}</span>
                        <span [class]="badgeClass(previous.qotStatus)">
                          {{ statusLabels[previous.qotStatus] }}
                        </span>
                        <span class="muted">{{ previous.qotCreatedDate | frDate }}</span>
                        <span class="ml-auto num">{{ own(previous).totalTTC | eur }}</span>
                      </li>
                    }
                  </ul>
                  <p class="mt-2 text-[12px] muted">
                    Chaque version reste consultable : c'est la trace de ce qui a été proposé, et
                    à quel prix.
                  </p>
                </div>
              }

              <!-- Actions -->
              <div class="flex flex-wrap items-center gap-2 border-t border-ink-100 px-4 py-3 dark:border-ink-800/70">
                @if (folder.current.quoteId) {
                  @if (folder.current.qotStatus === 'CREATED') {
                    <!--
                      Un brouillon ne peut pas être « accepté » : le client ne
                      l'a pas encore reçu. Le seul geste possible est de le
                      transmettre, ce qui le fige.
                    -->
                    <button
                      type="button"
                      class="btn-primary btn-sm"
                      (click)="setStatusOf(folder.current, 'PENDING')"
                    >
                      <app-icon name="send" [size]="15" /> Transmettre au client
                    </button>
                    <p class="text-[12.5px] muted">
                      Modifiable tant qu'il n'est pas transmis.
                    </p>
                  } @else if (folder.current.qotStatus === 'PENDING') {
                    <button
                      type="button"
                      class="btn-primary btn-sm"
                      (click)="setStatusOf(folder.current, 'ACCEPTED')"
                    >
                      <app-icon name="check" [size]="15" /> Accepté par le client
                    </button>
                    <button
                      type="button"
                      class="btn-secondary btn-sm"
                      (click)="setStatusOf(folder.current, 'REJECTED')"
                    >
                      Refusé
                    </button>
                  } @else {
                    <p class="text-[12.5px] muted">
                      Ce devis est {{ statusLabels[folder.current.qotStatus] | lowercase }} : il ne
                      se modifie plus. Toute évolution passe par une révision, depuis le document
                      d'origine.
                    </p>
                  }
                  <button
                    type="button"
                    class="btn-ghost btn-sm ml-auto text-red-600 dark:text-red-400"
                    (click)="remove(folder.current)"
                  >
                    <app-icon name="trash" [size]="15" /> Supprimer
                  </button>
                } @else {
                  <p class="text-[12.5px] text-red-700 dark:text-red-400">
                    Actions indisponibles : l'API ne renvoie pas l'identifiant de ce devis.
                  </p>
                }
              </div>
            }
          </div>
        }
      </div>
    }
  `,
})
export class QuoteListComponent implements OnInit {
  private readonly api = inject(QuoteService);
  private readonly cartApi = inject(CartService);
  private readonly toast = inject(ToastService);
  private readonly confirm = inject(ConfirmService);

  protected readonly statuses = QUOTE_STATUSES;
  protected readonly statusLabels = QUOTE_STATUS_LABELS;

  protected readonly quotes = signal<Quote[]>([]);
  private readonly carts = signal<Cart[]>([]);
  protected readonly loading = signal(true);
  protected readonly search = signal('');
  protected readonly statusFilter = signal<QuoteStatus | ''>('');
  protected readonly expanded = signal<string | null>(null);
  /** Ligne en cours d'enregistrement, pour neutraliser son champ. */
  protected readonly savingLine = signal<number | null>(null);

  protected readonly missingIds = computed(
    () => this.quotes().filter((q) => !q.quoteId).length,
  );

  /**
   * Regroupement par dossier : toutes les révisions d'une même affaire
   * partagent le numéro de leur version d'origine.
   */
  private readonly allFolders = computed(() => {
    const byRoot = new Map<string, Quote[]>();

    for (const quote of this.quotes()) {
      const key = rootNumber(quote);
      const bucket = byRoot.get(key);
      if (bucket) bucket.push(quote);
      else byRoot.set(key, [quote]);
    }

    return [...byRoot.entries()]
      .map(([root, versions]) => {
        // La version courante est la plus récente ; à défaut de date, le
        // numéro d'indice le plus élevé fait foi.
        const ordered = [...versions].sort(byRecency);
        return { root, current: ordered[0], versions: ordered };
      })
      .sort((a, b) => byRecency(a.current, b.current));
  });

  protected readonly folders = computed(() => {
    const term = this.search().trim().toLowerCase();
    const status = this.statusFilter();

    return this.allFolders().filter((folder) => {
      if (status && folder.current.qotStatus !== status) return false;
      if (!term) return true;

      const haystack = [
        folder.current.qotNumber,
        this.cartRefOf(folder.current) ?? '',
        ...(folder.current.qotLines ?? []).flatMap((l) => [l.articleName, l.articleRef]),
      ]
        .join(' ')
        .toLowerCase();
      return haystack.includes(term);
    });
  });

  protected readonly pendingCount = computed(
    () => this.allFolders().filter((f) => f.current.qotStatus === 'PENDING').length,
  );
  protected readonly draftCount = computed(
    () => this.allFolders().filter((f) => f.current.qotStatus === 'CREATED').length,
  );
  protected readonly acceptedCount = computed(
    () => this.allFolders().filter((f) => f.current.qotStatus === 'ACCEPTED').length,
  );
  protected readonly pendingAmount = computed(() =>
    this.allFolders()
      .filter((f) => f.current.qotStatus === 'PENDING')
      .reduce((sum, f) => sum + ownTotals(f.current.qotLines).totalTTC, 0),
  );
  protected readonly acceptedAmount = computed(() =>
    this.allFolders()
      .filter((f) => f.current.qotStatus === 'ACCEPTED')
      .reduce((sum, f) => sum + ownTotals(f.current.qotLines).totalTTC, 0),
  );
  protected readonly expiringCount = computed(
    () =>
      this.allFolders().filter((f) => f.current.qotStatus === 'PENDING' && isExpired(f.current))
        .length,
  );

  /** Sur les seuls devis tranchés : un devis en attente n'est ni gagné ni perdu. */
  protected readonly winRate = computed(() => {
    const decided = this.allFolders().filter(
      (f) => f.current.qotStatus === 'ACCEPTED' || f.current.qotStatus === 'REJECTED',
    );
    if (decided.length === 0) return '—';
    const won = decided.filter((f) => f.current.qotStatus === 'ACCEPTED').length;
    return `${Math.round((won / decided.length) * 100)} %`;
  });

  async ngOnInit(): Promise<void> {
    await this.load();
  }

  private async load(): Promise<void> {
    this.loading.set(true);
    const [quotes, carts] = await Promise.all([
      firstValueFrom(this.api.list()).catch(() => [] as Quote[]),
      firstValueFrom(this.cartApi.list()).catch(() => [] as Cart[]),
    ]);
    this.quotes.set(quotes);
    this.carts.set(carts);
    this.loading.set(false);
  }

  /** Totaux des seules lignes du devis, hors cumul du parent. */
  protected own(quote: Quote): QuoteTotals {
    return ownTotals(quote.qotLines);
  }

  /** Écart entre le total annoncé par l'API et celui des lignes. */
  protected gap(quote: Quote): number {
    return totalsGap(quote);
  }

  protected editable(quote: Quote): boolean {
    return isQuoteEditable(quote.qotStatus);
  }

  protected isStale(quote: Quote): boolean {
    return isExpired(quote);
  }

  protected cartLink(quote: Quote): string[] {
    return quote.cartId ? ['/documents', String(quote.cartId)] : ['/documents'];
  }

  /**
   * Le DTO ne porte que l'identifiant du panier ; la référence lisible se
   * retrouve dans la liste des documents déjà chargée.
   */
  protected cartRefOf(quote: Quote): string | null {
    return this.carts().find((c) => c.crtId === quote.cartId)?.crtRef ?? null;
  }

  protected setStatus(event: Event): void {
    this.statusFilter.set((event.target as HTMLSelectElement).value as QuoteStatus | '');
  }

  protected toggle(root: string): void {
    this.expanded.set(this.expanded() === root ? null : root);
  }

  protected badgeClass(status: QuoteStatus): string {
    const base = 'inline-flex items-center rounded-full px-2 py-0.5 text-[11px] font-medium ';
    switch (status) {
      case 'ACCEPTED':
        return base + 'bg-emerald-50 text-emerald-700 dark:bg-emerald-500/10 dark:text-emerald-400';
      case 'CREATED':
        return base + 'bg-ink-100 text-ink-600 ring-1 ring-inset ring-ink-300 dark:bg-ink-800 dark:text-ink-300 dark:ring-ink-600';
      case 'PENDING':
        return base + 'bg-brand-50 text-brand-700 dark:bg-brand-500/10 dark:text-brand-400';
      case 'REJECTED':
        return base + 'bg-red-50 text-red-700 dark:bg-red-500/10 dark:text-red-400';
      case 'REVISITED':
        return base + 'bg-ink-100 text-ink-500 line-through dark:bg-ink-800 dark:text-ink-400';
      default:
        return base + 'bg-ink-100 text-ink-500 dark:bg-ink-800 dark:text-ink-400';
    }
  }

  /**
   * Modifie la quantité d'une ligne figée.
   *
   * Le serveur désigne la ligne par la **référence d'article**, pas par son
   * identifiant : `PatchQuoteLineQuantity` ne porte que `artRef`. La casse n'a
   * pas d'importance, la comparaison étant faite avec `equalsIgnoreCase`.
   */
  protected async changeQuantity(quote: Quote, line: QuoteLine, event: Event): Promise<void> {
    const input = event.target as HTMLInputElement;
    const value = Number(input.value);

    if (!quote.quoteId || !Number.isFinite(value) || value < 0) {
      input.value = String(line.qotLnQuantity);
      return;
    }
    if (value === line.qotLnQuantity) return;

    this.savingLine.set(line.qotLnId);
    try {
      await firstValueFrom(
        this.api.patchQuantity(quote.quoteId, {
          artRef: line.articleRef,
          qotLineQuantity: value,
        }),
      );
      this.toast.success('Quantité modifiée', `${line.articleName} — ${value}`);
      await this.load();
    } catch (err) {
      // Le devis a pu changer de statut entre-temps : on remet la valeur
      // affichée plutôt que de laisser un champ qui ment.
      input.value = String(line.qotLnQuantity);
      if (err instanceof HttpErrorResponse && err.status === 409) {
        this.toast.warning(
          'Devis non modifiable',
          `${quote.qotNumber} n'est plus en attente : sa quantité ne peut plus changer. Passez par une révision.`,
        );
      }
    } finally {
      this.savingLine.set(null);
    }
  }

  protected async setStatusOf(quote: Quote, status: QuoteStatus): Promise<void> {
    if (!quote.quoteId) return;
    try {
      await firstValueFrom(this.api.patchStatus(quote.quoteId, status));
      this.toast.success(`Devis ${quote.qotNumber}`, this.statusLabels[status]);
      await this.load();
    } catch {
      /* l'intercepteur a signalé l'erreur */
    }
  }

  protected async remove(quote: Quote): Promise<void> {
    if (!quote.quoteId) return;

    const confirmed = await this.confirm.ask({
      title: 'Supprimer ce devis ?',
      message: `${quote.qotNumber} — un devis émis est la trace de ce qui a été proposé au client. Le passer en « refusé » ou « clos » conserve cette trace ; le supprimer l'efface définitivement.`,
      confirmLabel: 'Supprimer quand même',
      danger: true,
    });
    if (!confirmed) return;

    try {
      await firstValueFrom(this.api.delete(quote.quoteId));
      this.toast.success('Devis supprimé', quote.qotNumber);
      await this.load();
    } catch {
      /* idem */
    }
  }
}

/** Du plus récent au plus ancien ; à date égale, l'indice le plus élevé prime. */
function byRecency(a: Quote, b: Quote): number {
  const ta = a.qotCreatedDate ? new Date(a.qotCreatedDate).getTime() : 0;
  const tb = b.qotCreatedDate ? new Date(b.qotCreatedDate).getTime() : 0;
  if (ta !== tb) return tb - ta;
  return (b.qotNumber ?? '').localeCompare(a.qotNumber ?? '');
}
