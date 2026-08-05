import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { ArticleService, InventoryService } from '../../core/api';
import { Article, Inventory } from '../../core/models/api.models';
import {
  STALE_AFTER_DAYS,
  STOCK_STATUS_LABELS,
  StockReading,
  readStock,
  withVariations,
} from '../../core/models/stock';
import { ConfirmService } from '../../core/services/confirm.service';
import { ToastService } from '../../core/services/toast.service';
import { CapitalizePipe, FrDatePipe, RefPipe } from '../../shared/pipes/format.pipes';
import { EmptyStateComponent } from '../../shared/ui/empty-state.component';
import { FieldErrorComponent } from '../../shared/ui/field-error.component';
import { IconComponent } from '../../shared/ui/icon.component';
import { ModalComponent } from '../../shared/ui/modal.component';
import { PageHeaderComponent } from '../../shared/ui/page-header.component';
import { SearchInputComponent } from '../../shared/ui/search-input.component';

/**
 * Relevés d'inventaire : le stock constaté, article par article.
 *
 * Un relevé est un fait daté — quelqu'un est allé compter. C'est la seule
 * valeur de stock dont on soit certain, et elle sert d'ancrage : le stock
 * d'aujourd'hui, c'est le dernier comptage augmenté des entrées postérieures.
 *
 * L'écran s'organise donc autour de l'article plutôt que du relevé : ce qui
 * intéresse l'utilisateur, c'est « où en est cet article », pas « quels
 * relevés existent ». L'historique de chacun se déplie à la demande.
 */
