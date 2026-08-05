import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { ArticleService, CategoryService, TvaService } from '../../core/api';
import { Article, Category, Tva } from '../../core/models/api.models';
import { ConfirmService } from '../../core/services/confirm.service';
import { ToastService } from '../../core/services/toast.service';
import { CapitalizePipe, EurPipe, RefPipe, TauxPctPipe } from '../../shared/pipes/format.pipes';
import { EmptyStateComponent } from '../../shared/ui/empty-state.component';
import { FieldErrorComponent } from '../../shared/ui/field-error.component';
import { IconComponent } from '../../shared/ui/icon.component';
import { ModalComponent } from '../../shared/ui/modal.component';
import { PageHeaderComponent } from '../../shared/ui/page-header.component';
import { SearchInputComponent } from '../../shared/ui/search-input.component';

type StockFilter = 'all' | 'in' | 'low' | 'out';

@Component({
  selector: 'app-article-list',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    PageHeaderComponent,
    SearchInputComponent,
    EmptyStateComponent,
    ModalComponent,
    IconComponent,
    FieldErrorComponent,
    CapitalizePipe,
    EurPipe,
    RefPipe,
    TauxPctPipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <app-page-header
      title="Articles"
      subtitle="Catalogue des climatiseurs, pompes à chaleur, chaudières et accessoires."
    >
      <button type="button" class="btn-secondary" (click)="load()">
        <app-icon name="refresh" [size]="16" /> Actualiser
      </button>
      <button type="button" class="btn-primary" (click)="openCreate()" [disabled]="tvas().length === 0">
        <app-icon name="plus" [size]="16" /> Nouvel article
      </button>
    </app-page-header>

    <!-- Indicateurs -->
    <div class="mb-5 grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
      <div class="card card-pad">
        <p class="text-[13px] muted">Articles au catalogue</p>
        <p class="kpi-value mt-1">{{ items().length }}</p>
      </div>
      <div class="card card-pad">
        <p class="text-[13px] muted">Valeur du stock (TTC)</p>
        <p class="kpi-value mt-1">{{ stockValue() | eur }}</p>
      </div>
      <div class="card card-pad">
        <p class="text-[13px] muted">Stock faible (&lt; 5)</p>
        <p class="kpi-value mt-1 text-amber-600 dark:text-amber-400">{{ lowStockCount() }}</p>
      </div>
      <div class="card card-pad">
        <p class="text-[13px] muted">Rupture de stock</p>
        <p class="kpi-value mt-1 text-red-600 dark:text-red-400">{{ outOfStockCount() }}</p>
      </div>
    </div>

    <div class="card">
      <div class="flex flex-wrap items-center gap-3 border-b border-ink-200 p-4 dark:border-ink-800">
        <app-search-input [value]="search()" (valueChange)="search.set($event)" placeholder="Référence, nom, description…" />

        <select class="input w-auto" [value]="categoryFilter()" (change)="onCategoryFilter($event)">
          <option value="">Toutes les catégories</option>
          @for (c of categories(); track c.catId) {
            <option [value]="c.catId">{{ c.catName | capitalize }}</option>
          }
        </select>

        <select class="input w-auto" [value]="stockFilter()" (change)="onStockFilter($event)">
          <option value="all">Tous les stocks</option>
          <option value="in">En stock</option>
          <option value="low">Stock faible</option>
          <option value="out">Rupture</option>
        </select>

        <p class="ml-auto text-[13px] muted">{{ filtered().length }} / {{ items().length }} articles</p>
      </div>

      @if (loading()) {
        <div class="space-y-3 p-5">
          @for (i of [1, 2, 3, 4]; track i) {
            <div class="skeleton h-14 w-full"></div>
          }
        </div>
      } @else if (filtered().length === 0) {
        <app-empty-state icon="package" title="Aucun article" message="Créez votre premier article pour alimenter vos devis.">
          <button type="button" class="btn-primary" (click)="openCreate()" [disabled]="tvas().length === 0">
            <app-icon name="plus" [size]="16" /> Nouvel article
          </button>
        </app-empty-state>
      } @else {
        <div class="table-wrap">
          <table class="table">
            <thead>
              <tr>
                <th class="min-w-[240px]">Article</th>
                <th class="w-56">Catégories</th>
                <th class="w-28 whitespace-nowrap text-right">Prix HT</th>
                <th class="w-20 whitespace-nowrap text-right">TVA</th>
                <th class="w-28 whitespace-nowrap text-right">Prix TTC</th>
                <th class="w-24 text-center">Stock</th>
                <th class="w-28 text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              @for (a of filtered(); track a.artId) {
                <tr>
                  <td>
                    <div class="flex items-start gap-3">
                      <span
                        class="mt-0.5 flex h-9 w-9 shrink-0 items-center justify-center rounded-lg"
                        [class]="iconTone(a)"
                      >
                        <app-icon [name]="iconFor(a)" [size]="17" />
                      </span>
                      <div class="min-w-0">
                        <p class="truncate font-medium">{{ a.artName | capitalize }}</p>
                        <p class="truncate font-mono text-[12px] muted">{{ a.artReference | ref }}</p>
                      </div>
                    </div>
                  </td>
                  <td>
                    @if (a.categories?.length) {
                      <div class="flex flex-wrap gap-1.5">
                        @for (c of a.categories; track c.catId) {
                          <span class="badge-neutral">{{ c.catName | capitalize }}</span>
                        }
                      </div>
                    } @else {
                      <span class="text-[13px] muted">—</span>
                    }
                  </td>
                  <td class="num whitespace-nowrap text-right">{{ a.artPriceExcludeTaxes | eur }}</td>
                  <td class="num whitespace-nowrap text-right muted">{{ a.tva?.tvaTaux | tauxPct }}</td>
                  <td class="num whitespace-nowrap text-right font-semibold">{{ a.artPriceTTC | eur }}</td>
                  <td class="text-center">
                    <span [class]="stockBadge(a.artStock)">{{ a.artStock }}</span>
                  </td>
                  <td>
                    <div class="flex justify-end gap-1">
                      <button type="button" class="btn-icon" (click)="openEdit(a)" title="Modifier">
                        <app-icon name="edit" [size]="16" />
                      </button>
                      <button
                        type="button"
                        class="btn-icon hover:bg-red-50 hover:text-red-600 dark:hover:bg-red-500/10"
                        (click)="remove(a)"
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

    @if (modalOpen()) {
      <app-modal
        [title]="editing() ? 'Modifier l’article' : 'Nouvel article'"
        widthClass="max-w-3xl"
        (closed)="closeModal()"
      >
        <form [formGroup]="form" class="space-y-5" id="article-form" (ngSubmit)="submit()">
          <div class="grid gap-4 sm:grid-cols-6">
            <div class="sm:col-span-2">
              <label class="label" for="art-ref">Référence interne</label>
              <input
                id="art-ref"
                type="text"
                class="input font-mono text-[13px]"
                [class.input-error]="invalid('artReference')"
                formControlName="artReference"
                placeholder="CLIM-MS-3500"
              />
              <app-field-error [control]="form.controls.artReference" label="La référence" [submitted]="submitted()" />
            </div>

            <div class="sm:col-span-4">
              <label class="label" for="art-name">Désignation</label>
              <input
                id="art-name"
                type="text"
                class="input"
                [class.input-error]="invalid('artName')"
                formControlName="artName"
                placeholder="Climatiseur mural réversible 3,5 kW"
              />
              <app-field-error [control]="form.controls.artName" label="La désignation" [submitted]="submitted()" />
            </div>

            <div class="sm:col-span-6">
              <label class="label" for="art-desc">Description</label>
              <textarea
                id="art-desc"
                class="input"
                [class.input-error]="invalid('artDescription')"
                formControlName="artDescription"
                placeholder="Unité intérieure + extérieure, SCOP 4,6, classe A++, fluide R32…"
              ></textarea>
              <app-field-error [control]="form.controls.artDescription" label="La description" [submitted]="submitted()" />
            </div>

            <div class="sm:col-span-2">
              <label class="label" for="art-price">Prix HT</label>
              <div class="relative">
                <input
                  id="art-price"
                  type="number"
                  step="0.01"
                  min="0.01"
                  class="input pr-8"
                  [class.input-error]="invalid('artPriceExcludeTaxes')"
                  formControlName="artPriceExcludeTaxes"
                  placeholder="899.00"
                />
                <span class="pointer-events-none absolute right-3 top-1/2 -translate-y-1/2 text-sm muted">€</span>
              </div>
              <app-field-error
                [control]="form.controls.artPriceExcludeTaxes"
                label="Le prix HT"
                [submitted]="submitted()"
              />
            </div>

            <div class="sm:col-span-2">
              <label class="label" for="art-tva">Taux de TVA</label>
              <select
                id="art-tva"
                class="input"
                [class.input-error]="invalid('tvaId')"
                formControlName="tvaId"
              >
                <option [ngValue]="null" disabled>— Sélectionner —</option>
                @for (t of tvas(); track t.tvaId) {
                  <option [ngValue]="t.tvaId">
                    {{ t.tvaName | capitalize }} ({{ t.tvaTaux | tauxPct }})
                  </option>
                }
              </select>
              <app-field-error [control]="form.controls.tvaId" label="Le taux de TVA" [submitted]="submitted()" />
            </div>

            <!--
              Le stock n'est plus un champ saisissable : le serveur le calcule
              à partir des références fournisseur et fabricant. Laisser une
              case ici laisserait croire qu'on peut le corriger à la main.
            -->
            <div class="sm:col-span-2">
              <label class="label">Stock</label>
              <div
                class="flex h-[38px] items-center gap-2 rounded-lg border border-dashed border-ink-200 px-3 dark:border-ink-700"
              >
                @if (editing(); as current) {
                  <span class="num text-[14px] font-semibold">{{ current.artStock }}</span>
                  <a
                    [routerLink]="['/inventaire']"
                    [queryParams]="{ article: current.artId }"
                    class="ml-auto text-[12px] font-medium text-brand-600 hover:underline dark:text-brand-400"
                    (click)="closeModal()"
                    >Relevés</a
                  >
                } @else {
                  <span class="text-[12.5px] muted">Calculé par les références</span>
                }
              </div>
            </div>

            <div class="sm:col-span-6">
              <p class="rounded-lg bg-brand-50 px-3.5 py-2.5 text-[13px] text-brand-800 dark:bg-brand-500/10 dark:text-brand-300">
                Prix TTC calculé : <span class="font-semibold num">{{ pricePreview() | eur }}</span>
              </p>
            </div>
          </div>

          <div class="border-t border-ink-200 pt-5 dark:border-ink-800">
            <h3 class="panel-title">Catégories</h3>
            <p class="mb-3 mt-1 text-[13px] muted">Un article peut appartenir à plusieurs catégories.</p>
            @if (categories().length === 0) {
              <p class="text-[13px] muted">Aucune catégorie disponible.</p>
            } @else {
              <div
                class="grid max-h-48 gap-1.5 overflow-y-auto rounded-lg border border-ink-200 p-3 dark:border-ink-700 sm:grid-cols-2"
              >
                @for (c of categories(); track c.catId) {
                  <label class="flex cursor-pointer items-center gap-2.5 text-sm">
                    <input
                      type="checkbox"
                      class="h-4 w-4 rounded border-ink-300 text-brand-600 focus:ring-brand-500 dark:border-ink-600 dark:bg-ink-800"
                      [checked]="selectedCategories().includes(c.catId)"
                      (change)="toggleCategory(c.catId)"
                    />
                    <span>{{ c.catName | capitalize }}</span>
                  </label>
                }
              </div>
            }
          </div>

          @if (editing()) {
            <p class="rounded-lg bg-ink-50 px-3.5 py-2.5 text-[12.5px] muted dark:bg-ink-950/40">
              Les références fournisseurs et fabricants se gèrent depuis l'écran
              <span class="font-medium">Références</span> (l'API de modification d'un article ne les
              accepte pas).
            </p>
          }
        </form>

        <div footer>
          <button type="button" class="btn-secondary" (click)="closeModal()">Annuler</button>
          <button type="submit" form="article-form" class="btn-primary" [disabled]="saving()">
            <app-icon name="save" [size]="16" />
            {{ editing() ? 'Enregistrer' : 'Créer' }}
          </button>
        </div>
      </app-modal>
    }
  `,
})
export class ArticleListComponent implements OnInit {
  private readonly api = inject(ArticleService);
  private readonly categoryApi = inject(CategoryService);
  private readonly tvaApi = inject(TvaService);
  private readonly fb = inject(FormBuilder);
  private readonly toast = inject(ToastService);
  private readonly confirm = inject(ConfirmService);

  protected readonly items = signal<Article[]>([]);
  protected readonly categories = signal<Category[]>([]);
  protected readonly tvas = signal<Tva[]>([]);
  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly search = signal('');
  protected readonly categoryFilter = signal('');
  protected readonly stockFilter = signal<StockFilter>('all');
  protected readonly modalOpen = signal(false);
  protected readonly editing = signal<Article | null>(null);
  protected readonly submitted = signal(false);
  protected readonly selectedCategories = signal<number[]>([]);

  protected readonly form = this.fb.group({
    artReference: ['', [Validators.required, Validators.maxLength(60)]],
    artName: ['', [Validators.required, Validators.maxLength(120)]],
    artDescription: ['', [Validators.required, Validators.maxLength(1000)]],
    artPriceExcludeTaxes: [null as number | null, [Validators.required, Validators.min(0.01)]],
    tvaId: [null as number | null, [Validators.required]],
  });

  protected readonly filtered = computed(() => {
    const q = this.search().trim().toLowerCase();
    const cat = this.categoryFilter();
    const stock = this.stockFilter();

    return this.items().filter((a) => {
      if (q) {
        const haystack = [a.artReference, a.artName, a.artDescription]
          .filter(Boolean)
          .join(' ')
          .toLowerCase();
        if (!haystack.includes(q)) return false;
      }
      if (cat && !(a.categories ?? []).some((c) => String(c.catId) === cat)) return false;
      if (stock === 'in' && a.artStock <= 0) return false;
      if (stock === 'low' && (a.artStock <= 0 || a.artStock >= 5)) return false;
      if (stock === 'out' && a.artStock > 0) return false;
      return true;
    });
  });

  protected readonly stockValue = computed(() =>
    this.items().reduce((sum, a) => sum + Number(a.artPriceTTC ?? 0) * Number(a.artStock ?? 0), 0),
  );

  protected readonly lowStockCount = computed(
    () => this.items().filter((a) => a.artStock > 0 && a.artStock < 5).length,
  );

  protected readonly outOfStockCount = computed(
    () => this.items().filter((a) => a.artStock <= 0).length,
  );

  ngOnInit(): void {
    this.load();
  }

  protected invalid(name: string): boolean {
    const c = this.form.get(name);
    return !!c && c.invalid && (c.touched || c.dirty || this.submitted());
  }

  onCategoryFilter(event: Event): void {
    this.categoryFilter.set((event.target as HTMLSelectElement).value);
  }

  onStockFilter(event: Event): void {
    this.stockFilter.set((event.target as HTMLSelectElement).value as StockFilter);
  }

  /** Icône selon la nature de l'article, déduite du nom et des catégories. */
  iconFor(a: Article): string {
    const text = `${a.artName} ${(a.categories ?? []).map((c) => c.catName).join(' ')}`.toLowerCase();
    if (/(clim|split|réversible|reversible|froid)/.test(text)) return 'snowflake';
    if (/(chauff|radiateur|chaudi|pac|pompe|poêle|poele)/.test(text)) return 'flame';
    return 'package';
  }

  iconTone(a: Article): string {
    const icon = this.iconFor(a);
    if (icon === 'snowflake')
      return 'bg-brand-50 text-brand-600 dark:bg-brand-500/10 dark:text-brand-400';
    if (icon === 'flame') return 'bg-heat-50 text-heat-600 dark:bg-heat-500/10 dark:text-heat-400';
    return 'bg-ink-100 text-ink-500 dark:bg-ink-800 dark:text-ink-400';
  }

  stockBadge(stock: number): string {
    if (stock <= 0) return 'badge-danger num';
    if (stock < 5) return 'badge-warn num';
    return 'badge-success num';
  }

  /** Aperçu du prix TTC pendant la saisie. */
  pricePreview(): number {
    const ht = Number(this.form.controls.artPriceExcludeTaxes.value ?? 0);
    const tvaId = this.form.controls.tvaId.value;
    const tva = this.tvas().find((t) => t.tvaId === Number(tvaId));
    const rate = normalizeRate(tva?.tvaTaux);
    return Math.round(ht * (1 + rate) * 100) / 100;
  }

  toggleCategory(id: number): void {
    this.selectedCategories.update((list) =>
      list.includes(id) ? list.filter((x) => x !== id) : [...list, id],
    );
  }

  async load(): Promise<void> {
    this.loading.set(true);
    try {
      const [articles, categories, tvas] = await Promise.all([
        firstValueFrom(this.api.list()),
        firstValueFrom(this.categoryApi.list()).catch(() => [] as Category[]),
        firstValueFrom(this.tvaApi.list()).catch(() => [] as Tva[]),
      ]);
      this.items.set(articles);
      this.categories.set(flattenCategories(categories));
      this.tvas.set(tvas);
    } catch {
      this.items.set([]);
    } finally {
      this.loading.set(false);
    }
  }

  openCreate(): void {
    this.editing.set(null);
    this.submitted.set(false);
    this.selectedCategories.set([]);
    this.form.reset({
      artReference: '',
      artName: '',
      artDescription: '',
      artPriceExcludeTaxes: null,
      tvaId: this.tvas()[0]?.tvaId ?? null,
    });
    this.modalOpen.set(true);
  }

  openEdit(article: Article): void {
    this.editing.set(article);
    this.submitted.set(false);
    this.selectedCategories.set((article.categories ?? []).map((c) => c.catId));
    this.form.reset({
      artReference: article.artReference,
      artName: article.artName,
      artDescription: article.artDescription,
      artPriceExcludeTaxes: Number(article.artPriceExcludeTaxes),
      tvaId: article.tva?.tvaId ?? null,
    });
    this.modalOpen.set(true);
  }

  closeModal(): void {
    this.modalOpen.set(false);
  }

  async submit(): Promise<void> {
    this.submitted.set(true);
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.toast.warning('Formulaire incomplet', 'Vérifiez les champs signalés en rouge.');
      return;
    }

    this.saving.set(true);
    const raw = this.form.getRawValue();
    const base = {
      artReference: (raw.artReference ?? '').trim(),
      artName: (raw.artName ?? '').trim(),
      artDescription: (raw.artDescription ?? '').trim(),
      artPriceExcludeTaxes: Number(raw.artPriceExcludeTaxes),
      tvaId: Number(raw.tvaId),
      categoryIds: this.selectedCategories(),
    };
    const current = this.editing();

    try {
      if (current) {
        // `PUT /article/{id}` attend un ArticleUpdateDTO (sans `suppliers`).
        await firstValueFrom(
          this.api.update(current.artId, {
            ...base,
            // `ArticleService.update` déréférence `invIds` sans contrôle :
            // omettre le champ provoque une NullPointerException côté serveur.
            // On transmet donc toujours un tableau. Les relevés se gèrent
            // depuis l'écran d'inventaire, pas d'ici.
            invIds: [],
          }),
        );
        this.toast.success('Article modifié', base.artName);
      } else {
        await firstValueFrom(this.api.create({ ...base, suppliers: [] }));
        this.toast.success('Article créé', base.artName);
      }
      this.closeModal();
      await this.load();
    } catch {
      /* déjà notifié */
    } finally {
      this.saving.set(false);
    }
  }

  async remove(article: Article): Promise<void> {
    const ok = await this.confirm.askDelete(`l'article « ${article.artName} »`);
    if (!ok) return;
    try {
      await firstValueFrom(this.api.delete(article.artId));
      this.toast.success('Article supprimé');
      await this.load();
    } catch {
      /* déjà notifié */
    }
  }
}

/** L'API renvoie un arbre : on l'aplatit pour les listes de sélection. */
function flattenCategories(nodes: Category[]): Category[] {
  const out = new Map<number, Category>();
  const walk = (list: Category[] | null | undefined) => {
    for (const n of list ?? []) {
      if (!out.has(n.catId)) out.set(n.catId, n);
      walk(n.children);
    }
  };
  walk(nodes);
  return [...out.values()].sort((a, b) => a.catName.localeCompare(b.catName, 'fr'));
}

/** Accepte un taux exprimé en fraction (0.2) ou en points (20). */
function normalizeRate(taux: number | null | undefined): number {
  const n = Number(taux ?? 0);
  if (!Number.isFinite(n)) return 0;
  return n > 1 ? n / 100 : n;
}
