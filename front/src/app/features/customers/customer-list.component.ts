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
import { RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { AccountTypeService, CartService, CustomerService } from '../../core/api';
import { AccountType, Cart, Customer } from '../../core/models/api.models';
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
  selector: 'app-customer-list',
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
    AddressAutocompleteComponent,
    CapitalizePipe,
    AddressLinePipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <app-page-header
      title="Clients"
      subtitle="Particuliers et professionnels destinataires de vos devis et factures."
    >
      <button type="button" class="btn-secondary" (click)="load()">
        <app-icon name="refresh" [size]="16" /> Actualiser
      </button>
      <button type="button" class="btn-primary" (click)="openCreate()" [disabled]="accountTypes().length === 0">
        <app-icon name="plus" [size]="16" /> Nouveau client
      </button>
    </app-page-header>

    @if (!loading() && accountTypes().length === 0) {
      <div
        class="mb-4 flex items-start gap-2.5 rounded-lg border border-amber-200 bg-amber-50 px-4 py-3 text-[13px] text-amber-800 dark:border-amber-500/30 dark:bg-amber-500/10 dark:text-amber-300"
      >
        <app-icon name="alert" [size]="16" class="mt-0.5" />
        <span>
          Aucun type de compte n'existe.
          <a routerLink="/types-de-compte" class="font-medium underline">Créez-en un</a>
          avant d'ajouter des clients.
        </span>
      </div>
    }

    <div class="card">
      <div class="flex flex-wrap items-center justify-between gap-3 border-b border-ink-200 p-4 dark:border-ink-800">
        <app-search-input [value]="search()" (valueChange)="search.set($event)" placeholder="Nom, e-mail, téléphone…" />
        <p class="text-[13px] muted">{{ filtered().length }} / {{ items().length }} clients</p>
      </div>

      @if (loading()) {
        <div class="space-y-3 p-5">
          @for (i of [1, 2, 3, 4]; track i) {
            <div class="skeleton h-12 w-full"></div>
          }
        </div>
      } @else if (filtered().length === 0) {
        <app-empty-state icon="users" title="Aucun client" message="Ajoutez votre premier client pour établir un devis.">
          <button type="button" class="btn-primary" (click)="openCreate()" [disabled]="accountTypes().length === 0">
            <app-icon name="plus" [size]="16" /> Nouveau client
          </button>
        </app-empty-state>
      } @else {
        <div class="table-wrap">
          <table class="table">
            <thead>
              <tr>
                <th>Client</th>
                <th class="w-52">Contact</th>
                <th class="w-36">Type</th>
                <th>Adresse</th>
                <th class="w-28">Documents</th>
                <th class="w-28 text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              @for (c of filtered(); track c.ctmId) {
                <tr>
                  <td>
                    <div class="flex items-center gap-3">
                      <span
                        class="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-brand-100 text-[12px] font-semibold uppercase text-brand-700 dark:bg-brand-500/15 dark:text-brand-300"
                      >
                        {{ initials(c) }}
                      </span>
                      <div class="min-w-0">
                        <p class="truncate font-medium">
                          {{ c.ctmFirstName | capitalize }} {{ c.ctmLastName | capitalize }}
                        </p>
                        <p class="truncate text-[12.5px] muted">#{{ c.ctmId }}</p>
                      </div>
                    </div>
                  </td>
                  <td>
                    <p class="truncate text-[13px]">{{ c.ctmEmail }}</p>
                    <p class="num truncate text-[13px] muted">{{ c.ctmPhone }}</p>
                  </td>
                  <td>
                    @if (c.accountType) {
                      <span class="badge-brand">{{ c.accountType.accTypeLibelle | capitalize }}</span>
                    } @else {
                      <span class="text-[13px] muted">—</span>
                    }
                  </td>
                  <td class="text-[13px]">{{ c.address | addressLine }}</td>
                  <td><span class="badge-neutral num">{{ cartCount(c.ctmId) }}</span></td>
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
        [title]="editing() ? 'Modifier le client' : 'Nouveau client'"
        widthClass="max-w-2xl"
        (closed)="closeModal()"
      >
        <form [formGroup]="form" class="space-y-5" id="customer-form" (ngSubmit)="submit()">
          <div class="grid gap-4 sm:grid-cols-2">
            <div>
              <label class="label" for="ctm-first">Prénom</label>
              <input
                id="ctm-first"
                type="text"
                class="input"
                [class.input-error]="invalid('ctmFirstName')"
                formControlName="ctmFirstName"
                placeholder="Camille"
              />
              <app-field-error [control]="form.controls.ctmFirstName" label="Le prénom" [submitted]="submitted()" />
            </div>

            <div>
              <label class="label" for="ctm-last">Nom</label>
              <input
                id="ctm-last"
                type="text"
                class="input"
                [class.input-error]="invalid('ctmLastName')"
                formControlName="ctmLastName"
                placeholder="Durand"
              />
              <app-field-error [control]="form.controls.ctmLastName" label="Le nom" [submitted]="submitted()" />
            </div>

            <div>
              <label class="label" for="ctm-email">E-mail</label>
              <input
                id="ctm-email"
                type="email"
                class="input"
                [class.input-error]="invalid('ctmEmail')"
                formControlName="ctmEmail"
                placeholder="camille.durand@exemple.fr"
              />
              <app-field-error [control]="form.controls.ctmEmail" label="L’e-mail" [submitted]="submitted()" />
            </div>

            <div>
              <label class="label" for="ctm-phone">Téléphone</label>
              <input
                id="ctm-phone"
                type="tel"
                class="input"
                [class.input-error]="invalid('ctmPhone')"
                formControlName="ctmPhone"
                placeholder="06 12 34 56 78"
              />
              <app-field-error [control]="form.controls.ctmPhone" label="Le téléphone" [submitted]="submitted()" />
            </div>

            <div class="sm:col-span-2">
              <label class="label" for="ctm-type">Type de compte</label>
              <select
                id="ctm-type"
                class="input"
                [class.input-error]="invalid('accTypeId')"
                formControlName="accTypeId"
              >
                <option [ngValue]="null" disabled>— Sélectionner —</option>
                @for (t of accountTypes(); track t.accTypeId) {
                  <option [ngValue]="t.accTypeId">{{ t.accTypeLibelle | capitalize }}</option>
                }
              </select>
              <app-field-error [control]="form.controls.accTypeId" label="Le type de compte" [submitted]="submitted()" />
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

          @if (linkableCustomers().length > 0) {
            <div class="border-t border-ink-200 pt-5 dark:border-ink-800">
              <h3 class="panel-title">Comptes rattachés</h3>
              <p class="mb-3 mt-1 text-[13px] muted">
                Relation « Own » du MCD : un client peut piloter les comptes d'autres clients
                (siège social, gestionnaire de copropriété…).
              </p>
              <div
                class="max-h-40 space-y-1.5 overflow-y-auto rounded-lg border border-ink-200 p-3 dark:border-ink-700"
              >
                @for (c of linkableCustomers(); track c.ctmId) {
                  <label class="flex cursor-pointer items-center gap-2.5 text-sm">
                    <input
                      type="checkbox"
                      class="h-4 w-4 rounded border-ink-300 text-brand-600 focus:ring-brand-500 dark:border-ink-600 dark:bg-ink-800"
                      [checked]="linked().includes(c.ctmId)"
                      (change)="toggleLink(c.ctmId)"
                    />
                    <span>{{ c.ctmFirstName | capitalize }} {{ c.ctmLastName | capitalize }}</span>
                    <span class="text-[12.5px] muted">{{ c.ctmEmail }}</span>
                  </label>
                }
              </div>
            </div>
          }
        </form>

        <div footer>
          <button type="button" class="btn-secondary" (click)="closeModal()">Annuler</button>
          <button type="submit" form="customer-form" class="btn-primary" [disabled]="saving()">
            <app-icon name="save" [size]="16" />
            {{ editing() ? 'Enregistrer' : 'Créer' }}
          </button>
        </div>
      </app-modal>
    }
  `,
})
export class CustomerListComponent implements OnInit {
  private readonly api = inject(CustomerService);
  private readonly accountTypeApi = inject(AccountTypeService);
  private readonly cartApi = inject(CartService);
  private readonly resolver = inject(AddressResolverService);
  private readonly fb = inject(FormBuilder);
  private readonly toast = inject(ToastService);
  private readonly confirm = inject(ConfirmService);

  @ViewChild('autocomplete') autocomplete?: AddressAutocompleteComponent;

  protected readonly items = signal<Customer[]>([]);
  protected readonly accountTypes = signal<AccountType[]>([]);
  protected readonly carts = signal<Cart[]>([]);
  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly search = signal('');
  protected readonly modalOpen = signal(false);
  protected readonly editing = signal<Customer | null>(null);
  protected readonly submitted = signal(false);
  protected readonly linked = signal<number[]>([]);

  protected readonly form = this.fb.group({
    ctmFirstName: ['', [Validators.required, Validators.maxLength(60)]],
    ctmLastName: ['', [Validators.required, Validators.maxLength(60)]],
    ctmEmail: ['', [Validators.required, Validators.email]],
    ctmPhone: ['', [Validators.required, phoneValidator]],
    accTypeId: [null as number | null, [Validators.required]],
  });

  protected readonly filtered = computed(() => {
    const q = this.search().trim().toLowerCase();
    const list = this.items();
    if (!q) return list;
    return list.filter((c) =>
      [c.ctmFirstName, c.ctmLastName, c.ctmEmail, c.ctmPhone]
        .filter(Boolean)
        .join(' ')
        .toLowerCase()
        .includes(q),
    );
  });

  /** Clients pouvant être rattachés (tous sauf celui en cours d'édition). */
  protected readonly linkableCustomers = computed(() => {
    const current = this.editing();
    return this.items().filter((c) => c.ctmId !== current?.ctmId);
  });

  ngOnInit(): void {
    this.load();
  }

  protected invalid(name: string): boolean {
    const c = this.form.get(name);
    return !!c && c.invalid && (c.touched || c.dirty || this.submitted());
  }

  initials(c: Customer): string {
    return `${c.ctmFirstName?.[0] ?? ''}${c.ctmLastName?.[0] ?? ''}`;
  }

  cartCount(ctmId: number): number {
    return this.carts().filter((c) => c.customer?.ctmId === ctmId).length;
  }

  toggleLink(id: number): void {
    this.linked.update((list) =>
      list.includes(id) ? list.filter((x) => x !== id) : [...list, id],
    );
  }

  async load(): Promise<void> {
    this.loading.set(true);
    try {
      const [customers, types, carts] = await Promise.all([
        firstValueFrom(this.api.list()),
        firstValueFrom(this.accountTypeApi.list()).catch(() => [] as AccountType[]),
        firstValueFrom(this.cartApi.list()).catch(() => [] as Cart[]),
      ]);
      this.items.set(customers);
      this.accountTypes.set(types);
      this.carts.set(carts);
    } catch {
      this.items.set([]);
    } finally {
      this.loading.set(false);
    }
  }

  openCreate(): void {
    this.editing.set(null);
    this.submitted.set(false);
    this.linked.set([]);
    this.form.reset({
      ctmFirstName: '',
      ctmLastName: '',
      ctmEmail: '',
      ctmPhone: '',
      accTypeId: this.accountTypes()[0]?.accTypeId ?? null,
    });
    this.modalOpen.set(true);
  }

  openEdit(customer: Customer): void {
    this.editing.set(customer);
    this.submitted.set(false);
    this.linked.set((customer.customers ?? []).map((c) => c.ctmId));

    // Le DTO ne renvoie que le libellé du type de compte : on retrouve son id.
    const type = this.accountTypes().find(
      (t) => t.accTypeLibelle?.toLowerCase() === customer.accountType?.accTypeLibelle?.toLowerCase(),
    );

    this.form.reset({
      ctmFirstName: customer.ctmFirstName,
      ctmLastName: customer.ctmLastName,
      ctmEmail: customer.ctmEmail,
      ctmPhone: customer.ctmPhone,
      accTypeId: type?.accTypeId ?? null,
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
        ctmFirstName: (raw.ctmFirstName ?? '').trim(),
        ctmLastName: (raw.ctmLastName ?? '').trim(),
        ctmEmail: (raw.ctmEmail ?? '').trim(),
        ctmPhone: toE164(raw.ctmPhone ?? ''),
        addId: address.addId,
        accTypeId: Number(raw.accTypeId),
        customerIds: this.linked(),
      };

      if (current) {
        await firstValueFrom(this.api.update(current.ctmId, payload));
        this.toast.success('Client modifié', `${payload.ctmFirstName} ${payload.ctmLastName}`);
      } else {
        await firstValueFrom(this.api.create(payload));
        this.toast.success('Client créé', `${payload.ctmFirstName} ${payload.ctmLastName}`);
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

  async remove(customer: Customer): Promise<void> {
    const docs = this.cartCount(customer.ctmId);
    if (docs > 0) {
      this.toast.warning(
        'Suppression impossible',
        `${docs} document(s) commercial(aux) référencent encore ce client.`,
      );
      return;
    }
    const ok = await this.confirm.askDelete(
      `le client « ${customer.ctmFirstName} ${customer.ctmLastName} »`,
    );
    if (!ok) return;
    try {
      await firstValueFrom(this.api.delete(customer.ctmId));
      this.toast.success('Client supprimé');
      await this.load();
    } catch {
      /* déjà notifié */
    }
  }
}