@Component({
  selector: 'app-inventory',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    PageHeaderComponent,
    SearchInputComponent,
    EmptyStateComponent,
    ModalComponent,
    FieldErrorComponent,
    IconComponent,
    CapitalizePipe,
    FrDatePipe,
    RefPipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <app-page-header
      title="Inventaire"
      subtitle="Le stock réellement constaté, article par article."
    >
      <button type="button" class="btn-primary" (click)="openCreate()" [disabled]="loading()">
        <app-icon name="plus" [size]="16" /> Nouveau relevé
      </button>
    </app-page-header>

    <!--
      Sans date de relevé, la fonctionnalité perd son objet. On le signale une
      fois, en haut, plutôt que de répéter « non daté » sur chaque ligne.
    -->
    @if (undatedCount() > 0) {
      <div
        class="mb-5 flex items-start gap-3 rounded-xl border border-amber-200 bg-amber-50 p-4 dark:border-amber-500/30 dark:bg-amber-500/10"
      >
        <span class="mt-0.5 shrink-0 text-amber-600 dark:text-amber-400">
          <app-icon name="alert" [size]="17" />
        </span>
        <div>
          <p class="text-[13px] font-medium text-amber-900 dark:text-amber-200">
            {{ undatedCount() }} relevé(s) sans date
          </p>
          <p class="mt-1 text-[12.5px] text-amber-800 dark:text-amber-300/90">
            L'entité <code class="font-mono">Inventory</code> ne déclare pas
            <code class="font-mono">&#64;EntityListeners</code>, ce qui rend
            <code class="font-mono">&#64;CreatedDate</code> inerte : la date reste vide à
            l'enregistrement. Or c'est elle qui donne son sens à un relevé — savoir qu'il y avait
            12 unités sans savoir quand ne sert à rien. Le correctif est dans
            <code class="font-mono">backend-patch/Inventory-blocages.patch</code>.
          </p>
        </div>
      </div>
    }

    <!-- Repères -->
    <div class="mb-5 grid gap-3 sm:grid-cols-2 lg:grid-cols-4">
      <div class="card card-pad">
        <p class="text-[11px] font-semibold uppercase tracking-wider muted">Articles suivis</p>
        <p class="mt-1 text-2xl font-bold num">{{ trackedCount() }}</p>
        <p class="text-[12px] muted">sur {{ articles().length }} au catalogue</p>
      </div>
      <div class="card card-pad">
        <p class="text-[11px] font-semibold uppercase tracking-wider muted">Jamais inventoriés</p>
        <p class="mt-1 text-2xl font-bold num text-ink-400">{{ neverCount() }}</p>
        <p class="text-[12px] muted">aucun comptage enregistré</p>
      </div>
      <div class="card card-pad">
        <p class="text-[11px] font-semibold uppercase tracking-wider muted">Écarts constatés</p>
        <p class="mt-1 text-2xl font-bold num" [class.text-amber-600]="driftCount() > 0">
          {{ driftCount() }}
        </p>
        <p class="text-[12px] muted">comptage ≠ stock annoncé</p>
      </div>
      <div class="card card-pad">
        <p class="text-[11px] font-semibold uppercase tracking-wider muted">Relevés périmés</p>
        <p class="mt-1 text-2xl font-bold num" [class.text-red-600]="staleCount() > 0">
          {{ staleCount() }}
        </p>
        <p class="text-[12px] muted">plus de {{ staleAfter }} jours</p>
      </div>
    </div>

    <div class="mb-4 flex flex-wrap items-center gap-3">
      <app-search-input
        class="min-w-[16rem] flex-1"
        placeholder="Référence ou nom d'article…"
        (valueChange)="search.set($event)"
      />
      <select class="input w-auto" [value]="filter()" (change)="setFilter($event)">
        <option value="all">Tous les articles</option>
        <option value="tracked">Déjà inventoriés</option>
        <option value="never">Jamais inventoriés</option>
        <option value="drift">Avec écart</option>
        <option value="stale">Relevé périmé</option>
      </select>
    </div>

    @if (loading()) {
      <div class="space-y-2">
        @for (i of [1, 2, 3, 4, 5]; track i) {
          <div class="skeleton h-16 w-full"></div>
        }
      </div>
    } @else if (rows().length === 0) {
      <app-empty-state
        icon="boxes"
        title="Aucun article ne correspond"
        message="Ajustez la recherche ou le filtre."
      />
    } @else {
      <div class="card overflow-hidden">
        <table class="w-full text-[13px]">
          <thead class="border-b border-ink-200 bg-ink-50/60 dark:border-ink-800 dark:bg-ink-800/40">
            <tr class="text-left">
              <th class="px-4 py-2.5 text-[11px] font-semibold uppercase tracking-wider muted">Article</th>
              <th class="px-4 py-2.5 text-right text-[11px] font-semibold uppercase tracking-wider muted">Compté</th>
              <th class="px-4 py-2.5 text-right text-[11px] font-semibold uppercase tracking-wider muted">Annoncé</th>
              <th class="px-4 py-2.5 text-right text-[11px] font-semibold uppercase tracking-wider muted">Écart</th>
              <th class="px-4 py-2.5 text-[11px] font-semibold uppercase tracking-wider muted">Dernier relevé</th>
              <th class="w-px px-4 py-2.5"></th>
            </tr>
          </thead>
          <tbody>
            @for (row of rows(); track row.article.artId) {
              <tr class="border-b border-ink-100 last:border-0 dark:border-ink-800/70">
                <td class="px-4 py-3">
                  <p class="font-medium">{{ row.article.artName | capitalize }}</p>
                  <p class="font-mono text-[11px] muted">{{ row.article.artReference | ref }}</p>
                </td>
                <td class="px-4 py-3 text-right num font-semibold">
                  @if (row.reading.countedStock !== null) {
                    {{ row.reading.countedStock }}
                  } @else {
                    <span class="muted">—</span>
                  }
                </td>
                <td class="px-4 py-3 text-right num">{{ row.reading.reportedStock }}</td>
                <td class="px-4 py-3 text-right num">
                  @if (row.reading.drift === null) {
                    <span class="muted">—</span>
                  } @else if (row.reading.drift === 0) {
                    <span class="text-emerald-600 dark:text-emerald-400">0</span>
                  } @else {
                    <span [class]="row.reading.drift > 0 ? 'text-amber-600 dark:text-amber-400' : 'text-red-600 dark:text-red-400'">
                      {{ row.reading.drift > 0 ? '+' : '' }}{{ row.reading.drift }}
                    </span>
                  }
                </td>
                <td class="px-4 py-3">
                  <span [class]="badgeClass(row.reading)">{{ statusLabels[row.reading.status] }}</span>
                  @if (row.reading.ageInDays !== null) {
                    <span class="ml-1.5 text-[12px] muted">
                      {{ row.reading.lastInventory?.invDate | frDate }}
                    </span>
                  }
                </td>
                <td class="px-4 py-3">
                  <div class="flex items-center justify-end gap-1">
                    @if (row.history.length > 0) {
                      <button
                        type="button"
                        class="btn-icon"
                        (click)="toggle(row.article.artId)"
                        [attr.aria-label]="expanded() === row.article.artId ? 'Masquer l\\'historique' : 'Voir l\\'historique'"
                        [attr.aria-expanded]="expanded() === row.article.artId"
                      >
                        <app-icon
                          [name]="expanded() === row.article.artId ? 'chevronDown' : 'chevronRight'"
                          [size]="16"
                        />
                      </button>
                    }
                    <button
                      type="button"
                      class="btn-secondary btn-sm"
                      (click)="openCreate(row.article.artId)"
                    >
                      Compter
                    </button>
                  </div>
                </td>
              </tr>

              <!-- Historique déplié -->
              @if (expanded() === row.article.artId) {
                <tr class="border-b border-ink-100 bg-ink-50/50 dark:border-ink-800/70 dark:bg-ink-800/25">
                  <td colspan="6" class="px-4 py-3">
                    <p class="mb-2 text-[11px] font-semibold uppercase tracking-wider muted">
                      Historique des comptages
                    </p>
                    <ul class="space-y-1.5">
                      @for (entry of row.history; track entry.inventory.invId) {
                        <li class="flex items-center gap-3 text-[12.5px]">
                          <span class="w-28 shrink-0 muted">
                            @if (entry.inventory.invDate) {
                              {{ entry.inventory.invDate | frDate }}
                            } @else {
                              <span class="italic">non daté</span>
                            }
                          </span>
                          <span class="num w-12 shrink-0 text-right font-semibold">
                            {{ entry.inventory.invStock }}
                          </span>
                          <span class="num w-16 shrink-0 text-right">
                            @if (entry.variation === null) {
                              <span class="muted">—</span>
                            } @else {
                              <span
                                [class]="entry.variation > 0 ? 'text-emerald-600 dark:text-emerald-400' : entry.variation < 0 ? 'text-red-600 dark:text-red-400' : 'muted'"
                              >
                                {{ entry.variation > 0 ? '+' : '' }}{{ entry.variation }}
                              </span>
                            }
                          </span>
                          <span class="ml-auto flex gap-1">
                            <button
                              type="button"
                              class="btn-icon"
                              (click)="openEdit(entry.inventory)"
                              aria-label="Modifier ce relevé"
                            >
                              <app-icon name="edit" [size]="14" />
                            </button>
                            <button
                              type="button"
                              class="btn-icon text-red-600 dark:text-red-400"
                              (click)="remove(entry.inventory)"
                              aria-label="Supprimer ce relevé"
                            >
                              <app-icon name="trash" [size]="14" />
                            </button>
                          </span>
                        </li>
                      }
                    </ul>
                  </td>
                </tr>
              }
            }
          </tbody>
        </table>
      </div>
    }

    <!-- Saisie d'un relevé -->
    @if (modalOpen()) {
      <app-modal
        [title]="editing() ? 'Modifier le relevé' : 'Nouveau relevé'"
        widthClass="max-w-lg"
        (closed)="closeModal()"
      >
      <form [formGroup]="form" class="space-y-4" (ngSubmit)="submit()">
        <div>
          <label class="label" for="inv-article">Article</label>
          <select
            id="inv-article"
            class="input"
            formControlName="articleId"
            [class.input-error]="invalid('articleId')"
          >
            <option [ngValue]="null" disabled>Choisir un article…</option>
            @for (a of articles(); track a.artId) {
              <option [ngValue]="a.artId">{{ a.artReference }} — {{ a.artName }}</option>
            }
          </select>
          <app-field-error
            [control]="form.controls.articleId"
            label="L'article"
            [submitted]="submitted()"
          />
        </div>

        <div>
          <label class="label" for="inv-stock">Quantité constatée</label>
          <input
            id="inv-stock"
            type="number"
            min="0"
            step="1"
            class="input"
            formControlName="invStock"
            [class.input-error]="invalid('invStock')"
            placeholder="0"
          />
          <app-field-error
            [control]="form.controls.invStock"
            label="La quantité"
            [submitted]="submitted()"
          />
          <p class="mt-1.5 text-[12px] muted">
            La quantité réellement présente en dépôt, telle que vous venez de la compter.
          </p>
        </div>

        @if (selectedComparison(); as cmp) {
          <div class="rounded-lg border border-ink-200 p-3 text-[12.5px] dark:border-ink-800">
            <p class="muted">
              Stock annoncé par l'API pour cet article :
              <span class="num font-semibold text-ink-900 dark:text-ink-100">{{ cmp.reported }}</span>
            </p>
            @if (cmp.drift !== null && cmp.drift !== 0) {
              <p class="mt-1" [class]="cmp.drift > 0 ? 'text-amber-700 dark:text-amber-400' : 'text-red-700 dark:text-red-400'">
                Votre comptage s'en écarte de {{ cmp.drift > 0 ? '+' : '' }}{{ cmp.drift }}.
              </p>
            }
          </div>
        }
      </form>

        <div footer class="flex justify-end gap-2">
          <button type="button" class="btn-secondary" (click)="closeModal()" [disabled]="saving()">
            Annuler
          </button>
          <button type="button" class="btn-primary" (click)="submit()" [disabled]="saving()">
            @if (saving()) {
              <app-icon name="refresh" [size]="15" class="animate-spin" />
            }
            {{ editing() ? 'Enregistrer' : 'Enregistrer le relevé' }}
          </button>
        </div>
      </app-modal>
    }
  `,
})
export class InventoryComponent implements OnInit {
  private readonly api = inject(InventoryService);
  private readonly articleApi = inject(ArticleService);
  private readonly fb = inject(FormBuilder);
  private readonly toast = inject(ToastService);
  private readonly confirm = inject(ConfirmService);
  private readonly route = inject(ActivatedRoute);

  protected readonly staleAfter = STALE_AFTER_DAYS;
  protected readonly statusLabels = STOCK_STATUS_LABELS;

  protected readonly inventories = signal<Inventory[]>([]);
  protected readonly articles = signal<Article[]>([]);
  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly modalOpen = signal(false);
  protected readonly editing = signal<Inventory | null>(null);
  protected readonly submitted = signal(false);
  protected readonly expanded = signal<number | null>(null);
  protected readonly search = signal('');
  protected readonly filter = signal<'all' | 'tracked' | 'never' | 'drift' | 'stale'>('all');

  protected readonly form = this.fb.group({
    articleId: [null as number | null, [Validators.required]],
    invStock: [0, [Validators.required, Validators.min(0)]],
  });

  /** Une ligne par article, avec sa lecture de stock et son historique. */
  private readonly allRows = computed(() => {
    const inventories = this.inventories();
    return this.articles().map((article) => {
      const own = inventories.filter((inv) => inv.article?.artId === article.artId);
      return {
        article,
        reading: readStock(article, inventories),
        history: withVariations(own),
      };
    });
  });

  protected readonly rows = computed(() => {
    const term = this.search().trim().toLowerCase();
    const mode = this.filter();

    return this.allRows().filter((row) => {
      if (term) {
        const haystack = `${row.article.artName} ${row.article.artReference}`.toLowerCase();
        if (!haystack.includes(term)) return false;
      }
      switch (mode) {
        case 'tracked':
          return row.history.length > 0;
        case 'never':
          return row.reading.status === 'jamais-inventorie';
        case 'drift':
          return row.reading.drift !== null && row.reading.drift !== 0;
        case 'stale':
          return row.reading.status === 'perime';
        default:
          return true;
      }
    });
  });

  protected readonly trackedCount = computed(
    () => this.allRows().filter((r) => r.history.length > 0).length,
  );
  protected readonly neverCount = computed(
    () => this.allRows().filter((r) => r.reading.status === 'jamais-inventorie').length,
  );
  protected readonly driftCount = computed(
    () => this.allRows().filter((r) => r.reading.drift !== null && r.reading.drift !== 0).length,
  );
  protected readonly staleCount = computed(
    () => this.allRows().filter((r) => r.reading.status === 'perime').length,
  );
  protected readonly undatedCount = computed(
    () => this.inventories().filter((inv) => !inv.invDate).length,
  );

  /**
   * Valeur du formulaire exposée en signal.
   *
   * Un `computed` qui lirait directement `form.controls.x.value` ne se
   * recalculerait jamais : les contrôles réactifs ne sont pas des signaux, et
   * rien ne notifierait la dépendance. On passe donc par `valueChanges`.
   */
  private readonly formValue = toSignal(this.form.valueChanges, {
    initialValue: this.form.getRawValue(),
  });

  /** Comparaison en direct pendant la saisie, pour repérer l'écart tout de suite. */
  protected readonly selectedComparison = computed(() => {
    const value = this.formValue();
    const articleId = Number(value?.articleId ?? this.form.controls.articleId.value ?? 0);
    if (!articleId) return null;
    const article = this.articles().find((a) => a.artId === articleId);
    if (!article) return null;
    const counted = Number(value?.invStock ?? 0);
    return { reported: article.artStock, drift: counted - article.artStock };
  });

  async ngOnInit(): Promise<void> {
    await this.load();

    // Arrivée depuis la fiche article : on déplie directement le bon historique.
    const focus = Number(this.route.snapshot.queryParamMap.get('article'));
    if (focus) this.expanded.set(focus);
  }

  private async load(): Promise<void> {
    this.loading.set(true);
    const [inventories, articles] = await Promise.all([
      firstValueFrom(this.api.list()).catch(() => [] as Inventory[]),
      firstValueFrom(this.articleApi.list()).catch(() => [] as Article[]),
    ]);
    this.inventories.set(inventories);
    this.articles.set(articles);
    this.loading.set(false);
  }

  protected setFilter(event: Event): void {
    this.filter.set((event.target as HTMLSelectElement).value as ReturnType<typeof this.filter>);
  }

  protected toggle(articleId: number): void {
    this.expanded.set(this.expanded() === articleId ? null : articleId);
  }

  protected badgeClass(reading: StockReading): string {
    const base =
      'inline-flex items-center rounded-full px-2 py-0.5 text-[11px] font-medium ';
    switch (reading.status) {
      case 'fiable':
        return base + 'bg-emerald-50 text-emerald-700 dark:bg-emerald-500/10 dark:text-emerald-400';
      case 'ecart':
        return base + 'bg-amber-50 text-amber-700 dark:bg-amber-500/10 dark:text-amber-400';
      case 'perime':
        return base + 'bg-red-50 text-red-700 dark:bg-red-500/10 dark:text-red-400';
      case 'date-manquante':
        return base + 'bg-amber-50 text-amber-700 dark:bg-amber-500/10 dark:text-amber-400';
      default:
        return base + 'bg-ink-100 text-ink-500 dark:bg-ink-800 dark:text-ink-400';
    }
  }

  protected invalid(name: 'articleId' | 'invStock'): boolean {
    const control = this.form.controls[name];
    return control.invalid && (control.touched || this.submitted());
  }

  protected openCreate(articleId?: number): void {
    this.editing.set(null);
    this.submitted.set(false);
    this.form.reset({ articleId: articleId ?? null, invStock: 0 });
    this.form.controls.articleId.enable();
    this.modalOpen.set(true);
  }

  protected openEdit(inventory: Inventory): void {
    this.editing.set(inventory);
    this.submitted.set(false);
    this.form.reset({
      articleId: inventory.article?.artId ?? null,
      invStock: inventory.invStock,
    });
    // Déplacer un relevé d'un article à l'autre n'a pas de sens : ce serait
    // réécrire l'histoire de deux articles à la fois.
    this.form.controls.articleId.disable();
    this.modalOpen.set(true);
  }

  protected closeModal(): void {
    this.modalOpen.set(false);
  }

  protected async submit(): Promise<void> {
    this.submitted.set(true);
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.toast.warning('Formulaire incomplet', 'Vérifiez les champs signalés.');
      return;
    }

    this.saving.set(true);
    const raw = this.form.getRawValue();
    const payload = {
      articleId: Number(raw.articleId),
      invStock: Number(raw.invStock ?? 0),
    };
    const current = this.editing();

    try {
      if (current) {
        await firstValueFrom(this.api.update(current.invId, payload));
        this.toast.success('Relevé modifié');
      } else {
        await firstValueFrom(this.api.create(payload));
        this.toast.success('Relevé enregistré');
      }
      this.closeModal();
      await this.load();
      this.expanded.set(payload.articleId);
    } catch {
      /* l'intercepteur a déjà signalé l'erreur */
    } finally {
      this.saving.set(false);
    }
  }

  protected async remove(inventory: Inventory): Promise<void> {
    const when = inventory.invDate
      ? new Date(inventory.invDate).toLocaleDateString('fr-FR')
      : 'sans date';

    const confirmed = await this.confirm.ask({
      title: 'Supprimer ce relevé ?',
      message: `Comptage de ${inventory.invStock} unité(s), ${when}. Un relevé est un constat daté : le supprimer retire une preuve de l'historique.`,
      confirmLabel: 'Supprimer',
      danger: true,
    });
    if (!confirmed) return;

    try {
      await firstValueFrom(this.api.delete(inventory.invId));
      this.toast.success('Relevé supprimé');
      await this.load();
    } catch {
      /* idem */
    }
  }
}
