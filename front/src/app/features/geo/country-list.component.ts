import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { CountryService } from '../../core/api';
import { Country } from '../../core/models/api.models';
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
  selector: 'app-country-list',
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
    <app-page-header title="Pays" subtitle="Référentiel des pays rattachés aux villes.">
      <button type="button" class="btn-secondary" (click)="load()">
        <app-icon name="refresh" [size]="16" /> Actualiser
      </button>
      <button type="button" class="btn-primary" (click)="openCreate()">
        <app-icon name="plus" [size]="16" /> Nouveau pays
      </button>
    </app-page-header>

    <div class="card">
      <div class="flex flex-wrap items-center justify-between gap-3 border-b border-ink-200 p-4 dark:border-ink-800">
        <app-search-input [value]="search()" (valueChange)="search.set($event)" placeholder="Rechercher un pays…" />
        <p class="text-[13px] muted">{{ filtered().length }} / {{ items().length }} pays</p>
      </div>

      @if (loading()) {
        <div class="space-y-3 p-5">
          @for (i of [1, 2, 3]; track i) {
            <div class="skeleton h-10 w-full"></div>
          }
        </div>
      } @else if (filtered().length === 0) {
        <app-empty-state
          icon="globe"
          title="Aucun pays"
          message="Ajoutez un pays pour pouvoir y rattacher des villes."
        >
          <button type="button" class="btn-primary" (click)="openCreate()">
            <app-icon name="plus" [size]="16" /> Nouveau pays
          </button>
        </app-empty-state>
      } @else {
        <div class="table-wrap">
          <table class="table">
            <thead>
              <tr>
                <th class="w-20">ID</th>
                <th>Nom</th>
                <th class="w-28 text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              @for (c of filtered(); track c.cntId) {
                <tr>
                  <td class="num muted">#{{ c.cntId }}</td>
                  <td class="font-medium">{{ c.cntName | capitalize }}</td>
                  <td>
                    <div class="flex justify-end gap-1">
                      <button type="button" class="btn-icon" (click)="openEdit(c)" title="Modifier">
                        <app-icon name="edit" [size]="16" />
                      </button>
                      <button
                        type="button"
                        class="btn-icon hover:bg-red-50 hover:text-red-600 dark:hover:bg-red-500/10"
                        (click)="remove(c)"
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
        [title]="editing() ? 'Modifier le pays' : 'Nouveau pays'"
        widthClass="max-w-md"
        (closed)="closeModal()"
      >
        <form [formGroup]="form" (ngSubmit)="submit()" id="country-form">
          <label class="label" for="cntName">Nom du pays</label>
          <input
            id="cntName"
            type="text"
            class="input"
            [class.input-error]="form.controls.cntName.invalid && submitted()"
            formControlName="cntName"
            placeholder="France"
          />
          <app-field-error [control]="form.controls.cntName" label="Le nom" [submitted]="submitted()" />
        </form>

        <div footer>
          <button type="button" class="btn-secondary" (click)="closeModal()">Annuler</button>
          <button type="submit" form="country-form" class="btn-primary" [disabled]="saving()">
            <app-icon name="save" [size]="16" />
            {{ editing() ? 'Enregistrer' : 'Créer' }}
          </button>
        </div>
      </app-modal>
    }
  `,
})
export class CountryListComponent implements OnInit {
  private readonly api = inject(CountryService);
  private readonly fb = inject(FormBuilder);
  private readonly toast = inject(ToastService);
  private readonly confirm = inject(ConfirmService);

  protected readonly items = signal<Country[]>([]);
  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly search = signal('');
  protected readonly modalOpen = signal(false);
  protected readonly editing = signal<Country | null>(null);
  protected readonly submitted = signal(false);

  protected readonly form = this.fb.nonNullable.group({
    cntName: ['', [Validators.required, Validators.maxLength(80)]],
  });

  protected readonly filtered = computed(() => {
    const q = this.search().trim().toLowerCase();
    const list = this.items();
    if (!q) return list;
    return list.filter((c) => c.cntName?.toLowerCase().includes(q));
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

  openCreate(): void {
    this.editing.set(null);
    this.submitted.set(false);
    this.form.reset({ cntName: '' });
    this.modalOpen.set(true);
  }

  openEdit(country: Country): void {
    this.editing.set(country);
    this.submitted.set(false);
    this.form.reset({ cntName: country.cntName });
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
    const payload = { cntName: this.form.getRawValue().cntName.trim() };
    const current = this.editing();

    try {
      if (current) {
        await firstValueFrom(this.api.update(current.cntId, payload));
        this.toast.success('Pays modifié', payload.cntName);
      } else {
        await firstValueFrom(this.api.create(payload));
        this.toast.success('Pays créé', payload.cntName);
      }
      this.closeModal();
      await this.load();
    } catch {
      /* le message d'erreur est déjà affiché par l'intercepteur */
    } finally {
      this.saving.set(false);
    }
  }

  async remove(country: Country): Promise<void> {
    const ok = await this.confirm.askDelete(`le pays « ${country.cntName} »`);
    if (!ok) return;
    try {
      await firstValueFrom(this.api.delete(country.cntId));
      this.toast.success('Pays supprimé');
      await this.load();
    } catch {
      /* déjà notifié */
    }
  }
}
