import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { CategoryService } from '../../core/api';
import { Category } from '../../core/models/api.models';
import { ConfirmService } from '../../core/services/confirm.service';
import { ToastService } from '../../core/services/toast.service';
import { CapitalizePipe } from '../../shared/pipes/format.pipes';
import { EmptyStateComponent } from '../../shared/ui/empty-state.component';
import { FieldErrorComponent } from '../../shared/ui/field-error.component';
import { IconComponent } from '../../shared/ui/icon.component';
import { ModalComponent } from '../../shared/ui/modal.component';
import { PageHeaderComponent } from '../../shared/ui/page-header.component';
import { SearchInputComponent } from '../../shared/ui/search-input.component';

/** Ligne aplatie de l'arbre des catégories, avec son niveau d'indentation. */
interface FlatCategory {
  readonly node: Category;
  readonly depth: number;
}

@Component({
  selector: 'app-category-list',
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
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <app-page-header
      title="Catégories"
      subtitle="Arborescence du catalogue. Une catégorie peut avoir une catégorie parente (relation « Contain » du MCD)."
    >
      <button type="button" class="btn-secondary" (click)="load()">
        <app-icon name="refresh" [size]="16" /> Actualiser
      </button>
      <button type="button" class="btn-primary" (click)="openCreate()">
        <app-icon name="plus" [size]="16" /> Nouvelle catégorie
      </button>
    </app-page-header>

    <div class="card">
      <div class="flex flex-wrap items-center justify-between gap-3 border-b border-ink-200 p-4 dark:border-ink-800">
        <app-search-input [value]="search()" (valueChange)="search.set($event)" placeholder="Rechercher une catégorie…" />
        <p class="text-[13px] muted">{{ rows().length }} catégories</p>
      </div>

      @if (loading()) {
        <div class="space-y-3 p-5">
          @for (i of [1, 2, 3, 4]; track i) {
            <div class="skeleton h-10 w-full"></div>
          }
        </div>
      } @else if (rows().length === 0) {
        <app-empty-state icon="tag" title="Aucune catégorie" message="Structurez votre catalogue : Climatisation, Chauffage, Accessoires…">
          <button type="button" class="btn-primary" (click)="openCreate()">
            <app-icon name="plus" [size]="16" /> Nouvelle catégorie
          </button>
        </app-empty-state>
      } @else {
        <div class="table-wrap">
          <table class="table">
            <thead>
              <tr>
                <th class="w-20">ID</th>
                <th>Nom</th>
                <th class="w-56">Slug</th>
                <th class="w-48">Parent</th>
                <th class="w-28 text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              @for (row of rows(); track row.node.catId) {
                <tr>
                  <td class="num muted">#{{ row.node.catId }}</td>
                  <td>
                    <div class="flex items-center" [style.padding-left.px]="row.depth * 20">
                      @if (row.depth > 0) {
                        <span class="mr-1.5 text-ink-300 dark:text-ink-600">
                          <app-icon name="chevronRight" [size]="14" />
                        </span>
                      }
                      <span class="font-medium">{{ row.node.catName | capitalize }}</span>
                    </div>
                  </td>
                  <td><code class="text-[12.5px] muted">{{ row.node.catSlug }}</code></td>
                  <td>
                    @if (row.node.catParentName) {
                      <span class="badge-neutral">{{ row.node.catParentName | capitalize }}</span>
                    } @else {
                      <span class="badge-brand">Racine</span>
                    }
                  </td>
                  <td>
                    <div class="flex justify-end gap-1">
                      <button type="button" class="btn-icon" (click)="openEdit(row.node)" title="Modifier">
                        <app-icon name="edit" [size]="16" />
                      </button>
                      <button
                        type="button"
                        class="btn-icon hover:bg-red-50 hover:text-red-600 dark:hover:bg-red-500/10"
                        (click)="remove(row.node)"
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
        [title]="editing() ? 'Modifier la catégorie' : 'Nouvelle catégorie'"
        widthClass="max-w-lg"
        (closed)="closeModal()"
      >
        <form [formGroup]="form" (ngSubmit)="submit()" id="cat-form" class="space-y-4">
          <div>
            <label class="label" for="catName">Nom</label>
            <input
              id="catName"
              type="text"
              class="input"
              [class.input-error]="form.controls.catName.invalid && submitted()"
              formControlName="catName"
              (input)="syncSlug()"
              placeholder="Climatisation réversible"
            />
            <app-field-error [control]="form.controls.catName" label="Le nom" [submitted]="submitted()" />
          </div>

          <div>
            <label class="label" for="catSlug">Slug</label>
            <input
              id="catSlug"
              type="text"
              class="input font-mono text-[13px]"
              [class.input-error]="form.controls.catSlug.invalid && submitted()"
              formControlName="catSlug"
              placeholder="climatisation-reversible"
            />
            <app-field-error [control]="form.controls.catSlug" label="Le slug" [submitted]="submitted()" />
            <p class="hint">Généré automatiquement depuis le nom, modifiable.</p>
          </div>

          <div>
            <label class="label" for="parentId">Catégorie parente</label>
            <select id="parentId" class="input" formControlName="parentId">
              <option [ngValue]="null">— Aucune (catégorie racine) —</option>
              @for (c of availableParents(); track c.catId) {
                <option [ngValue]="c.catId">{{ c.catName | capitalize }}</option>
              }
            </select>
          </div>
        </form>

        <div footer>
          <button type="button" class="btn-secondary" (click)="closeModal()">Annuler</button>
          <button type="submit" form="cat-form" class="btn-primary" [disabled]="saving()">
            <app-icon name="save" [size]="16" />
            {{ editing() ? 'Enregistrer' : 'Créer' }}
          </button>
        </div>
      </app-modal>
    }
  `,
})
export class CategoryListComponent implements OnInit {
  private readonly api = inject(CategoryService);
  private readonly fb = inject(FormBuilder);
  private readonly toast = inject(ToastService);
  private readonly confirm = inject(ConfirmService);

  protected readonly items = signal<Category[]>([]);
  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly search = signal('');
  protected readonly modalOpen = signal(false);
  protected readonly editing = signal<Category | null>(null);
  protected readonly submitted = signal(false);
  /** Le slug suit le nom tant que l'utilisateur ne l'a pas édité à la main. */
  private slugTouched = false;

  protected readonly form = this.fb.group({
    catName: ['', [Validators.required, Validators.maxLength(80)]],
    catSlug: ['', [Validators.required, Validators.pattern(/^[a-z0-9]+(?:-[a-z0-9]+)*$/)]],
    parentId: [null as number | null],
  });

  /** Toutes les catégories, à plat (l'API renvoie déjà un arbre partiel). */
  protected readonly flatAll = computed<Category[]>(() => {
    const out = new Map<number, Category>();
    const walk = (nodes: Category[] | null | undefined) => {
      for (const n of nodes ?? []) {
        if (!out.has(n.catId)) out.set(n.catId, n);
        walk(n.children);
      }
    };
    walk(this.items());
    return [...out.values()].sort((a, b) => a.catName.localeCompare(b.catName, 'fr'));
  });

  /** Arbre aplati avec indentation, filtré par la recherche. */
  protected readonly rows = computed<FlatCategory[]>(() => {
    const q = this.search().trim().toLowerCase();

    if (q) {
      return this.flatAll()
        .filter((c) => c.catName?.toLowerCase().includes(q) || c.catSlug?.toLowerCase().includes(q))
        .map((node) => ({ node, depth: 0 }));
    }

    const out: FlatCategory[] = [];
    const walk = (nodes: Category[] | null | undefined, depth: number) => {
      for (const n of nodes ?? []) {
        out.push({ node: n, depth });
        walk(n.children, depth + 1);
      }
    };
    // On ne repart que des racines pour éviter les doublons.
    walk(this.items().filter((c) => !c.catParentName), 0);

    // Sécurité : si l'API renvoie une liste plate, on affiche tout.
    if (out.length === 0) return this.items().map((node) => ({ node, depth: 0 }));
    return out;
  });

  /** Parents possibles : toutes les catégories sauf celle en cours d'édition. */
  protected readonly availableParents = computed(() => {
    const current = this.editing();
    return this.flatAll().filter((c) => c.catId !== current?.catId);
  });

  ngOnInit(): void {
    this.load();
  }

  async load(): Promise<void> {
    this.loading.set(true);
    try {
      this.items.set(await firstValueFrom(this.api.list()));
    } catch {
      this.items.set([]);
    } finally {
      this.loading.set(false);
    }
  }

  syncSlug(): void {
    if (this.slugTouched) return;
    const name = this.form.controls.catName.value ?? '';
    this.form.controls.catSlug.setValue(slugify(name));
  }

  openCreate(): void {
    this.editing.set(null);
    this.submitted.set(false);
    this.slugTouched = false;
    this.form.reset({ catName: '', catSlug: '', parentId: null });
    this.modalOpen.set(true);
  }

  openEdit(category: Category): void {
    this.editing.set(category);
    this.submitted.set(false);
    this.slugTouched = true;
    const parent = this.flatAll().find(
      (c) => c.catName?.toLowerCase() === category.catParentName?.toLowerCase(),
    );
    this.form.reset({
      catName: category.catName,
      catSlug: category.catSlug,
      parentId: parent?.catId ?? null,
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
      return;
    }
    this.saving.set(true);
    const raw = this.form.getRawValue();
    const payload = {
      catName: (raw.catName ?? '').trim(),
      catSlug: (raw.catSlug ?? '').trim(),
      parentId: raw.parentId ? Number(raw.parentId) : null,
    };
    const current = this.editing();
    try {
      if (current) {
        await firstValueFrom(this.api.update(current.catId, payload));
        this.toast.success('Catégorie modifiée', payload.catName);
      } else {
        await firstValueFrom(this.api.create(payload));
        this.toast.success('Catégorie créée', payload.catName);
      }
      this.closeModal();
      await this.load();
    } catch {
      /* déjà notifié */
    } finally {
      this.saving.set(false);
    }
  }

  async remove(category: Category): Promise<void> {
    const ok = await this.confirm.askDelete(`la catégorie « ${category.catName} »`);
    if (!ok) return;
    try {
      await firstValueFrom(this.api.delete(category.catId));
      this.toast.success('Catégorie supprimée');
      await this.load();
    } catch {
      /* déjà notifié */
    }
  }
}

/** Transforme un libellé en slug URL (sans accents, en minuscules). */
function slugify(value: string): string {
  return value
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .toLowerCase()
    .trim()
    .replace(/[^a-z0-9]+/g, '-')
    .replace(/^-+|-+$/g, '');
}
