import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  ViewChild,
  computed,
  inject,
  signal,
} from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { AddressService } from '../../core/api';
import { Address } from '../../core/models/api.models';
import {
  AddressResolverService,
  FlatAddress,
  PersistLog,
} from '../../core/services/address-resolver.service';
import { ConfirmService } from '../../core/services/confirm.service';
import { ToastService } from '../../core/services/toast.service';
import { CapitalizePipe } from '../../shared/pipes/format.pipes';
import { AddressAutocompleteComponent } from '../../shared/ui/address-autocomplete.component';
import { EmptyStateComponent } from '../../shared/ui/empty-state.component';
import { IconComponent } from '../../shared/ui/icon.component';
import { ModalComponent } from '../../shared/ui/modal.component';
import { PageHeaderComponent } from '../../shared/ui/page-header.component';
import { SearchInputComponent } from '../../shared/ui/search-input.component';

@Component({
  selector: 'app-address-list',
  standalone: true,
  imports: [
    PageHeaderComponent,
    SearchInputComponent,
    EmptyStateComponent,
    ModalComponent,
    IconComponent,
    AddressAutocompleteComponent,
    CapitalizePipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <app-page-header
      title="Adresses"
      subtitle="Adresses partagées par les clients, fournisseurs et fabricants. La saisie est assistée par la Base Adresse Nationale."
    >
      <button type="button" class="btn-secondary" (click)="load()">
        <app-icon name="refresh" [size]="16" /> Actualiser
      </button>
      <button type="button" class="btn-primary" (click)="openCreate()">
        <app-icon name="plus" [size]="16" /> Nouvelle adresse
      </button>
    </app-page-header>

    <div class="card">
      <div class="flex flex-wrap items-center justify-between gap-3 border-b border-ink-200 p-4 dark:border-ink-800">
        <app-search-input [value]="search()" (valueChange)="search.set($event)" placeholder="Rechercher une adresse…" />
        <p class="text-[13px] muted">{{ filtered().length }} / {{ items().length }} adresses</p>
      </div>

      @if (loading()) {
        <div class="space-y-3 p-5">
          @for (i of [1, 2, 3]; track i) {
            <div class="skeleton h-10 w-full"></div>
          }
        </div>
      } @else if (filtered().length === 0) {
        <app-empty-state icon="mapPin" title="Aucune adresse" message="Créez une adresse : la recherche assistée remplit tous les champs.">
          <button type="button" class="btn-primary" (click)="openCreate()">
            <app-icon name="plus" [size]="16" /> Nouvelle adresse
          </button>
        </app-empty-state>
      } @else {
        <div class="table-wrap">
          <table class="table">
            <thead>
              <tr>
                <th class="w-20">ID</th>
                <th class="w-24">N°</th>
                <th>Voie</th>
                <th class="w-40">Complément</th>
                <th class="w-28">Code postal</th>
                <th class="w-44">Ville</th>
                <th class="w-28 text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              @for (a of filtered(); track a.addId) {
                <tr>
                  <td class="num muted">#{{ a.addId }}</td>
                  <td class="num">{{ a.addNumber || '—' }}</td>
                  <td class="font-medium">{{ a.addStreet | capitalize }}</td>
                  <td class="muted">{{ a.addComplement || '—' }}</td>
                  <td class="num">{{ a.postalCode?.pCodeName || '—' }}</td>
                  <td>
                    <span class="badge-neutral">{{ a.city?.cityName | capitalize }}</span>
                  </td>
                  <td>
                    <div class="flex justify-end gap-1">
                      <button type="button" class="btn-icon" (click)="openEdit(a)" title="Modifier">
                        <app-icon name="edit" [size]="16" />
                      </button>
                      <button
                        type="button"
                        class="btn-icon hover:bg-red-50 hover:text-red-600 dark:hover:bg-red-500/10"
                        (click)="remove(a)"
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
        [title]="editing() ? 'Modifier l’adresse' : 'Nouvelle adresse'"
        subtitle="Les référentiels manquants (pays, code postal, ville, association) sont créés automatiquement."
        widthClass="max-w-2xl"
        (closed)="closeModal()"
      >
        <app-address-autocomplete
          #autocomplete
          [address]="editing()"
          [submitted]="submitted()"
          (valueChange)="draft.set($event)"
        />

        @if (log().length) {
          <div class="mt-5 rounded-lg border border-ink-200 bg-ink-50 p-3.5 dark:border-ink-700 dark:bg-ink-950/40">
            <p class="mb-2 text-[12px] font-semibold uppercase tracking-wide muted">
              Opérations effectuées
            </p>
            <ul class="space-y-1">
              @for (entry of log(); track entry.step) {
                <li class="flex items-center gap-2 text-[13px]">
                  <span [class]="entry.created ? 'text-emerald-600' : 'text-ink-400'">
                    <app-icon [name]="entry.created ? 'plus' : 'check'" [size]="13" />
                  </span>
                  <span>{{ entry.step }}</span>
                  <span class="muted">{{ entry.created ? '— créé' : '— existant' }}</span>
                </li>
              }
            </ul>
          </div>
        }

        <div footer>
          <button type="button" class="btn-secondary" (click)="closeModal()">Annuler</button>
          <button type="button" class="btn-primary" (click)="submit()" [disabled]="saving()">
            <app-icon name="save" [size]="16" />
            {{ editing() ? 'Enregistrer' : 'Créer l’adresse' }}
          </button>
        </div>
      </app-modal>
    }
  `,
})
export class AddressListComponent implements OnInit {
  private readonly api = inject(AddressService);
  private readonly resolver = inject(AddressResolverService);
  private readonly toast = inject(ToastService);
  private readonly confirm = inject(ConfirmService);

  @ViewChild('autocomplete') autocomplete?: AddressAutocompleteComponent;

  protected readonly items = signal<Address[]>([]);
  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly search = signal('');
  protected readonly modalOpen = signal(false);
  protected readonly editing = signal<Address | null>(null);
  protected readonly submitted = signal(false);
  protected readonly draft = signal<FlatAddress | null>(null);
  protected readonly log = signal<PersistLog[]>([]);

  protected readonly filtered = computed(() => {
    const q = this.search().trim().toLowerCase();
    const list = this.items();
    if (!q) return list;
    return list.filter((a) =>
      [a.addNumber, a.addStreet, a.addComplement, a.postalCode?.pCodeName, a.city?.cityName]
        .filter(Boolean)
        .join(' ')
        .toLowerCase()
        .includes(q),
    );
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
    this.log.set([]);
    this.draft.set(null);
    this.modalOpen.set(true);
  }

  openEdit(address: Address): void {
    this.editing.set(address);
    this.submitted.set(false);
    this.log.set([]);
    this.draft.set(null);
    this.modalOpen.set(true);
  }

  closeModal(): void {
    this.modalOpen.set(false);
  }

  async submit(): Promise<void> {
    this.submitted.set(true);

    const ac = this.autocomplete;
    if (!ac?.valid) {
      ac?.markAllAsTouched();
      this.toast.warning('Formulaire incomplet', 'Voie, code postal et ville sont obligatoires.');
      return;
    }

    this.saving.set(true);
    try {
      const result = await this.resolver.persist(ac.value, this.editing()?.addId ?? null);
      this.log.set(result.log);
      const created = result.log.filter((l) => l.created).length;
      this.toast.success(
        this.editing() ? 'Adresse modifiée' : 'Adresse enregistrée',
        created > 1 ? `${created} enregistrements créés dans le référentiel.` : undefined,
      );
      await this.load();
      this.closeModal();
    } catch (err) {
      const message = err instanceof Error ? err.message : undefined;
      if (message) this.toast.error("Échec de l'enregistrement", message);
    } finally {
      this.saving.set(false);
    }
  }

  async remove(address: Address): Promise<void> {
    const ok = await this.confirm.askDelete(`l'adresse « ${AddressResolverService.format(address)} »`);
    if (!ok) return;
    try {
      await firstValueFrom(this.api.delete(address.addId));
      this.toast.success('Adresse supprimée');
      await this.load();
    } catch {
      /* déjà notifié */
    }
  }
}
