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
import { MakerReferenceService, MakerService } from '../../core/api';
import { Maker, MakerReference } from '../../core/models/api.models';
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
  selector: 'app-maker-list',
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
      title="Fabricants"
      subtitle="Marques d'équipements (Daikin, Atlantic, Mitsubishi…). Un fabricant référence ses articles avec sa propre référence constructeur."
    >
      <button type="button" class="btn-secondary" (click)="load()">
        <app-icon name="refresh" [size]="16" /> Actualiser
      </button>
      <button type="button" class="btn-primary" (click)="openCreate()">
        <app-icon name="plus" [size]="16" /> Nouveau fabricant
      </button>
    </app-page-header>

    <div class="card">
      <div class="flex flex-wrap items-center justify-between gap-3 border-b border-ink-200 p-4 dark:border-ink-800">
        <app-search-input [value]="search()" (valueChange)="search.set($event)" placeholder="Rechercher un fabricant…" />
        <p class="text-[13px] muted">{{ filtered().length }} / {{ items().length }} fabricants</p>
      </div>

      @if (loading()) {
        <div class="space-y-3 p-5">
          @for (i of [1, 2, 3]; track i) {
            <div class="skeleton h-12 w-full"></div>
          }
        </div>
      } @else if (filtered().length === 0) {
        <app-empty-state icon="factory" title="Aucun fabricant" message="Renseignez les marques que vous distribuez.">
          <button type="button" class="btn-primary" (click)="openCreate()">
            <app-icon name="plus" [size]="16" /> Nouveau fabricant
          </button>
        </app-empty-state>
      } @else {
        <div class="table-wrap">
          <table class="table">
            <thead>
              <tr>
                <th>Fabricant</th>
                <th class="w-56">Contact</th>
                <th>Adresse</th>
                <th class="w-28">Références</th>
                <th class="w-28 text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              @for (m of filtered(); track m.mkrId) {
                <tr>
                  <td>
                    <div class="flex items-center gap-3">
                      <span
                        class="flex h-9 w-9 shrink-0 items-center justify-center rounded-lg bg-heat-50 text-heat-600 dark:bg-heat-500/10 dark:text-heat-400"
                      >
                        <app-icon name="factory" [size]="17" />
                      </span>
                      <div class="min-w-0">
                        <p class="truncate font-medium">{{ m.mkrName | capitalize }}</p>
                        <p class="truncate text-[12.5px] muted">#{{ m.mkrId }}</p>
                      </div>
                    </div>
                  </td>
                  <td>
                    <p class="truncate text-[13px]">{{ m.mkrEmail }}</p>
                    <p class="num truncate text-[13px] muted">{{ m.mkrPhone }}</p>
                  </td>
                  <td class="text-[13px]">{{ m.address | addressLine }}</td>
                  <td><span class="badge-neutral num">{{ refCount(m.mkrId) }}</span></td>
                  <td>
                    <div class="flex justify-end gap-1">
                      <button type="button" class="btn-icon" (click)="openEdit(m)" title="Modifier">
                        <app-icon name="edit" [size]="16" />
                      </button>
                      <button
                        type="button"
                        class="btn-icon hover:bg-red-50 hover:text-red-600 dark:hover:bg-red-500/10"
                        (click)="remove(m)"
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
        [title]="editing() ? 'Modifier le fabricant' : 'Nouveau fabricant'"
        widthClass="max-w-2xl"
        (closed)="closeModal()"
      >
        <form [formGroup]="form" class="space-y-5" id="maker-form" (ngSubmit)="submit()">
          <div class="grid gap-4 sm:grid-cols-2">
            <div class="sm:col-span-2">
              <label class="label" for="mkr-name">Nom du fabricant</label>
              <input
                id="mkr-name"
                type="text"
                class="input"
                [class.input-error]="invalid('mkrName')"
                formControlName="mkrName"
                placeholder="Atlantic"
              />
              <app-field-error [control]="form.controls.mkrName" label="Le nom" [submitted]="submitted()" />
            </div>

            <div>
              <label class="label" for="mkr-email">E-mail</label>
              <input
                id="mkr-email"
                type="email"
                class="input"
                [class.input-error]="invalid('mkrEmail')"
                formControlName="mkrEmail"
                placeholder="contact@atlantic.fr"
              />
              <app-field-error [control]="form.controls.mkrEmail" label="L’e-mail" [submitted]="submitted()" />
            </div>

            <div>
              <label class="label" for="mkr-phone">Téléphone</label>
              <input
                id="mkr-phone"
                type="tel"
                class="input"
                [class.input-error]="invalid('mkrPhone')"
                formControlName="mkrPhone"
                placeholder="+33 2 40 00 00 00"
              />
              <app-field-error [control]="form.controls.mkrPhone" label="Le téléphone" [submitted]="submitted()" />
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
          <button type="submit" form="maker-form" class="btn-primary" [disabled]="saving()">
            <app-icon name="save" [size]="16" />
            {{ editing() ? 'Enregistrer' : 'Créer' }}
          </button>
        </div>
      </app-modal>
    }
  `,
})
export class MakerListComponent implements OnInit {
  private readonly api = inject(MakerService);
  private readonly refApi = inject(MakerReferenceService);
  private readonly resolver = inject(AddressResolverService);
  private readonly fb = inject(FormBuilder);
  private readonly toast = inject(ToastService);
  private readonly confirm = inject(ConfirmService);

  @ViewChild('autocomplete') autocomplete?: AddressAutocompleteComponent;

  protected readonly items = signal<Maker[]>([]);
  protected readonly references = signal<MakerReference[]>([]);
  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly search = signal('');
  protected readonly modalOpen = signal(false);
  protected readonly editing = signal<Maker | null>(null);
  protected readonly submitted = signal(false);

  protected readonly form = this.fb.nonNullable.group({
    mkrName: ['', [Validators.required, Validators.maxLength(50)]],
    mkrEmail: ['', [Validators.required, Validators.email, Validators.maxLength(50)]],
    mkrPhone: ['', [Validators.required, phoneValidator]],
  });

  protected readonly filtered = computed(() => {
    const q = this.search().trim().toLowerCase();
    const list = this.items();
    if (!q) return list;
    return list.filter((m) =>
      [m.mkrName, m.mkrEmail, m.mkrPhone].filter(Boolean).join(' ').toLowerCase().includes(q),
    );
  });

  ngOnInit(): void {
    this.load();
  }

  protected invalid(name: string): boolean {
    const c = this.form.get(name);
    return !!c && c.invalid && (c.touched || c.dirty || this.submitted());
  }

  refCount(mkrId: number): number {
    return this.references().filter((r) => r.maker?.mkrId === mkrId).length;
  }

  async load(): Promise<void> {
    this.loading.set(true);
    try {
      const [makers, refs] = await Promise.all([
        firstValueFrom(this.api.list()),
        firstValueFrom(this.refApi.list()).catch(() => [] as MakerReference[]),
      ]);
      this.items.set(makers);
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
    this.form.reset({ mkrName: '', mkrEmail: '', mkrPhone: '' });
    this.modalOpen.set(true);
  }

  openEdit(maker: Maker): void {
    this.editing.set(maker);
    this.submitted.set(false);
    this.form.reset({
      mkrName: maker.mkrName,
      mkrEmail: maker.mkrEmail,
      mkrPhone: maker.mkrPhone,
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
      const { address } = await this.resolver.persist(ac.value, current?.address?.addId ?? null);

      const payload = {
        mkrName: raw.mkrName.trim(),
        mkrEmail: raw.mkrEmail.trim(),
        mkrPhone: toE164(raw.mkrPhone),
        addressId: address.addId,
      };

      if (current) {
        await firstValueFrom(this.api.update(current.mkrId, payload));
        this.toast.success('Fabricant modifié', payload.mkrName);
      } else {
        await firstValueFrom(this.api.create(payload));
        this.toast.success('Fabricant créé', payload.mkrName);
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

  async remove(maker: Maker): Promise<void> {
    const ok = await this.confirm.askDelete(`le fabricant « ${maker.mkrName} »`);
    if (!ok) return;
    try {
      await firstValueFrom(this.api.delete(maker.mkrId));
      this.toast.success('Fabricant supprimé');
      await this.load();
    } catch {
      /* déjà notifié */
    }
  }
}
