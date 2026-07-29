import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { AccountTypeService, CustomerService } from '../../core/api';
import { AccountType, Customer } from '../../core/models/api.models';
import { ConfirmService } from '../../core/services/confirm.service';
import { ToastService } from '../../core/services/toast.service';
import { CapitalizePipe } from '../../shared/pipes/format.pipes';
import { EmptyStateComponent } from '../../shared/ui/empty-state.component';
import { FieldErrorComponent } from '../../shared/ui/field-error.component';
import { IconComponent } from '../../shared/ui/icon.component';
import { ModalComponent } from '../../shared/ui/modal.component';
import { PageHeaderComponent } from '../../shared/ui/page-header.component';

@Component({
  selector: 'app-account-type-list',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    PageHeaderComponent,
    EmptyStateComponent,
    ModalComponent,
    IconComponent,
    FieldErrorComponent,
    CapitalizePipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <app-page-header
      title="Types de compte"
      subtitle="Qualifient les clients : particulier, professionnel, syndic, revendeur…"
    >
      <button type="button" class="btn-secondary" (click)="load()">
        <app-icon name="refresh" [size]="16" /> Actualiser
      </button>
      <button type="button" class="btn-primary" (click)="openCreate()">
        <app-icon name="plus" [size]="16" /> Nouveau type
      </button>
    </app-page-header>

    <div class="card">
      @if (loading()) {
        <div class="space-y-3 p-5">
          @for (i of [1, 2, 3]; track i) {
            <div class="skeleton h-10 w-full"></div>
          }
        </div>
      } @else if (items().length === 0) {
        <app-empty-state
          icon="settings"
          title="Aucun type de compte"
          message="Créez au moins un type de compte avant d'enregistrer des clients."
        >
          <button type="button" class="btn-primary" (click)="openCreate()">
            <app-icon name="plus" [size]="16" /> Nouveau type
          </button>
        </app-empty-state>
      } @else {
        <div class="table-wrap">
          <table class="table">
            <thead>
              <tr>
                <th class="w-20">ID</th>
                <th>Libellé</th>
                <th class="w-40">Clients rattachés</th>
                <th class="w-28 text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              @for (t of items(); track t.accTypeId) {
                <tr>
                  <td class="num muted">#{{ t.accTypeId }}</td>
                  <td class="font-medium">{{ t.accTypeLibelle | capitalize }}</td>
                  <td>
                    <span class="badge-neutral num">{{ countFor(t) }}</span>
                  </td>
                  <td>
                    <div class="flex justify-end gap-1">
                      <button type="button" class="btn-icon" (click)="openEdit(t)" title="Modifier">
                        <app-icon name="edit" [size]="16" />
                      </button>
                      <button
                        type="button"
                        class="btn-icon hover:bg-red-50 hover:text-red-600 dark:hover:bg-red-500/10"
                        (click)="remove(t)"
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
        [title]="editing() ? 'Modifier le type de compte' : 'Nouveau type de compte'"
        widthClass="max-w-md"
        (closed)="closeModal()"
      >
        <form [formGroup]="form" (ngSubmit)="submit()" id="acct-form">
          <label class="label" for="accTypeLibelle">Libellé</label>
          <input
            id="accTypeLibelle"
            type="text"
            class="input"
            [class.input-error]="form.controls.accTypeLibelle.invalid && submitted()"
            formControlName="accTypeLibelle"
            placeholder="Professionnel"
          />
          <app-field-error
            [control]="form.controls.accTypeLibelle"
            label="Le libellé"
            [submitted]="submitted()"
          />
        </form>

        <div footer>
          <button type="button" class="btn-secondary" (click)="closeModal()">Annuler</button>
          <button type="submit" form="acct-form" class="btn-primary" [disabled]="saving()">
            <app-icon name="save" [size]="16" />
            {{ editing() ? 'Enregistrer' : 'Créer' }}
          </button>
        </div>
      </app-modal>
    }
  `,
})
export class AccountTypeListComponent implements OnInit {
  private readonly api = inject(AccountTypeService);
  private readonly customerApi = inject(CustomerService);
  private readonly fb = inject(FormBuilder);
  private readonly toast = inject(ToastService);
  private readonly confirm = inject(ConfirmService);

  protected readonly items = signal<AccountType[]>([]);
  protected readonly customers = signal<Customer[]>([]);
  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly modalOpen = signal(false);
  protected readonly editing = signal<AccountType | null>(null);
  protected readonly submitted = signal(false);

  protected readonly form = this.fb.nonNullable.group({
    accTypeLibelle: ['', [Validators.required, Validators.maxLength(60)]],
  });

  ngOnInit(): void {
    this.load();
  }

  async load(): Promise<void> {
    this.loading.set(true);
    try {
      const [types, customers] = await Promise.all([
        firstValueFrom(this.api.list()),
        firstValueFrom(this.customerApi.list()).catch(() => [] as Customer[]),
      ]);
      this.items.set(types);
      this.customers.set(customers);
    } catch {
      this.items.set([]);
    } finally {
      this.loading.set(false);
    }
  }

  /**
   * Le DTO client n'expose que le libellé du type de compte (pas son id) :
   * le rapprochement se fait donc sur le libellé.
   */
  countFor(type: AccountType): number {
    const label = type.accTypeLibelle?.toLowerCase();
    return this.customers().filter((c) => c.accountType?.accTypeLibelle?.toLowerCase() === label)
      .length;
  }

  openCreate(): void {
    this.editing.set(null);
    this.submitted.set(false);
    this.form.reset({ accTypeLibelle: '' });
    this.modalOpen.set(true);
  }

  openEdit(item: AccountType): void {
    this.editing.set(item);
    this.submitted.set(false);
    this.form.reset({ accTypeLibelle: item.accTypeLibelle });
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
    const payload = { accTypeLibelle: this.form.getRawValue().accTypeLibelle.trim() };
    const current = this.editing();
    try {
      if (current) {
        await firstValueFrom(this.api.update(current.accTypeId, payload));
        this.toast.success('Type de compte modifié');
      } else {
        await firstValueFrom(this.api.create(payload));
        this.toast.success('Type de compte créé');
      }
      this.closeModal();
      await this.load();
    } catch {
      /* déjà notifié */
    } finally {
      this.saving.set(false);
    }
  }

  async remove(item: AccountType): Promise<void> {
    const ok = await this.confirm.askDelete(`le type « ${item.accTypeLibelle} »`);
    if (!ok) return;
    try {
      await firstValueFrom(this.api.delete(item.accTypeId));
      this.toast.success('Type de compte supprimé');
      await this.load();
    } catch {
      /* déjà notifié */
    }
  }
}
