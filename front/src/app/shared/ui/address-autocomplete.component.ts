import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  EventEmitter,
  HostListener,
  Input,
  OnInit,
  Output,
  computed,
  inject,
  signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Subject, debounceTime, distinctUntilChanged, switchMap } from 'rxjs';
import { Address } from '../../core/models/api.models';
import {
  AddressResolverService,
  FlatAddress,
} from '../../core/services/address-resolver.service';
import { BanFeature, BanService } from '../../core/services/ban.service';
import { FieldErrorComponent } from './field-error.component';
import { IconComponent } from './icon.component';

/**
 * Saisie d'adresse assistée par l'API Adresse de l'État (Base Adresse
 * Nationale).
 *
 * L'utilisateur tape le début de son adresse ; les suggestions officielles
 * apparaissent au fil de la frappe. La sélection d'une suggestion remplit
 * automatiquement le numéro, la voie, le code postal et la ville — chaque champ
 * restant modifiable manuellement (adresses étrangères, lieux-dits, etc.).
 *
 * Le composant n'écrit rien en base : il expose un `FlatAddress` valide que le
 * formulaire parent transmet à `AddressResolverService.persist()` lors de la
 * soumission.
 */
@Component({
  selector: 'app-address-autocomplete',
  standalone: true,
  imports: [ReactiveFormsModule, IconComponent, FieldErrorComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div [formGroup]="form" class="space-y-4">
      <!-- Recherche assistée -->
      <div class="relative">
        <label class="label" for="ban-search">
          Rechercher une adresse
          <span class="ml-1 font-normal muted">— Base Adresse Nationale</span>
        </label>

        <div class="relative">
          <span class="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-ink-400">
            <app-icon name="mapPin" [size]="16" />
          </span>
          <input
            id="ban-search"
            type="text"
            class="input pl-9 pr-9"
            autocomplete="off"
            placeholder="Ex. : 12 rue de la Paix, Lyon"
            [value]="query()"
            (input)="onQuery($event)"
            (focus)="open.set(true)"
            (keydown)="onKeydown($event)"
            role="combobox"
            aria-autocomplete="list"
            [attr.aria-expanded]="open() && suggestions().length > 0"
          />
          <span class="absolute right-3 top-1/2 -translate-y-1/2 text-ink-400">
            @if (loading()) {
              <svg class="h-4 w-4 animate-spin" viewBox="0 0 24 24" fill="none">
                <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" opacity=".25" />
                <path d="M22 12a10 10 0 0 0-10-10" stroke="currentColor" stroke-width="3" stroke-linecap="round" />
              </svg>
            } @else if (query().length > 0) {
              <button type="button" class="hover:text-ink-700 dark:hover:text-ink-200" (click)="clearQuery()" aria-label="Effacer">
                <app-icon name="close" [size]="15" />
              </button>
            }
          </span>
        </div>

        <!-- Liste de suggestions -->
        @if (open() && suggestions().length > 0) {
          <ul
            class="absolute z-30 mt-1.5 max-h-72 w-full animate-fade-in overflow-y-auto rounded-lg border border-ink-200 bg-white py-1 shadow-pop dark:border-ink-700 dark:bg-ink-900"
            role="listbox"
          >
            @for (s of suggestions(); track s.properties.id; let i = $index) {
              <li
                role="option"
                [attr.aria-selected]="i === highlighted()"
                class="cursor-pointer px-3 py-2 text-sm transition-colors"
                [class.bg-brand-50]="i === highlighted()"
                [class.dark:bg-ink-800]="i === highlighted()"
                (mouseenter)="highlighted.set(i)"
                (mousedown)="$event.preventDefault(); choose(s)"
              >
                <div class="flex items-start gap-2.5">
                  <span class="mt-0.5 text-brand-500"><app-icon name="mapPin" [size]="15" /></span>
                  <div class="min-w-0">
                    <p class="truncate font-medium text-ink-900 dark:text-ink-100">
                      {{ s.properties.label }}
                    </p>
                    <p class="truncate text-[12.5px] muted">{{ s.properties.context }}</p>
                  </div>
                </div>
              </li>
            }
          </ul>
        }

        @if (open() && !loading() && query().length >= 3 && suggestions().length === 0) {
          <div
            class="absolute z-30 mt-1.5 w-full animate-fade-in rounded-lg border border-ink-200 bg-white px-3 py-2.5 text-[13px] muted shadow-pop dark:border-ink-700 dark:bg-ink-900"
          >
            Aucune adresse trouvée. Vous pouvez saisir les champs manuellement ci-dessous.
          </div>
        }

        <p class="hint">
          Service public gratuit — la sélection remplit automatiquement les champs.
        </p>
      </div>

      <!-- Champs détaillés -->
      <div class="grid gap-4 sm:grid-cols-6">
        <div class="sm:col-span-2">
          <label class="label" for="add-number">N° de voie</label>
          <input id="add-number" type="text" class="input" formControlName="number" placeholder="12" />
        </div>

        <div class="sm:col-span-4">
          <label class="label" for="add-street">Voie</label>
          <input
            id="add-street"
            type="text"
            class="input"
            [class.input-error]="invalid('street')"
            formControlName="street"
            placeholder="rue de la Paix"
          />
          <app-field-error [control]="form.controls['street']" label="La voie" [submitted]="submitted" />
        </div>

        <div class="sm:col-span-6">
          <label class="label" for="add-complement">Complément</label>
          <input
            id="add-complement"
            type="text"
            class="input"
            formControlName="complement"
            placeholder="Bâtiment B, 3ᵉ étage…"
          />
        </div>

        <div class="sm:col-span-2">
          <label class="label" for="add-postal">Code postal</label>
          <input
            id="add-postal"
            type="text"
            inputmode="numeric"
            class="input"
            [class.input-error]="invalid('postalCode')"
            formControlName="postalCode"
            placeholder="69001"
          />
          <app-field-error
            [control]="form.controls['postalCode']"
            label="Le code postal"
            [submitted]="submitted"
          />
        </div>

        <div class="sm:col-span-2">
          <label class="label" for="add-city">Ville</label>
          <input
            id="add-city"
            type="text"
            class="input"
            [class.input-error]="invalid('city')"
            formControlName="city"
            placeholder="Lyon"
          />
          <app-field-error [control]="form.controls['city']" label="La ville" [submitted]="submitted" />
        </div>

        <div class="sm:col-span-2">
          <label class="label" for="add-country">Pays</label>
          <input id="add-country" type="text" class="input" formControlName="country" placeholder="France" />
        </div>
      </div>

      @if (picked()) {
        <div
          class="flex items-start gap-2.5 rounded-lg border border-emerald-200 bg-emerald-50 px-3.5 py-2.5 text-[13px] text-emerald-800 dark:border-emerald-500/30 dark:bg-emerald-500/10 dark:text-emerald-300"
        >
          <span class="mt-0.5"><app-icon name="checkCircle" [size]="15" /></span>
          <span>
            Adresse validée par la BAN :
            <span class="font-medium">{{ picked() }}</span>
          </span>
        </div>
      }
    </div>
  `,
})
export class AddressAutocompleteComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly ban = inject(BanService);
  private readonly destroyRef = inject(DestroyRef);

  /** Adresse existante à éditer (pré-remplit les champs). */
  @Input() address: Address | null = null;
  /** Passe à `true` quand le formulaire parent est soumis. */
  @Input() submitted = false;

  /** Émis à chaque modification, valide ou non. */
  @Output() valueChange = new EventEmitter<FlatAddress>();
  /** Émis avec l'état de validité des champs adresse. */
  @Output() validityChange = new EventEmitter<boolean>();

  readonly form: FormGroup = this.fb.group({
    number: [''],
    street: ['', [Validators.required, Validators.maxLength(120)]],
    complement: [''],
    postalCode: ['', [Validators.required, Validators.pattern(/^[0-9A-Za-z\- ]{4,10}$/)]],
    city: ['', [Validators.required, Validators.maxLength(80)]],
    country: ['France', [Validators.required]],
  });

  protected readonly query = signal('');
  protected readonly suggestions = signal<BanFeature[]>([]);
  protected readonly loading = signal(false);
  protected readonly open = signal(false);
  protected readonly highlighted = signal(0);
  protected readonly picked = signal<string | null>(null);

  private readonly queries = new Subject<string>();

  ngOnInit(): void {
    if (this.address) {
      this.form.patchValue(AddressResolverService.flatten(this.address), { emitEvent: false });
      this.query.set(AddressResolverService.format(this.address));
    }

    this.queries
      .pipe(
        debounceTime(250),
        distinctUntilChanged(),
        switchMap((q) => {
          this.loading.set(true);
          return this.ban.search(q, 8);
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((features) => {
        this.loading.set(false);
        this.suggestions.set(features);
        this.highlighted.set(0);
      });

    this.form.valueChanges.pipe(takeUntilDestroyed(this.destroyRef)).subscribe(() => this.emit());

    // État initial
    queueMicrotask(() => this.emit());
  }

  /** Valeur courante, exploitable par le parent. */
  get value(): FlatAddress {
    return this.form.getRawValue() as FlatAddress;
  }

  get valid(): boolean {
    return this.form.valid;
  }

  markAllAsTouched(): void {
    this.form.markAllAsTouched();
  }

  protected invalid(name: string): boolean {
    const c = this.form.get(name);
    return !!c && c.invalid && (c.touched || c.dirty || this.submitted);
  }

  protected onQuery(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.query.set(value);
    this.picked.set(null);
    this.open.set(true);
    if (value.trim().length < 3) {
      this.suggestions.set([]);
      this.loading.set(false);
      return;
    }
    this.queries.next(value);
  }

  protected clearQuery(): void {
    this.query.set('');
    this.suggestions.set([]);
    this.picked.set(null);
  }

  protected onKeydown(event: KeyboardEvent): void {
    const list = this.suggestions();
    if (!this.open() || list.length === 0) return;

    switch (event.key) {
      case 'ArrowDown':
        event.preventDefault();
        this.highlighted.update((i) => (i + 1) % list.length);
        break;
      case 'ArrowUp':
        event.preventDefault();
        this.highlighted.update((i) => (i - 1 + list.length) % list.length);
        break;
      case 'Enter':
        event.preventDefault();
        this.choose(list[this.highlighted()]);
        break;
      case 'Escape':
        this.open.set(false);
        break;
    }
  }

  /** Applique une suggestion aux champs du formulaire. */
  protected choose(feature: BanFeature): void {
    const parts = BanService.toParts(feature);
    this.form.patchValue({
      number: parts.number,
      street: parts.street,
      postalCode: parts.postalCode,
      city: parts.city,
      country: parts.country,
    });
    this.form.markAsDirty();
    this.query.set(parts.label);
    this.picked.set(parts.label);
    this.suggestions.set([]);
    this.open.set(false);
  }

  @HostListener('document:click', ['$event'])
  protected onDocumentClick(event: MouseEvent): void {
    const host = (event.target as HTMLElement)?.closest('app-address-autocomplete');
    if (!host) this.open.set(false);
  }

  private emit(): void {
    this.valueChange.emit(this.value);
    this.validityChange.emit(this.form.valid);
  }
}
