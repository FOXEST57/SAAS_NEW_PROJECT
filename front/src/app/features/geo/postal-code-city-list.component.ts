import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { CityService, PostalCodeCityService, PostalCodeService } from '../../core/api';
import { City, PostalCode, PostalCodeCity } from '../../core/models/api.models';
import { ConfirmService } from '../../core/services/confirm.service';
import { ToastService } from '../../core/services/toast.service';
import { CapitalizePipe } from '../../shared/pipes/format.pipes';
import { EmptyStateComponent } from '../../shared/ui/empty-state.component';
import { FieldErrorComponent } from '../../shared/ui/field-error.component';
import { IconComponent } from '../../shared/ui/icon.component';
import { ModalComponent } from '../../shared/ui/modal.component';
import { PageHeaderComponent } from '../../shared/ui/page-header.component';
import { SearchInputComponent } from '../../shared/ui/search-input.component';

/**
 * Table d'association code postal ↔ ville.
 *
 * Ce lien est indispensable : le backend refuse la création d'une adresse
 * si le couple (code postal, ville) n'est pas déclaré ici.
 */
@Component({
  selector: 'app-postal-code-city-list',
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
      title="Associations code postal / ville"
      subtitle="Une adresse ne peut être enregistrée que si le couple code postal + ville figure dans cette table."
    >
      <button type="button" class="btn-secondary" (click)="load()">
        <app-icon name="refresh" [size]="16" /> Actualiser
      </button>
      <button type="button" class="btn-primary" (click)="openCreate()">
        <app-icon name="plus" [size]="16" /> Nouvelle association
      </button>
    </app-page-header>

    <div class="card">
      <div class="flex flex-wrap items-center justify-between gap-3 border-b border-ink-200 p-4 dark:border-ink-800">
        <app-search-input [value]="search()" (valueChange)="search.set($event)" placeholder="Rechercher…" />
        <p class="text-[13px] muted">{{ filtered().length }} / {{ items().length }} associations</p>
      </div>

      @if (loading()) {
        <div class="space-y-3 p-5">
          @for (i of [1, 2, 3]; track i) {
            <div class="skeleton h-10 w-full"></div>
          }
        </div>
      } @else if (filtered().length === 0) {
        <app-empty-state
          icon="layers"
          title="Aucune association"
          message="Reliez un code postal à une ville pour permettre la saisie d'adresses."
        />
      } @else {
        <div class="table-wrap">
          <table class="table">
            <thead>
              <tr>
                <th class="w-40">Code postal</th>
                <th>Ville</th>
                <th class="w-40">Pays</th>
                <th class="w-24 text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              @for (l of filtered(); track key(l)) {
                <tr>
                  <td class="num font-medium">{{ l.postalCode?.pCodeName }}</td>
                  <td>{{ l.city?.cityName | capitalize }}</td>
                  <td>
                    <span class="badge-neutral">{{ l.city?.country?.cntName | capitalize }}</span>
                  </td>
                  <td>
                    <div class="flex justify-end">
                      <button
                        type="button"
                        class="btn-icon hover:bg-red-50 hover:text-red-600 dark:hover:bg-red-500/10"
                        (click)="remove(l)"
                        title="Supprimer l'association"
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
      <app-modal title="Nouvelle association" widthClass="max-w-md" (closed)="closeModal()">
        <form [formGroup]="form" (ngSubmit)="submit()" id="pcc-form" class="space-y-4">
          <div>
            <label class="label" for="pCodeId">Code postal</label>
            <select
              id="pCodeId"
              class="input"
              [class.input-error]="form.controls.pCodeId.invalid && submitted()"
              formControlName="pCodeId"
            >
              <option [ngValue]="null" disabled>— Sélectionner —</option>
              @for (p of postalCodes(); track p.pCodeId) {
                <option [ngValue]="p.pCodeId">{{ p.pCodeName }}</option>
              }
            </select>
            <app-field-error [control]="form.controls.pCodeId" label="Le code postal" [submitted]="submitted()" />
          </div>

          <div>
            <label class="label" for="cityId">Ville</label>
            <select
              id="cityId"
              class="input"
              [class.input-error]="form.controls.cityId.invalid && submitted()"
              formControlName="cityId"
            >
              <option [ngValue]="null" disabled>— Sélectionner —</option>
              @for (c of cities(); track c.cityId) {
                <option [ngValue]="c.cityId">{{ c.cityName | capitalize }}</option>
              }
            </select>
            <app-field-error [control]="form.controls.cityId" label="La ville" [submitted]="submitted()" />
          </div>
        </form>

        <div footer>
          <button type="button" class="btn-secondary" (click)="closeModal()">Annuler</button>
          <button type="submit" form="pcc-form" class="btn-primary" [disabled]="saving()">
            <app-icon name="save" [size]="16" /> Créer
          </button>
        </div>
      </app-modal>
    }
  `,
})
export class PostalCodeCityListComponent implements OnInit {
  private readonly api = inject(PostalCodeCityService);
  private readonly postalApi = inject(PostalCodeService);
  private readonly cityApi = inject(CityService);
  private readonly fb = inject(FormBuilder);
  private readonly toast = inject(ToastService);
  private readonly confirm = inject(ConfirmService);

  protected readonly items = signal<PostalCodeCity[]>([]);
  protected readonly postalCodes = signal<PostalCode[]>([]);
  protected readonly cities = signal<City[]>([]);
  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly search = signal('');
  protected readonly modalOpen = signal(false);
  protected readonly submitted = signal(false);

  protected readonly form = this.fb.group({
    pCodeId: [null as number | null, [Validators.required]],
    cityId: [null as number | null, [Validators.required]],
  });

  protected readonly filtered = computed(() => {
    const q = this.search().trim().toLowerCase();
    const list = this.items();
    if (!q) return list;
    return list.filter(
      (l) =>
        l.postalCode?.pCodeName?.toLowerCase().includes(q) ||
        l.city?.cityName?.toLowerCase().includes(q),
    );
  });

  ngOnInit(): void {
    this.load();
  }

  key(link: PostalCodeCity): string {
    return `${link.postalCode?.pCodeId}-${link.city?.cityId}`;
  }

  async load(): Promise<void> {
    this.loading.set(true);
    try {
      const [links, codes, cities] = await Promise.all([
        firstValueFrom(this.api.list()),
        firstValueFrom(this.postalApi.list()),
        firstValueFrom(this.cityApi.list()),
      ]);
      this.items.set(links);
      this.postalCodes.set(codes);
      this.cities.set(cities);
    } catch {
      this.items.set([]);
    } finally {
      this.loading.set(false);
    }
  }

  openCreate(): void {
    this.submitted.set(false);
    this.form.reset({ pCodeId: null, cityId: null });
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
    try {
      await firstValueFrom(
        this.api.create({ pCodeId: Number(raw.pCodeId), cityId: Number(raw.cityId) }),
      );
      this.toast.success('Association créée');
      this.closeModal();
      await this.load();
    } catch {
      /* déjà notifié */
    } finally {
      this.saving.set(false);
    }
  }

  async remove(link: PostalCodeCity): Promise<void> {
    const ok = await this.confirm.askDelete(
      `l'association ${link.postalCode?.pCodeName} / ${link.city?.cityName}`,
    );
    if (!ok) return;
    try {
      await firstValueFrom(this.api.delete(link.postalCode!.pCodeId, link.city!.cityId));
      this.toast.success('Association supprimée');
      await this.load();
    } catch {
      /* déjà notifié */
    }
  }
}
