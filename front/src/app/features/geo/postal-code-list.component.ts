import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { PostalCodeCityService, PostalCodeService } from '../../core/api';
import { PostalCode, PostalCodeCity } from '../../core/models/api.models';
import { ConfirmService } from '../../core/services/confirm.service';
import { ToastService } from '../../core/services/toast.service';
import { CapitalizePipe } from '../../shared/pipes/format.pipes';
import { EmptyStateComponent } from '../../shared/ui/empty-state.component';
import { FieldErrorComponent } from '../../shared/ui/field-error.component';
import { IconComponent } from '../../shared/ui/icon.component';
import { ModalComponent } from '../../shared/ui/modal.component';
import { PageHeaderComponent } from '../../shared/ui/page-header.component';
import { SearchInputComponent } from '../../shared/ui/search-input.component';

@Component({
  selector: 'app-postal-code-list',
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
      title="Codes postaux"
      subtitle="Chaque adresse pointe vers un code postal, lui-même associé à une ou plusieurs villes."
    >
      <button type="button" class="btn-secondary" (click)="load()">
        <app-icon name="refresh" [size]="16" /> Actualiser
      </button>
      <button type="button" class="btn-primary" (click)="openCreate()">
        <app-icon name="plus" [size]="16" /> Nouveau code postal
      </button>
    </app-page-header>

    <div class="card">
      <div class="flex flex-wrap items-center justify-between gap-3 border-b border-ink-200 p-4 dark:border-ink-800">
        <app-search-input [value]="search()" (valueChange)="search.set($event)" placeholder="Rechercher un code…" />
        <p class="text-[13px] muted">{{ filtered().length }} / {{ items().length }} codes</p>
      </div>

      @if (loading()) {
        <div class="space-y-3 p-5">
          @for (i of [1, 2, 3]; track i) {
            <div class="skeleton h-10 w-full"></div>
          }
        </div>
      } @else if (filtered().length === 0) {
        <app-empty-state icon="boxes" title="Aucun code postal" message="Créez un code postal pour commencer.">
          <button type="button" class="btn-primary" (click)="openCreate()">
            <app-icon name="plus" [size]="16" /> Nouveau code postal
          </button>
        </app-empty-state>
      } @else {
        <div class="table-wrap">
          <table class="table">
            <thead>
              <tr>
                <th class="w-20">ID</th>
                <th class="w-40">Code</th>
                <th>Villes associées</th>
                <th class="w-28 text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              @for (p of filtered(); track p.pCodeId) {
                <tr>
                  <td class="num muted">#{{ p.pCodeId }}</td>
                  <td class="num font-medium">{{ p.pCodeName }}</td>
                  <td>
                    @if (citiesOf(p.pCodeId).length) {
                      <div class="flex flex-wrap gap-1.5">
                        @for (name of citiesOf(p.pCodeId); track name) {
                          <span class="badge-neutral">{{ name | capitalize }}</span>
                        }
                      </div>
                    } @else {
                      <span class="text-[13px] muted">Aucune ville associée</span>
                    }
                  </td>
                  <td>
                    <div class="flex justify-end gap-1">
                      <button type="button" class="btn-icon" (click)="openEdit(p)" title="Modifier">
                        <app-icon name="edit" [size]="16" />
                      </button>
                      <button
                        type="button"
                        class="btn-icon hover:bg-red-50 hover:text-red-600 dark:hover:bg-red-500/10"
                        (click)="remove(p)"
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
        [title]="editing() ? 'Modifier le code postal' : 'Nouveau code postal'"
        widthClass="max-w-md"
        (closed)="closeModal()"
      >
        <form [formGroup]="form" (ngSubmit)="submit()" id="pcode-form">
          <label class="label" for="pCodeName">Code postal</label>
          <input
            id="pCodeName"
            type="text"
            inputmode="numeric"
            class="input"
            [class.input-error]="form.controls.pCodeName.invalid && submitted()"
            formControlName="pCodeName"
            placeholder="69001"
          />
          <app-field-error
            [control]="form.controls.pCodeName"
            label="Le code postal"
            [submitted]="submitted()"
          />
        </form>

        <div footer>
          <button type="button" class="btn-secondary" (click)="closeModal()">Annuler</button>
          <button type="submit" form="pcode-form" class="btn-primary" [disabled]="saving()">
            <app-icon name="save" [size]="16" />
            {{ editing() ? 'Enregistrer' : 'Créer' }}
          </button>
        </div>
      </app-modal>
    }
  `,
})
export class PostalCodeListComponent implements OnInit {
  private readonly api = inject(PostalCodeService);
  private readonly linkApi = inject(PostalCodeCityService);
  private readonly fb = inject(FormBuilder);
  private readonly toast = inject(ToastService);
  private readonly confirm = inject(ConfirmService);

  protected readonly items = signal<PostalCode[]>([]);
  protected readonly links = signal<PostalCodeCity[]>([]);
  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly search = signal('');
  protected readonly modalOpen = signal(false);
  protected readonly editing = signal<PostalCode | null>(null);
  protected readonly submitted = signal(false);

  protected readonly form = this.fb.nonNullable.group({
    pCodeName: ['', [Validators.required, Validators.pattern(/^[0-9A-Za-z\- ]{4,10}$/)]],
  });

  protected readonly filtered = computed(() => {
    const q = this.search().trim().toLowerCase();
    const list = this.items();
    if (!q) return list;
    return list.filter((p) => p.pCodeName?.toLowerCase().includes(q));
  });

  ngOnInit(): void {
    this.load();
  }

  async load(): Promise<void> {
    this.loading.set(true);
    try {
      const [codes, links] = await Promise.all([
        firstValueFrom(this.api.list()),
        firstValueFrom(this.linkApi.list()),
      ]);
      this.items.set(codes);
      this.links.set(links);
    } catch {
      this.items.set([]);
      this.links.set([]);
    } finally {
      this.loading.set(false);
    }
  }

  /** Villes rattachées à un code postal via la table d'association. */
  citiesOf(pCodeId: number): string[] {
    return this.links()
      .filter((l) => l.postalCode?.pCodeId === pCodeId)
      .map((l) => l.city?.cityName ?? '')
      .filter(Boolean);
  }

  openCreate(): void {
    this.editing.set(null);
    this.submitted.set(false);
    this.form.reset({ pCodeName: '' });
    this.modalOpen.set(true);
  }

  openEdit(item: PostalCode): void {
    this.editing.set(item);
    this.submitted.set(false);
    this.form.reset({ pCodeName: item.pCodeName });
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
    const payload = { pCodeName: this.form.getRawValue().pCodeName.trim() };
    const current = this.editing();
    try {
      if (current) {
        await firstValueFrom(this.api.update(current.pCodeId, payload));
        this.toast.success('Code postal modifié', payload.pCodeName);
      } else {
        await firstValueFrom(this.api.create(payload));
        this.toast.success('Code postal créé', payload.pCodeName);
      }
      this.closeModal();
      await this.load();
    } catch {
      /* déjà notifié */
    } finally {
      this.saving.set(false);
    }
  }

  async remove(item: PostalCode): Promise<void> {
    const ok = await this.confirm.askDelete(`le code postal ${item.pCodeName}`);
    if (!ok) return;
    try {
      await firstValueFrom(this.api.delete(item.pCodeId));
      this.toast.success('Code postal supprimé');
      await this.load();
    } catch {
      /* déjà notifié */
    }
  }
}
