import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { ArticleService, CartService, CustomerService, OrderLineService } from '../../core/api';
import { Article, Cart, Customer, OrderLine } from '../../core/models/api.models';
import {
  DOCUMENT_STATUSES,
  DocumentStatus,
  normalizeStatus,
  statusMeta,
} from '../../core/models/document-status';
import { ConfirmService } from '../../core/services/confirm.service';
import { ToastService } from '../../core/services/toast.service';
import { AddressLinePipe, CapitalizePipe, EurPipe, RefPipe, TauxPctPipe } from '../../shared/pipes/format.pipes';
import { EmptyStateComponent } from '../../shared/ui/empty-state.component';
import { FieldErrorComponent } from '../../shared/ui/field-error.component';
import { IconComponent } from '../../shared/ui/icon.component';
import { PageHeaderComponent } from '../../shared/ui/page-header.component';
import { SearchInputComponent } from '../../shared/ui/search-input.component';
import { StatusBadgeComponent } from '../../shared/ui/status-badge.component';
import { DocumentLine, buildLine, buildReference, totalsOf } from './document-totals';

/** Ligne du brouillon local, avant enregistrement. */
interface DraftLine {
  articleId: number;
  quantity: number;
}

@Component({
  selector: 'app-cart-editor',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    PageHeaderComponent,
    SearchInputComponent,
    EmptyStateComponent,
    IconComponent,
    FieldErrorComponent,
    StatusBadgeComponent,
    CapitalizePipe,
    EurPipe,
    RefPipe,
    TauxPctPipe,
    AddressLinePipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <app-page-header [title]="pageTitle()" [subtitle]="pageSubtitle()">
      <a routerLink="/documents" class="btn-ghost">
        <app-icon name="arrowLeft" [size]="16" /> Retour
      </a>
      @if (cart()) {
        <a [routerLink]="['/documents', cart()!.crtId, 'impression']" class="btn-secondary">
          <app-icon name="print" [size]="16" /> Aperçu
        </a>
      }
      <button type="button" class="btn-primary" (click)="save()" [disabled]="saving()">
        <app-icon name="save" [size]="16" />
        {{ cart() ? 'Enregistrer' : 'Créer le document' }}
      </button>
    </app-page-header>

    @if (loading()) {
      <div class="grid gap-5 lg:grid-cols-3">
        <div class="skeleton h-96 lg:col-span-2"></div>
        <div class="skeleton h-96"></div>
      </div>
    } @else {
      <div class="grid gap-5 lg:grid-cols-3">
        <!-- Colonne principale : lignes -->
        <div class="space-y-5 lg:col-span-2">
          <!-- Ajout d'articles -->
          @if (editable()) {
            <div class="card">
              <div class="flex flex-wrap items-center justify-between gap-3 border-b border-ink-200 p-4 dark:border-ink-800">
                <h2 class="panel-title">Ajouter un article</h2>
                <app-search-input
                  [value]="catalogSearch()"
                  (valueChange)="catalogSearch.set($event)"
                  placeholder="Référence, nom, catégorie…"
                />
              </div>

              @if (catalogResults().length === 0) {
                <p class="px-5 py-6 text-center text-sm muted">
                  @if (catalogSearch()) {
                    Aucun article ne correspond à « {{ catalogSearch() }} ».
                  } @else {
                    Le catalogue est vide.
                    <a routerLink="/articles" class="font-medium text-brand-600 hover:underline">
                      Créer un article
                    </a>
                  }
                </p>
              } @else {
                <ul class="max-h-72 divide-y divide-ink-100 overflow-y-auto dark:divide-ink-800">
                  @for (a of catalogResults(); track a.artId) {
                    <li class="flex items-center gap-3 px-4 py-2.5">
                      <span
                        class="flex h-9 w-9 shrink-0 items-center justify-center rounded-lg"
                        [class]="iconTone(a)"
                      >
                        <app-icon [name]="iconFor(a)" [size]="17" />
                      </span>
                      <div class="min-w-0 flex-1">
                        <p class="truncate text-sm font-medium">{{ a.artName | capitalize }}</p>
                        <p class="truncate font-mono text-[12px] muted">{{ a.artReference | ref }}</p>
                      </div>
                      <div class="shrink-0 text-right">
                        <p class="num text-sm font-semibold">{{ a.artPriceTTC | eur }}</p>
                        <p class="text-[12px] muted">
                          stock&nbsp;<span class="num" [class]="a.artStock <= 0 ? 'text-red-600 dark:text-red-400' : ''">{{ a.artStock }}</span>
                        </p>
                      </div>
                      <button
                        type="button"
                        class="btn-primary btn-sm shrink-0"
                        (click)="addLine(a)"
                        [disabled]="a.artStock <= 0 && !isInDraft(a.artId)"
                      >
                        <app-icon name="plus" [size]="14" />
                        Ajouter
                      </button>
                    </li>
                  }
                </ul>
              }
            </div>
          }

          <!-- Lignes du document -->
          <div class="card">
            <div class="flex items-center justify-between border-b border-ink-200 p-4 dark:border-ink-800">
              <h2 class="panel-title">Lignes du document</h2>
              <span class="badge-neutral num">{{ lines().length }}</span>
            </div>

            @if (lines().length === 0) {
              <app-empty-state
                icon="cart"
                title="Aucune ligne"
                message="Ajoutez des articles depuis le catalogue ci-dessus."
              />
            } @else {
              <div class="table-wrap">
                <table class="table">
                  <thead>
                    <tr>
                      <th class="min-w-[150px]">Article</th>
                      <th class="w-[86px] whitespace-nowrap text-right">P.U. HT</th>
                      <th class="w-[62px] whitespace-nowrap text-right">TVA</th>
                      <th class="w-[104px] text-center">Qté</th>
                      <th class="w-[92px] whitespace-nowrap text-right">Total HT</th>
                      <th class="w-[96px] whitespace-nowrap text-right">Total TTC</th>
                      @if (editable()) {
                        <th class="w-14"></th>
                      }
                    </tr>
                  </thead>
                  <tbody>
                    @for (line of lines(); track line.articleId) {
                      <tr>
                        <td>
                          <p class="font-medium leading-snug">{{ line.name | capitalize }}</p>
                          <p class="whitespace-nowrap font-mono text-[12px] muted">{{ line.reference | ref }}</p>
                          @if (line.quantity > line.stock) {
                            <p class="mt-1 flex items-center gap-1 text-[12px] font-medium text-amber-600 dark:text-amber-400">
                              <app-icon name="alert" [size]="12" />
                              Stock insuffisant ({{ line.stock }} disponible(s))
                            </p>
                          }
                        </td>
                        <td class="num whitespace-nowrap text-right">{{ line.unitHt | eur }}</td>
                        <td class="num whitespace-nowrap text-right muted">{{ line.vatRate | tauxPct }}</td>
                        <td>
                          @if (editable()) {
                            <div class="flex items-center justify-center gap-1">
                              <button
                                type="button"
                                class="btn-icon h-7 w-7"
                                (click)="changeQuantity(line.articleId, -1)"
                                aria-label="Diminuer"
                              >
                                <app-icon name="minus" [size]="14" />
                              </button>
                              <input
                                type="number"
                                min="1"
                                class="input h-8 w-14 px-1.5 text-center num"
                                [value]="line.quantity"
                                (change)="setQuantity(line.articleId, $event)"
                              />
                              <button
                                type="button"
                                class="btn-icon h-7 w-7"
                                (click)="changeQuantity(line.articleId, 1)"
                                aria-label="Augmenter"
                              >
                                <app-icon name="plus" [size]="14" />
                              </button>
                            </div>
                          } @else {
                            <p class="num text-center">{{ line.quantity }}</p>
                          }
                        </td>
                        <td class="num whitespace-nowrap text-right">{{ line.totalHt | eur }}</td>
                        <td class="num whitespace-nowrap text-right font-semibold">{{ line.totalTtc | eur }}</td>
                        @if (editable()) {
                          <td>
                            <button
                              type="button"
                              class="btn-icon hover:bg-red-50 hover:text-red-600 dark:hover:bg-red-500/10"
                              (click)="removeLine(line.articleId)"
                              title="Retirer la ligne"
                            >
                              <app-icon name="trash" [size]="16" />
                            </button>
                          </td>
                        }
                      </tr>
                    }
                  </tbody>
                </table>
              </div>
            }
          </div>
        </div>

        <!-- Colonne latérale -->
        <div class="space-y-5">
          <!-- Informations du document -->
          <div class="card card-pad">
            <h2 class="mb-4 panel-title">Informations</h2>

            <form [formGroup]="form" class="space-y-4">
              <div>
                <label class="label" for="crt-ref">Référence</label>
                <input
                  id="crt-ref"
                  type="text"
                  class="input font-mono text-[13px]"
                  [class.input-error]="invalid('crtRef')"
                  formControlName="crtRef"
                />
                <app-field-error [control]="form.controls.crtRef" label="La référence" [submitted]="submitted()" />
              </div>

              <div>
                <label class="label" for="crt-customer">Client</label>
                <select
                  id="crt-customer"
                  class="input"
                  [class.input-error]="invalid('ctmId')"
                  formControlName="ctmId"
                >
                  <option [ngValue]="null" disabled>— Sélectionner un client —</option>
                  @for (c of customers(); track c.ctmId) {
                    <option [ngValue]="c.ctmId">
                      {{ c.ctmFirstName | capitalize }} {{ c.ctmLastName | capitalize }}
                    </option>
                  }
                </select>
                <app-field-error [control]="form.controls.ctmId" label="Le client" [submitted]="submitted()" />
                @if (customers().length === 0) {
                  <p class="hint">
                    Aucun client :
                    <a routerLink="/clients" class="font-medium text-brand-600 hover:underline">en créer un</a>.
                  </p>
                }
              </div>

              @if (selectedCustomer(); as c) {
                <div class="rounded-lg bg-ink-50 p-3.5 text-[13px] dark:bg-ink-950/40">
                  <p class="font-medium">{{ c.ctmFirstName | capitalize }} {{ c.ctmLastName | capitalize }}</p>
                  <p class="muted">{{ c.ctmEmail }}</p>
                  <p class="num muted">{{ c.ctmPhone }}</p>
                  <p class="mt-1.5 muted">{{ c.address | addressLine }}</p>
                </div>
              }

              <div>
                <span class="label">Statut</span>
                <div class="flex items-center gap-2">
                  <app-status-badge [status]="status()" />
                  <span class="text-[12.5px] muted">{{ statusDescription() }}</span>
                </div>
              </div>
            </form>
          </div>

          <!-- Totaux -->
          <div class="card card-pad">
            <h2 class="mb-4 panel-title">Récapitulatif</h2>

            <dl class="space-y-2.5 text-sm">
              <div class="flex justify-between">
                <dt class="muted">Articles</dt>
                <dd class="num font-medium">{{ totals().itemCount }}</dd>
              </div>
              <div class="flex justify-between">
                <dt class="muted">Total HT</dt>
                <dd class="num font-medium">{{ totals().totalHt | eur }}</dd>
              </div>

              @for (b of totals().buckets; track b.rate) {
                <div class="flex justify-between">
                  <dt class="muted">TVA {{ b.rate | tauxPct }}</dt>
                  <dd class="num">{{ b.amount | eur }}</dd>
                </div>
              }

              <div class="flex justify-between border-t border-ink-200 pt-3 dark:border-ink-800">
                <dt class="font-semibold">Total TTC</dt>
                <dd class="num text-lg font-semibold text-brand-700 dark:text-brand-400">
                  {{ totals().totalTtc | eur }}
                </dd>
              </div>
            </dl>
          </div>

          <!-- Cycle de vie -->
          @if (cart()) {
            <div class="card card-pad">
              <h2 class="mb-1 panel-title">Cycle de vie</h2>
              <p class="mb-4 text-[13px] muted">
                Faites évoluer le document. Une facture n'est plus modifiable.
              </p>

              <div class="space-y-2">
                @for (next of nextStatuses(); track next; let first = $first) {
                  <button
                    type="button"
                    class="w-full justify-start"
                    [class]="first && next !== 'ANNULE' ? 'btn-primary' : 'btn-secondary'"
                    (click)="transition(next)"
                    [disabled]="saving()"
                  >
                    <app-icon [name]="transitionIcon(next)" [size]="16" />
                    {{ transitionLabel(next) }}
                  </button>
                }
                @if (nextStatuses().length === 0) {
                  <p class="text-[13px] muted">Aucune transition possible depuis ce statut.</p>
                }
              </div>
            </div>
          } @else {
            <div class="card card-pad">
              <p class="text-[13px] muted">
                Le document sera créé au statut <span class="font-medium">Panier</span>. Vous pourrez
                ensuite le transformer en devis puis en facture.
              </p>
            </div>
          }
        </div>
      </div>
    }
  `,
})
export class CartEditorComponent implements OnInit {
  private readonly cartApi = inject(CartService);
  private readonly lineApi = inject(OrderLineService);
  private readonly articleApi = inject(ArticleService);
  private readonly customerApi = inject(CustomerService);
  private readonly fb = inject(FormBuilder);
  private readonly toast = inject(ToastService);
  private readonly confirm = inject(ConfirmService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  protected readonly cart = signal<Cart | null>(null);
  protected readonly articles = signal<Article[]>([]);
  protected readonly customers = signal<Customer[]>([]);
  protected readonly draft = signal<DraftLine[]>([]);
  /** Lignes telles qu'enregistrées côté serveur, pour calculer le delta. */
  private readonly persistedLines = signal<OrderLine[]>([]);
  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly submitted = signal(false);
  protected readonly catalogSearch = signal('');
  protected readonly status = signal<DocumentStatus>('PANIER');

  protected readonly form = this.fb.group({
    crtRef: ['', [Validators.required, Validators.maxLength(50)]],
    ctmId: [null as number | null, [Validators.required]],
  });

  private readonly catalog = computed(() => new Map(this.articles().map((a) => [a.artId, a])));

  protected readonly editable = computed(() => statusMeta(this.status()).editable);

  protected readonly lines = computed<DocumentLine[]>(() => {
    const catalog = this.catalog();
    return this.draft()
      .map((d) => {
        const article = catalog.get(d.articleId);
        return article ? buildLine(article, d.quantity) : null;
      })
      .filter((l): l is DocumentLine => l !== null);
  });

  protected readonly totals = computed(() => totalsOf(this.lines()));

  protected readonly selectedCustomer = computed(() => {
    const id = this.form.controls.ctmId.value;
    return this.customers().find((c) => c.ctmId === Number(id)) ?? null;
  });

  protected readonly catalogResults = computed(() => {
    const q = this.catalogSearch().trim().toLowerCase();
    const inDraft = new Set(this.draft().map((d) => d.articleId));

    return this.articles()
      .filter((a) => {
        if (inDraft.has(a.artId)) return false;
        if (!q) return true;
        const haystack = [a.artReference, a.artName, a.artDescription]
          .concat((a.categories ?? []).map((c) => c.catName))
          .filter(Boolean)
          .join(' ')
          .toLowerCase();
        return haystack.includes(q);
      })
      .slice(0, 40);
  });

  protected readonly nextStatuses = computed(() => statusMeta(this.status()).next);

  async ngOnInit(): Promise<void> {
    const idParam = this.route.snapshot.paramMap.get('id');
    const id = idParam ? Number(idParam) : null;

    this.loading.set(true);
    try {
      const [articles, customers] = await Promise.all([
        firstValueFrom(this.articleApi.list()).catch(() => [] as Article[]),
        firstValueFrom(this.customerApi.list()).catch(() => [] as Customer[]),
      ]);
      this.articles.set(articles);
      this.customers.set(customers);

      if (id) {
        await this.loadCart(id);
      } else {
        const carts = await firstValueFrom(this.cartApi.list()).catch(() => [] as Cart[]);
        this.form.reset({
          crtRef: buildReference(DOCUMENT_STATUSES.PANIER.prefix, carts.map((c) => c.crtRef)),
          ctmId: customers[0]?.ctmId ?? null,
        });
        this.status.set('PANIER');
      }
    } finally {
      this.loading.set(false);
    }
  }

  private async loadCart(id: number): Promise<void> {
    const cart = await firstValueFrom(this.cartApi.getById(id));
    this.cart.set(cart);
    this.status.set(normalizeStatus(cart.crtStatus));
    this.form.reset({ crtRef: cart.crtRef, ctmId: cart.customer?.ctmId ?? null });

    const lines = await firstValueFrom(this.lineApi.listByCart(id)).catch(() => [] as OrderLine[]);
    this.persistedLines.set(lines);
    this.draft.set(
      lines
        .map((l) => ({
          articleId: l.article?.artId ?? l.id?.articleId ?? 0,
          quantity: l.quantity ?? 0,
        }))
        .filter((d) => d.articleId > 0),
    );
  }

  /* ---------------- Affichage ---------------- */

  pageTitle(): string {
    const cart = this.cart();
    if (!cart) return 'Nouveau document';
    return `${statusMeta(cart.crtStatus).docLabel} ${cart.crtRef?.toUpperCase()}`;
  }

  pageSubtitle(): string {
    const cart = this.cart();
    if (!cart) return 'Composez un panier, puis transformez-le en devis et en facture.';
    return statusMeta(cart.crtStatus).description;
  }

  statusDescription(): string {
    return statusMeta(this.status()).description;
  }

  transitionLabel(next: DocumentStatus): string {
    const map: Record<DocumentStatus, string> = {
      PANIER: 'Repasser en panier',
      DEVIS: 'Transformer en devis',
      FACTURE: 'Facturer',
      PAYEE: 'Marquer comme payée',
      ANNULE: 'Annuler le document',
    };
    return map[next];
  }

  transitionIcon(next: DocumentStatus): string {
    const map: Record<DocumentStatus, string> = {
      PANIER: 'arrowLeft',
      DEVIS: 'file',
      FACTURE: 'invoice',
      PAYEE: 'checkCircle',
      ANNULE: 'close',
    };
    return map[next];
  }

  iconFor(a: Article): string {
    const text = `${a.artName} ${(a.categories ?? []).map((c) => c.catName).join(' ')}`.toLowerCase();
    if (/(clim|split|réversible|reversible|froid)/.test(text)) return 'snowflake';
    if (/(chauff|radiateur|chaudi|pac|pompe|poêle|poele)/.test(text)) return 'flame';
    return 'package';
  }

  iconTone(a: Article): string {
    const icon = this.iconFor(a);
    if (icon === 'snowflake')
      return 'bg-brand-50 text-brand-600 dark:bg-brand-500/10 dark:text-brand-400';
    if (icon === 'flame') return 'bg-heat-50 text-heat-600 dark:bg-heat-500/10 dark:text-heat-400';
    return 'bg-ink-100 text-ink-500 dark:bg-ink-800 dark:text-ink-400';
  }

  protected invalid(name: string): boolean {
    const c = this.form.get(name);
    return !!c && c.invalid && (c.touched || c.dirty || this.submitted());
  }

  isInDraft(articleId: number): boolean {
    return this.draft().some((d) => d.articleId === articleId);
  }

  /* ---------------- Édition des lignes ---------------- */

  addLine(article: Article): void {
    if (this.isInDraft(article.artId)) return;
    this.draft.update((list) => [...list, { articleId: article.artId, quantity: 1 }]);
  }

  removeLine(articleId: number): void {
    this.draft.update((list) => list.filter((d) => d.articleId !== articleId));
  }

  changeQuantity(articleId: number, delta: number): void {
    this.draft.update((list) =>
      list.map((d) =>
        d.articleId === articleId ? { ...d, quantity: Math.max(1, d.quantity + delta) } : d,
      ),
    );
  }

  setQuantity(articleId: number, event: Event): void {
    const value = Math.max(1, Number((event.target as HTMLInputElement).value) || 1);
    this.draft.update((list) =>
      list.map((d) => (d.articleId === articleId ? { ...d, quantity: value } : d)),
    );
  }

  /* ---------------- Persistance ---------------- */

  async save(): Promise<void> {
    this.submitted.set(true);

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.toast.warning('Formulaire incomplet', 'Référence et client sont obligatoires.');
      return;
    }

    if (!this.editable()) {
      this.toast.warning('Document verrouillé', 'Une facture émise ne peut plus être modifiée.');
      return;
    }

    this.saving.set(true);
    const raw = this.form.getRawValue();
    const existing = this.cart();

    try {
      if (!existing) {
        // Création : le backend crée les lignes fournies dans la même requête.
        const created = await firstValueFrom(
          this.cartApi.create({
            crtRef: (raw.crtRef ?? '').trim(),
            crtStatus: this.status(),
            ctmId: Number(raw.ctmId),
            orderLines: this.draft().map((d) => ({
              cartId: 0, // ignoré par le backend lors de la création
              articleId: d.articleId,
              quantity: d.quantity,
            })),
          }),
        );
        this.toast.success('Document créé', created.crtRef?.toUpperCase());
        await this.router.navigate(['/documents', created.crtId]);
        return;
      }

      // Mise à jour : l'en-tête d'abord…
      await firstValueFrom(
        this.cartApi.update(existing.crtId, {
          crtRef: (raw.crtRef ?? '').trim(),
          crtStatus: this.status(),
          ctmId: Number(raw.ctmId),
          orderLines: [],
        }),
      );

      // …puis les lignes, que `PUT /cart/{id}` ne prend pas en charge.
      await this.syncLines(existing.crtId);

      this.toast.success('Document enregistré');
      await this.loadCart(existing.crtId);
    } catch {
      /* déjà notifié par l'intercepteur */
    } finally {
      this.saving.set(false);
    }
  }

  /**
   * Réconcilie les lignes locales avec celles du serveur : créations,
   * modifications de quantité et suppressions.
   */
  private async syncLines(cartId: number): Promise<void> {
    const before = new Map(
      this.persistedLines()
        .map((l) => [l.article?.artId ?? l.id?.articleId ?? 0, l.quantity ?? 0] as const)
        .filter(([id]) => id > 0),
    );
    const after = new Map(this.draft().map((d) => [d.articleId, d.quantity] as const));

    const tasks: Promise<unknown>[] = [];

    for (const [articleId, quantity] of after) {
      const previous = before.get(articleId);
      if (previous === undefined) {
        tasks.push(firstValueFrom(this.lineApi.create({ cartId, articleId, quantity })));
      } else if (previous !== quantity) {
        tasks.push(firstValueFrom(this.lineApi.update(articleId, cartId, { quantity })));
      }
    }

    for (const [articleId] of before) {
      if (!after.has(articleId)) {
        tasks.push(firstValueFrom(this.lineApi.delete(articleId, cartId)));
      }
    }

    await Promise.all(tasks);
  }

  /** Change le statut du document et adapte sa référence au nouveau cycle. */
  async transition(next: DocumentStatus): Promise<void> {
    const existing = this.cart();
    if (!existing) return;

    const meta = DOCUMENT_STATUSES[next];

    if (next === 'FACTURE') {
      const ok = await this.confirm.ask({
        title: 'Émettre la facture',
        message:
          'Une fois facturé, le contenu du document ne sera plus modifiable. Confirmez-vous l’émission ?',
        confirmLabel: 'Émettre la facture',
      });
      if (!ok) return;
    }

    if (next === 'ANNULE') {
      const ok = await this.confirm.ask({
        title: 'Annuler le document',
        message: 'Le document sera marqué comme annulé. Vous pourrez le repasser en panier ensuite.',
        confirmLabel: 'Annuler le document',
        danger: true,
      });
      if (!ok) return;
    }

    this.saving.set(true);
    try {
      await firstValueFrom(
        this.cartApi.update(existing.crtId, {
          crtRef: this.form.controls.crtRef.value?.trim() || existing.crtRef,
          crtStatus: next,
          ctmId: Number(this.form.controls.ctmId.value ?? existing.customer?.ctmId),
          orderLines: [],
        }),
      );
      this.status.set(next);
      this.toast.success(`Document passé au statut « ${meta.label} »`);
      await this.loadCart(existing.crtId);
    } catch {
      /* déjà notifié */
    } finally {
      this.saving.set(false);
    }
  }
}
