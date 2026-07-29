import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import {
  ArticleService,
  MakerReferenceService,
  MakerService,
  SupplierReferenceService,
  SupplierService,
} from '../../core/api';
import {
  Article,
  Maker,
  MakerReference,
  Supplier,
  SupplierReference,
} from '../../core/models/api.models';
import { ConfirmService } from '../../core/services/confirm.service';
import { ToastService } from '../../core/services/toast.service';
import { CapitalizePipe, EurPipe, RefPipe } from '../../shared/pipes/format.pipes';
import { EmptyStateComponent } from '../../shared/ui/empty-state.component';
import { FieldErrorComponent } from '../../shared/ui/field-error.component';
import { IconComponent } from '../../shared/ui/icon.component';
import { ModalComponent } from '../../shared/ui/modal.component';
import { PageHeaderComponent } from '../../shared/ui/page-header.component';
import { SearchInputComponent } from '../../shared/ui/search-input.component';

type Tab = 'supplier' | 'maker';

/**
 * Gestion des associations Article ↔ Fournisseur et Article ↔ Fabricant.
 *
 * Ces deux entités portent une clé composite et des attributs propres
 * (référence, stock, prix) : elles justifient un écran dédié.
 */
@Component({
  selector: 'app-reference-list',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    PageHeaderComponent,
    SearchInputComponent,
    EmptyStateComponent,
    ModalComponent,
    IconComponent,
    FieldErrorComponent,
    CapitalizePipe,
    EurPipe,
    RefPipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <app-page-header
      title="Références"
      subtitle="Associez vos articles aux fournisseurs (prix d'achat) et aux fabricants (référence constructeur)."
    >
      <button type="button" class="btn-secondary" (click)="load()">
        <app-icon name="refresh" [size]="16" /> Actualiser
      </button>
      <button type="button" class="btn-primary" (click)="openCreate()">
        <app-icon name="plus" [size]="16" />
        {{ tab() === 'supplier' ? 'Nouvelle réf. fournisseur' : 'Nouvelle réf. fabricant' }}
      </button>
    </app-page-header>

    <!-- Onglets -->
    <div class="mb-5 inline-flex rounded-lg border border-ink-200 bg-white p-1 dark:border-ink-700 dark:bg-ink-900">
      <button
        type="button"
        class="rounded-md px-3.5 py-1.5 text-sm font-medium transition-colors"
        [class]="tab() === 'supplier' ? activeTab : inactiveTab"
        (click)="tab.set('supplier')"
      >
        <span class="inline-flex items-center gap-2">
          <app-icon name="truck" [size]="15" /> Fournisseurs
          <span class="badge-neutral num">{{ supplierRefs().length }}</span>
        </span>
      </button>
      <button
        type="button"
        class="rounded-md px-3.5 py-1.5 text-sm font-medium transition-colors"
        [class]="tab() === 'maker' ? activeTab : inactiveTab"
        (click)="tab.set('maker')"
      >
        <span class="inline-flex items-center gap-2">
          <app-icon name="factory" [size]="15" /> Fabricants
          <span class="badge-neutral num">{{ makerRefs().length }}</span>
        </span>
      </button>
    </div>

    <div class="card">
      <div class="flex flex-wrap items-center justify-between gap-3 border-b border-ink-200 p-4 dark:border-ink-800">
        <app-search-input [value]="search()" (valueChange)="search.set($event)" placeholder="Article, référence, partenaire…" />
      </div>

      @if (loading()) {
        <div class="space-y-3 p-5">
          @for (i of [1, 2, 3]; track i) {
            <div class="skeleton h-12 w-full"></div>
          }
        </div>
      } @else if (tab() === 'supplier') {
        @if (filteredSupplierRefs().length === 0) {
          <app-empty-state
            icon="truck"
            title="Aucune référence fournisseur"
            message="Reliez un article à un fournisseur pour suivre son prix d'achat et son stock."
          />
        } @else {
          <div class="table-wrap">
            <table class="table">
              <thead>
                <tr>
                  <th>Article</th>
                  <th class="w-48">Fournisseur</th>
                  <th class="w-48">Réf. fournisseur</th>
                  <th class="w-32 text-right">Prix d'achat</th>
                  <th class="w-24 text-center">Stock</th>
                  <th class="w-28 text-right">Actions</th>
                </tr>
              </thead>
              <tbody>
                @for (r of filteredSupplierRefs(); track supplierKey(r)) {
                  <tr>
                    <td>
                      <p class="font-medium">{{ r.article?.artName | capitalize }}</p>
                      <p class="font-mono text-[12px] muted">{{ r.article?.artReference | ref }}</p>
                    </td>
                    <td><span class="badge-brand">{{ r.supplier?.splName | capitalize }}</span></td>
                    <td class="font-mono text-[12.5px]">{{ r.splRefReference | ref }}</td>
                    <td class="num text-right font-semibold">{{ r.supplierPrice | eur }}</td>
                    <td class="num text-center">{{ r.splRefStock }}</td>
                    <td>
                      <div class="flex justify-end gap-1">
                        <button type="button" class="btn-icon" (click)="openEditSupplier(r)" title="Modifier">
                          <app-icon name="edit" [size]="16" />
                        </button>
                        <button
                          type="button"
                          class="btn-icon hover:bg-red-50 hover:text-red-600 dark:hover:bg-red-500/10"
                          (click)="removeSupplier(r)"
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
      } @else {
        @if (filteredMakerRefs().length === 0) {
          <app-empty-state
            icon="factory"
            title="Aucune référence fabricant"
            message="Reliez un article à son fabricant pour conserver la référence constructeur."
          />
        } @else {
          <div class="table-wrap">
            <table class="table">
              <thead>
                <tr>
                  <th>Article</th>
                  <th class="w-48">Fabricant</th>
                  <th class="w-48">Réf. constructeur</th>
                  <th class="w-32 text-right">Prix de vente</th>
                  <th class="w-24 text-center">Stock</th>
                  <th class="w-28 text-right">Actions</th>
                </tr>
              </thead>
              <tbody>
                @for (r of filteredMakerRefs(); track makerKey(r)) {
                  <tr>
                    <td>
                      <p class="font-medium">{{ r.article?.artName | capitalize }}</p>
                      <p class="font-mono text-[12px] muted">{{ r.article?.artReference | ref }}</p>
                    </td>
                    <td><span class="badge-heat">{{ r.maker?.mkrName | capitalize }}</span></td>
                    <td class="font-mono text-[12.5px]">{{ r.reference | ref }}</td>
                    <td class="num text-right font-semibold">{{ r.artMkrSellPrice | eur }}</td>
                    <td class="num text-center">{{ r.artMkrStock }}</td>
                    <td>
                      <div class="flex justify-end gap-1">
                        <button type="button" class="btn-icon" (click)="openEditMaker(r)" title="Modifier">
                          <app-icon name="edit" [size]="16" />
                        </button>
                        <button
                          type="button"
                          class="btn-icon hover:bg-red-50 hover:text-red-600 dark:hover:bg-red-500/10"
                          (click)="removeMaker(r)"
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
      }
    </div>

    @if (modalOpen()) {
      <app-modal [title]="modalTitle()" widthClass="max-w-xl" (closed)="closeModal()">
        <form [formGroup]="form" class="space-y-4" id="ref-form" (ngSubmit)="submit()">
          <div class="grid gap-4 sm:grid-cols-2">
            <div class="sm:col-span-2">
              <label class="label" for="ref-article">Article</label>
              <select
                id="ref-article"
                class="input"
                [class.input-error]="invalid('articleId')"
                formControlName="articleId"
              >
                <option [ngValue]="null" disabled>— Sélectionner un article —</option>
                @for (a of articles(); track a.artId) {
                  <option [ngValue]="a.artId">
                    {{ a.artName | capitalize }} ({{ a.artReference | ref }})
                  </option>
                }
              </select>
              <app-field-error [control]="form.controls.articleId" label="L’article" [submitted]="submitted()" />
            </div>

            <div class="sm:col-span-2">
              <label class="label" for="ref-partner">
                {{ tab() === 'supplier' ? 'Fournisseur' : 'Fabricant' }}
              </label>
              <select
                id="ref-partner"
                class="input"
                [class.input-error]="invalid('partnerId')"
                formControlName="partnerId"
              >
                <option [ngValue]="null" disabled>— Sélectionner —</option>
                @if (tab() === 'supplier') {
                  @for (s of suppliers(); track s.splId) {
                    <option [ngValue]="s.splId">{{ s.splName | capitalize }}</option>
                  }
                } @else {
                  @for (m of makers(); track m.mkrId) {
                    <option [ngValue]="m.mkrId">{{ m.mkrName | capitalize }}</option>
                  }
                }
              </select>
              <app-field-error [control]="form.controls.partnerId" label="Le partenaire" [submitted]="submitted()" />
            </div>

            <div class="sm:col-span-2">
              <label class="label" for="ref-reference">
                {{ tab() === 'supplier' ? 'Référence fournisseur' : 'Référence constructeur' }}
              </label>
              <input
                id="ref-reference"
                type="text"
                class="input font-mono text-[13px]"
                [class.input-error]="invalid('reference')"
                formControlName="reference"
                placeholder="MSZ-AP35VGK"
              />
              <app-field-error [control]="form.controls.reference" label="La référence" [submitted]="submitted()" />
            </div>

            <div>
              <label class="label" for="ref-price">
                {{ tab() === 'supplier' ? "Prix d'achat HT" : 'Prix de vente conseillé' }}
              </label>
              <div class="relative">
                <input
                  id="ref-price"
                  type="number"
                  step="0.01"
                  min="0"
                  class="input pr-8"
                  [class.input-error]="invalid('price')"
                  formControlName="price"
                  placeholder="0.00"
                />
                <span class="pointer-events-none absolute right-3 top-1/2 -translate-y-1/2 text-sm muted">€</span>
              </div>
              <app-field-error [control]="form.controls.price" label="Le prix" [submitted]="submitted()" />
            </div>

            <div>
              <label class="label" for="ref-stock">Stock</label>
              <input
                id="ref-stock"
                type="number"
                min="0"
                step="1"
                class="input"
                [class.input-error]="invalid('stock')"
                formControlName="stock"
                placeholder="0"
              />
              <app-field-error [control]="form.controls.stock" label="Le stock" [submitted]="submitted()" />
            </div>
          </div>

          @if (editingKey()) {
            <p class="rounded-lg bg-ink-50 px-3.5 py-2.5 text-[12.5px] muted dark:bg-ink-950/40">
              L'article et le partenaire forment la clé composite : ils ne sont pas modifiables.
              Supprimez puis recréez la référence pour les changer.
            </p>
          }
        </form>

        <div footer>
          <button type="button" class="btn-secondary" (click)="closeModal()">Annuler</button>
          <button type="submit" form="ref-form" class="btn-primary" [disabled]="saving()">
            <app-icon name="save" [size]="16" />
            {{ editingKey() ? 'Enregistrer' : 'Créer' }}
          </button>
        </div>
      </app-modal>
    }
  `,
})
export class ReferenceListComponent implements OnInit {
  private readonly supplierRefApi = inject(SupplierReferenceService);
  private readonly makerRefApi = inject(MakerReferenceService);
  private readonly articleApi = inject(ArticleService);
  private readonly supplierApi = inject(SupplierService);
  private readonly makerApi = inject(MakerService);
  private readonly fb = inject(FormBuilder);
  private readonly toast = inject(ToastService);
  private readonly confirm = inject(ConfirmService);

  protected readonly activeTab = 'bg-brand-600 text-white shadow-sm';
  protected readonly inactiveTab =
    'text-ink-600 hover:text-ink-900 dark:text-ink-400 dark:hover:text-white';

  protected readonly tab = signal<Tab>('supplier');
  protected readonly supplierRefs = signal<SupplierReference[]>([]);
  protected readonly makerRefs = signal<MakerReference[]>([]);
  protected readonly articles = signal<Article[]>([]);
  protected readonly suppliers = signal<Supplier[]>([]);
  protected readonly makers = signal<Maker[]>([]);
  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly search = signal('');
  protected readonly modalOpen = signal(false);
  protected readonly submitted = signal(false);
  /** `null` en création ; `[articleId, partnerId]` en modification. */
  protected readonly editingKey = signal<[number, number] | null>(null);

  protected readonly form = this.fb.group({
    articleId: [null as number | null, [Validators.required]],
    partnerId: [null as number | null, [Validators.required]],
    reference: ['', [Validators.required, Validators.maxLength(60)]],
    price: [0 as number | null, [Validators.required, Validators.min(0)]],
    stock: [0, [Validators.required, Validators.min(0)]],
  });

  protected readonly filteredSupplierRefs = computed(() => {
    const q = this.search().trim().toLowerCase();
    const list = this.supplierRefs();
    if (!q) return list;
    return list.filter((r) =>
      [r.article?.artName, r.article?.artReference, r.supplier?.splName, r.splRefReference]
        .filter(Boolean)
        .join(' ')
        .toLowerCase()
        .includes(q),
    );
  });

  protected readonly filteredMakerRefs = computed(() => {
    const q = this.search().trim().toLowerCase();
    const list = this.makerRefs();
    if (!q) return list;
    return list.filter((r) =>
      [r.article?.artName, r.article?.artReference, r.maker?.mkrName, r.reference]
        .filter(Boolean)
        .join(' ')
        .toLowerCase()
        .includes(q),
    );
  });

  ngOnInit(): void {
    this.load();
  }

  protected invalid(name: string): boolean {
    const c = this.form.get(name);
    return !!c && c.invalid && (c.touched || c.dirty || this.submitted());
  }

  supplierKey(r: SupplierReference): string {
    return `${r.article?.artId}-${r.supplier?.splId}`;
  }

  makerKey(r: MakerReference): string {
    return `${r.article?.artId}-${r.maker?.mkrId}`;
  }

  modalTitle(): string {
    const editing = !!this.editingKey();
    if (this.tab() === 'supplier') {
      return editing ? 'Modifier la référence fournisseur' : 'Nouvelle référence fournisseur';
    }
    return editing ? 'Modifier la référence fabricant' : 'Nouvelle référence fabricant';
  }

  async load(): Promise<void> {
    this.loading.set(true);
    try {
      const [sRefs, mRefs, articles, suppliers, makers] = await Promise.all([
        firstValueFrom(this.supplierRefApi.list()).catch(() => [] as SupplierReference[]),
        firstValueFrom(this.makerRefApi.list()).catch(() => [] as MakerReference[]),
        firstValueFrom(this.articleApi.list()).catch(() => [] as Article[]),
        firstValueFrom(this.supplierApi.list()).catch(() => [] as Supplier[]),
        firstValueFrom(this.makerApi.list()).catch(() => [] as Maker[]),
      ]);
      this.supplierRefs.set(sRefs);
      this.makerRefs.set(mRefs);
      this.articles.set(articles);
      this.suppliers.set(suppliers);
      this.makers.set(makers);
    } finally {
      this.loading.set(false);
    }
  }

  openCreate(): void {
    this.editingKey.set(null);
    this.submitted.set(false);
    this.form.reset({ articleId: null, partnerId: null, reference: '', price: 0, stock: 0 });
    this.form.controls.articleId.enable();
    this.form.controls.partnerId.enable();
    this.modalOpen.set(true);
  }

  openEditSupplier(r: SupplierReference): void {
    this.tab.set('supplier');
    this.editingKey.set([r.article!.artId, r.supplier!.splId]);
    this.submitted.set(false);
    this.form.reset({
      articleId: r.article?.artId ?? null,
      partnerId: r.supplier?.splId ?? null,
      reference: r.splRefReference,
      price: Number(r.supplierPrice ?? 0),
      stock: r.splRefStock ?? 0,
    });
    this.form.controls.articleId.disable();
    this.form.controls.partnerId.disable();
    this.modalOpen.set(true);
  }

  openEditMaker(r: MakerReference): void {
    this.tab.set('maker');
    this.editingKey.set([r.article!.artId, r.maker!.mkrId]);
    this.submitted.set(false);
    this.form.reset({
      articleId: r.article?.artId ?? null,
      partnerId: r.maker?.mkrId ?? null,
      reference: r.reference,
      price: Number(r.artMkrSellPrice ?? 0),
      stock: r.artMkrStock ?? 0,
    });
    this.form.controls.articleId.disable();
    this.form.controls.partnerId.disable();
    this.modalOpen.set(true);
  }

  closeModal(): void {
    this.form.controls.articleId.enable();
    this.form.controls.partnerId.enable();
    this.modalOpen.set(false);
  }

  async submit(): Promise<void> {
    this.submitted.set(true);
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    const raw = this.form.getRawValue();
    const articleId = Number(raw.articleId);
    const partnerId = Number(raw.partnerId);
    const reference = (raw.reference ?? '').trim();
    const price = Number(raw.price ?? 0);
    const stock = Number(raw.stock ?? 0);
    const key = this.editingKey();

    try {
      if (this.tab() === 'supplier') {
        if (key) {
          await firstValueFrom(
            this.supplierRefApi.update(key[0], key[1], {
              splRefReference: reference,
              splRefSellPrice: price,
              splRefStock: stock,
            }),
          );
          this.toast.success('Référence fournisseur modifiée');
        } else {
          await firstValueFrom(
            this.supplierRefApi.create({
              articleId,
              supplierId: partnerId,
              splRefReference: reference,
              splRefSellPrice: price,
              splRefStock: stock,
            }),
          );
          this.toast.success('Référence fournisseur créée');
        }
      } else {
        if (key) {
          await firstValueFrom(
            this.makerRefApi.update(key[0], key[1], {
              reference,
              artMkrStock: stock,
              artMkrSellPrice: price,
            }),
          );
          this.toast.success('Référence fabricant modifiée');
        } else {
          await firstValueFrom(
            this.makerRefApi.create({
              artId: articleId,
              mkrId: partnerId,
              artMkrReference: reference,
              artMkrStock: stock,
              artMkrSellPrice: price,
            }),
          );
          this.toast.success('Référence fabricant créée');
        }
      }
      this.closeModal();
      await this.load();
    } catch {
      /* déjà notifié */
    } finally {
      this.saving.set(false);
    }
  }

  async removeSupplier(r: SupplierReference): Promise<void> {
    const ok = await this.confirm.askDelete(
      `la référence « ${r.splRefReference} » de ${r.supplier?.splName}`,
    );
    if (!ok) return;
    try {
      await firstValueFrom(this.supplierRefApi.delete(r.article!.artId, r.supplier!.splId));
      this.toast.success('Référence supprimée');
      await this.load();
    } catch {
      /* déjà notifié */
    }
  }

  async removeMaker(r: MakerReference): Promise<void> {
    const ok = await this.confirm.askDelete(
      `la référence « ${r.reference} » de ${r.maker?.mkrName}`,
    );
    if (!ok) return;
    try {
      await firstValueFrom(this.makerRefApi.delete(r.article!.artId, r.maker!.mkrId));
      this.toast.success('Référence supprimée');
      await this.load();
    } catch {
      /* déjà notifié */
    }
  }
}
