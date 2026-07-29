import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { CityService, CountryService, PostalCodeCityService } from '../../core/api';
import { City, Country, PostalCodeCity } from '../../core/models/api.models';
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
  selector: 'app-city-list',
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
    <app-page-header title="Villes" subtitle="Chaque ville appartient à un pays et peut couvrir plusieurs codes postaux.">
      <button type="button" class="btn-secondary" (click)="load()">
        <app-icon name="refresh" [size]="16" /> Actualiser
      </button>
      <button type="button" class="btn-primary" (click)="openCreate()" [disabled]="countries().length === 0">
        <app-icon name="plus" [size]="16" /> Nouvelle ville
      </button>
    </app-page-header>

    @if (!loading() && countries().length === 0) {
      <div
        class="mb-4 flex items-start gap-2.5 rounded-lg border border-amber-200 bg-amber-50 px-4 py-3 text-[13px] text-amber-800 dark:border-amber-500/30 dark:bg-amber-500/10 dark:text-amber-300"
      >
        <app-icon name="alert" [size]="16" class="mt-0.5" />
        <span>Aucun pays n'existe encore. Créez d'abord un pays pour pouvoir y rattacher des villes.</span>
      </div>
    }

    <div class="card">
      <div class="flex flex-wrap items-center justify-between gap-3 border-b border-ink-200 p-4 dark:border-ink-800">
        <app-search-input [value]="search()" (valueChange)="search.set($event)" placeholder="Rechercher une ville…" />
        <p class="text-[13px] muted">{{ filtered().length }} / {{ items().length }} villes</p>
      </div>

      @if (loading()) {
        <div class="space-y-3 p-5">
          @for (i of [1, 2, 3]; track i) {
            <div class="skeleton h-10 w-full"></div>
          }
        </div>
      } @else if (filtered().length === 0) {
        <app-empty-state icon="building" title="Aucune ville" message="Ajoutez une ville pour pouvoir enregistrer des adresses." />
      } @else {
        <div class="table-wrap">
          <table class="table">
            <thead>
              <tr>
                <th class="w-20">ID</th>
                <th>Ville</th>
                <th class="w-40">Pays</th>
                <th>Codes postaux</th>
                <th class="w-28 text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              @for (c of filtered(); track c.cityId) {
                <tr>
                  <td class="num muted">#{{ c.cityId }}</td>
                  <td class="font-medium">{{ c.cityName | capitalize }}</td>
                  <td>
                    @if (c.country) {
                      <span class="badge-brand">{{ c.country.cntName | capitalize }}</span>
                    } @else {
                      <span class="text-[13px] muted">—</span>
                    }
                  </td>
                  <td>
                    @if (codesOf(c.cityId).length) {
                      <div class="flex flex-wrap gap-1.5">
                        @for (code of codesOf(c.cityId); track code) {
                          <span class="badge-neutral num">{{ code }}</span>
                        }
                      </div>
                    } @else {
                      <span class="text-[13px] muted">—</span>
                    }
                  </td>
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
        [title]="editing() ? 'Modifier la ville' : 'Nouvelle ville'"
        widthClass="max-w-md"
        (closed)="closeModal()"
      >
        <form [formGroup]="form" (ngSubmit)="submit()" id="city-form" class="space-y-4">
          <div>
            <label class="label" for="cityName">Nom de la ville</label>
            <input
              id="cityName"
              type="text"
              class="input"
              [class.input-error]="form.controls.cityName.invalid && submitted()"
              formControlName="cityName"
              placeholder="Lyon"
            />
            <app-field-error [control]="form.controls.cityName" label="Le nom" [submitted]="submitted()" />
          </div>

          <div>
            <label class="label" for="cntId">Pays</label>
            <select
              id="cntId"
              class="input"
              [class.input-error]="form.controls.cntId.invalid && submitted()"
              formControlName="cntId"
            >
              <option [ngValue]="null" disabled>— Sélectionner un pays —</option>
              @for (country of countries(); track country.cntId) {
                <option [ngValue]="country.cntId">{{ country.cntName | capitalize }}</option>
              }
            </select>
            <app-field-error [control]="form.controls.cntId" label="Le pays" [submitted]="submitted()" />
          </div>
        </form>

        <div footer>
          <button type="button" class="btn-secondary" (click)="closeModal()">Annuler</button>
          <button type="submit" form="city-form" class="btn-primary" [disabled]="saving()">
            <app-icon name="save" [size]="16" />
            {{ editing() ? 'Enregistrer' : 'Créer' }}
          </button>
        </div>
      </app-modal>
    }
  `,
})
export class CityListComponent implements OnInit {
  private readonly api = inject(CityService);
  private readonly countryApi = inject(CountryService);
  private readonly linkApi = inject(PostalCodeCityService);
  private readonly fb = inject(FormBuilder);
  private readonly toast = inject(ToastService);
  private readonly confirm = inject(ConfirmService);

  protected readonly items = signal<City[]>([]);
  protected readonly countries = signal<Country[]>([]);
  protected readonly links = signal<PostalCodeCity[]>([]);
  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly search = signal('');
  protected readonly modalOpen = signal(false);
  protected readonly editing = signal<City | null>(null);
  protected readonly submitted = signal(false);

  protected readonly form = this.fb.group({
    cityName: ['', [Validators.required, Validators.maxLength(80)]],
    cntId: [null as number | null, [Validators.required]],
  });

  protected readonly filtered = computed(() => {
    const q = this.search().trim().toLowerCase();
    const list = this.items();
    if (!q) return list;
    return list.filter(
      (c) =>
        c.cityName?.toLowerCase().includes(q) || c.country?.cntName?.toLowerCase().includes(q),
    );
  });

  ngOnInit(): void {
    this.load();
  }

  async load(): Promise<void> {
    this.loading.set(true);
    try {
      const [cities, countries, links] = await Promise.all([
        firstValueFrom(this.api.list()),
        firstValueFrom(this.countryApi.list()),
        firstValueFrom(this.linkApi.list()),
      ]);
      this.items.set(cities);
      this.countries.set(countries);
      this.links.set(links);
    } catch {
      this.items.set([]);
    } finally {
      this.loading.set(false);
    }
  }

  codesOf(cityId: number): string[] {
    return this.links()
      .filter((l) => l.city?.cityId === cityId)
      .map((l) => l.postalCode?.pCodeName ?? '')
      .filter(Boolean);
  }

  openCreate(): void {
    this.editing.set(null);
    this.submitted.set(false);
    this.form.reset({ cityName: '', cntId: this.countries()[0]?.cntId ?? null });
    this.modalOpen.set(true);
  }

  openEdit(city: City): void {
    this.editing.set(city);
    this.submitted.set(false);
    this.form.reset({ cityName: city.cityName, cntId: city.country?.cntId ?? null });
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
    const payload = { cityName: (raw.cityName ?? '').trim(), cntId: Number(raw.cntId) };
    const current = this.editing();
    try {
      if (current) {
        await firstValueFrom(this.api.update(current.cityId, payload));
        this.toast.success('Ville modifiée', payload.cityName);
      } else {
        await firstValueFrom(this.api.create(payload));
        this.toast.success('Ville créée', payload.cityName);
      }
      this.closeModal();
      await this.load();
    } catch {
      /* déjà notifié */
    } finally {
      this.saving.set(false);
    }
  }

  async remove(city: City): Promise<void> {
    const ok = await this.confirm.askDelete(`la ville « ${city.cityName} »`);
    if (!ok) return;
    try {
      await firstValueFrom(this.api.delete(city.cityId));
      this.toast.success('Ville supprimée');
      await this.load();
    } catch {
      /* déjà notifié */
    }
  }
}
