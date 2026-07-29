import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  ViewChild,
  computed,
  inject,
  signal,
} from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { SupplierReferenceService, SupplierService } from '../../core/api';
import { Supplier, SupplierReference } from '../../core/models/api.models';
import { AddressResolverService } from '../../core/services/address-resolver.service';
import { ConfirmService } from '../../core/services/confirm.service';
import { ToastService } from '../../core/services/toast.service';
import { AddressLinePipe, CapitalizePipe } from '../../shared/pipes/format.pipes';
import { AddressAutocompleteComponent } from '../../shared/ui/address-autocomplete.component';
import { EmptyStateComponent } from '../../shared/ui/empty-state.component';
import { FieldErrorComponent } from '../../shared/ui/field-error.component';
import { IconComponent } from '../../shared/ui/icon.component';
import { ModalComponent } from '../../shared/ui/modal.component';
import { PageHeaderComponent } from '../../shared/ui/page-header.component';
import { SearchInputComponent } from '../../shared/ui/search-input.component';
import { phoneValidator, toE164 } from '../../shared/validators';

@Component({
  selector: 'app-supplier-list',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    PageHeaderComponent,
    SearchInputComponent,
    EmptyStateComponent,
    ModalComponent,
    IconComponent,
    FieldErrorComponent,
    AddressAutocompleteComponent,
    CapitalizePipe,
    AddressLinePipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <app-page-header
      title="Fournisseurs"
      subtitle="Distributeurs auprès desquels vous achetez vos équipements. Chaque fournisseur référence des articles à son propre prix."
    >
      <button type="button" class="btn-secondary" (click)="load()">
        <app-icon name="refresh" [size]="16" /> Actualiser
      </button>
      <button type="button" class="btn-primary" (click)="openCreate()">
        <app-icon name="plus" [size]="16" /> Nouveau fournisseur
      </button>
    </app-page-header>

    <div class="card">
      <div class="flex flex-wrap items-center justify-between gap-3 border-b border-ink-200 p-4 dark:border-ink-800">
        <app-search-input [value]="search()" (valueChange)="search.set($event)" placeholder="Rechercher un fournisseur…" />
        <p class="text-[13px] muted">{{ filtered().length }} / {{ items().length }} fournisseurs</p>
      </div>

      @if (loading()) {
        <div class="space-y-3 p-5">
          @for (i of [1, 2, 3]; track i) {
            <div class="skeleton h-12 w-full"></div>
          }
        </div>
      } @else if (filtered().length === 0) {
        <app-empty-state icon="truck" title="Aucun fournisseur" message="Ajoutez vos distributeurs pour référencer leurs tarifs.">
          <button type="button" class="btn-primary" (click)="openCreate()">
            <app-icon name="plus" [size]="16" /> Nouveau fournisseur
          </button>
        </app-empty-state>
      } @else {
        <div class="table-wrap">
          <table class="table">
            <thead>
              <tr>
                <th>Fournisseur</th>
                <th class="w-56">Contact</th>
                <th>Adresse</th>
                <th class="w-28">Références</th>
                <th class="w-28 text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              @for (s of filtered(); track s.splId) {
                <tr>
                  <td>
                    <div class="flex items-center gap-3">
                      <span
                        class="flex h-9 w-9 shrink-0 items-center justify-center rounded-lg bg-brand-50 text-brand-600 dark:bg-brand-500/10 dark:text-brand-400"
                      >
                        <app-icon name="truck" [size]="17" />
                      </span>
                      <div class="min-w-0">
                        <p class="truncate font-medium">{{ s.splName | capitalize }}</p>
                        <p class="truncate text-[12.5px] muted">#{{ s.splId }}</p>
                      </div>
                    </div>
                  </td>
                  <td>
                    <p class="truncate text-[13px]">{{ s.splEmail }}</p>
                    <p class="num truncate text-[13px] muted">{{ s.splPhone }}</p>
                  </td>
                  <td class="text-[13px]">{{ s.address | addressLine }}</td>
                  <td><span class="badge-neutral num">{{ refCount(s.splId) }}</span></td>
                  <td>
                    <div class="flex justify-end gap-1">
                      <button type="button" class="btn-icon" (click)="openEdit(s)" title="Modifier">
                        <app-icon name="edit" [size]="16" />
                      </button>
                      <button
                        type="button"
                        class="btn-icon hover:bg-red-50 hover:text-red-600 dark:hover:bg-red-500/10"
                        (click)="remove(s)"
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
        [title]="editing() ? 'Modifier le fournisseur' : 'Nouveau fournisseur'"
        widthClass="max-w-2xl"
        (closed)="closeModal()"
      >
        <form [formGroup]="form" class="space-y-5" id="supplier-form" (ngSubmit)="submit()">
          <div class="grid gap-4 sm:grid-cols-2">
            <div class="sm:col-span-2">
              <label class="label" for="spl-name">Raison sociale</label>
              <input
                id="spl-name"
                type="text"
                class="input"
                [class.input-error]="invalid('name')"
                formControlName="name"
                placeholder="Daikin France"
              />
              <app-field-error [control]="form.controls.name" label="La raison sociale" [submitted]="submitted()" />
            </div>

            <div>
              <label class="label" for="spl-email">E-mail</label>
              <input
                id="spl-email"
                type="email"
                class="input"
                [class.input-error]="invalid('email')"
                formControlName="email"
                placeholder="contact@daikin.fr"
              />
              <app-field-error [control]="form.controls.email" label="L’e-mail" [submitted]="submitted()" />
            </div>

            <div>
              <label class="label" for="spl-phone">Téléphone</label>
              <input
                id="spl-phone"
                type="tel"
                class="input"
                [class.input-error]="invalid('phoneNumber')"
                formControlName="phoneNumber"
                placeholder="+33 1 02 03 04 05"
              />
              <app-field-error
                [control]="form.controls.phoneNumber"
                label="Le téléphone"
                [submitted]="submitted()"
              />
            </div>
          </div>

          <div class="border-t border-ink-200 pt-5 dark:border-ink-800">
            <h3 class="mb-3 panel-title">Adresse</h3>
            <app-address-autocomplete
              #autocomplete
              [address]="editing()?.address ?? null"
              [submitted]="submitted()"
            />
          </div>
        </form>

        <div footer>
          <button type="button" class="btn-secondary" (click)="closeModal()">Annuler</button>
          <button type="submit" form="supplier-form" class="btn-primary" [disabled]="saving()">
            <app-icon name="save" [size]="16" />
            {{ editing() ? 'Enregistrer' : 'Créer' }}
          </button>
        </div>
      </app-modal>
    }
  `,
})
export class SupplierListComponent implements OnInit {
  private readonly api = inject(SupplierService);
  private readonly refApi = inject(SupplierReferenceService);
  private readonly resolver = inject(AddressResolverService);
  private readonly fb = inject(FormBuilder);
  private readonly toast = inject(ToastService);
  private readonly confirm = inject(ConfirmService);

  @ViewChild('autocomplete') autocomplete?: AddressAutocompleteComponent;

  protected readonly items = signal<Supplier[]>([]);
  protected readonly references = signal<SupplierReference[]>([]);
  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly search = signal('');
  protected readonly modalOpen = signal(false);
  protected readonly editing = signal<Supplier | null>(null);
  protected readonly submitted = signal(false);

  protected readonly form = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.maxLength(50)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(50)]],
    phoneNumber: ['', [Validators.required, phoneValidator]],
  });

  protected readonly filtered = computed(() => {
    const q = this.search().trim().toLowerCase();
    const list = this.items();
    if (!q) return list;
    return list.filter((s) =>
      [s.splName, s.splEmail, s.splPhone].filter(Boolean).join(' ').toLowerCase().includes(q),
    );
  });

  ngOnInit(): void {
    this.load();
  }

  protected invalid(name: string): boolean {
    const c = this.form.get(name);
    return !!c && c.invalid && (c.touched || c.dirty || this.submitted());
  }

  refCount(splId: number): number {
    return this.references().filter((r) => r.supplier?.splId === splId).length;
  }

  async load(): Promise<void> {
    this.loading.set(true);
    try {
      const [suppliers, refs] = await Promise.all([
        firstValueFrom(this.api.list()),
        firstValueFrom(this.refApi.list()).catch(() => [] as SupplierReference[]),
      ]);
      this.items.set(suppliers);
      this.references.set(refs);
    } catch {
      this.items.set([]);
    } finally {
      this.loading.set(false);
    }
  }

  openCreate(): void {
    this.editing.set(null);
    this.submitted.set(false);
    this.form.reset({ name: '', email: '', phoneNumber: '' });
    this.modalOpen.set(true);
  }

  openEdit(supplier: Supplier): void {
    this.editing.set(supplier);
    this.submitted.set(false);
    this.form.reset({
      name: supplier.splName,
      email: supplier.splEmail,
      phoneNumber: supplier.splPhone,
    });
    this.modalOpen.set(true);
  }

  closeModal(): void {
    this.modalOpen.set(false);
  }

  async submit(): Promise<void> {
    this.submitted.set(true);
    const ac = this.autocomplete;

    if (this.form.invalid || !ac?.valid) {
      this.form.markAllAsTouched();
      ac?.markAllAsTouched();
      this.toast.warning('Formulaire incomplet', 'Vérifiez les champs signalés en rouge.');
      return;
    }

    this.saving.set(true);
    const raw = this.form.getRawValue();
    const current = this.editing();

    try {
      // 1. L'adresse (et son référentiel) d'abord : le fournisseur en dépend.
      const { address } = await this.resolver.persist(ac.value, current?.address?.addId ?? null);

      // 2. Puis le fournisseur lui-même.
      const payload = {
        name: raw.name.trim(),
        email: raw.email.trim(),
        phoneNumber: toE164(raw.phoneNumber),
        addressId: address.addId,
      };

      if (current) {
        await firstValueFrom(this.api.update(current.splId, payload));
        this.toast.success('Fournisseur modifié', payload.name);
      } else {
        await firstValueFrom(this.api.create(payload));
        this.toast.success('Fournisseur créé', payload.name);
      }

      this.closeModal();
      await this.load();
    } catch (err) {
      const message = err instanceof Error ? err.message : undefined;
      if (message) this.toast.error("Échec de l'enregistrement", message);
    } finally {
      this.saving.set(false);
    }
  }

  async remove(supplier: Supplier): Promise<void> {
    const ok = await this.confirm.askDelete(`le fournisseur « ${supplier.splName} »`);
    if (!ok) return;
    try {
      await firstValueFrom(this.api.delete(supplier.splId));
      this.toast.success('Fournisseur supprimé');
      await this.load();
    } catch {
      /* déjà notifié */
    }
  }
}
