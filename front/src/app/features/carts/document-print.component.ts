import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ArticleService, CartService, OrderLineService } from '../../core/api';
import { Article, Cart, OrderLine } from '../../core/models/api.models';
import { statusMeta } from '../../core/models/document-status';
import { AddressResolverService } from '../../core/services/address-resolver.service';
import { CapitalizePipe, EurPipe, FrDatePipe, RefPipe, TauxPctPipe } from '../../shared/pipes/format.pipes';
import { IconComponent } from '../../shared/ui/icon.component';
import { computeTotals } from './document-totals';

/**
 * Aperçu imprimable d'un devis ou d'une facture, au format A4.
 *
 * L'impression passe par la fonction native du navigateur (`window.print()`),
 * qui permet également l'export PDF sans dépendance supplémentaire.
 */
@Component({
  selector: 'app-document-print',
  standalone: true,
  imports: [RouterLink, IconComponent, CapitalizePipe, EurPipe, FrDatePipe, RefPipe, TauxPctPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <!-- Barre d'actions (masquée à l'impression) -->
    <div class="no-print mb-5 flex flex-wrap items-center justify-between gap-3">
      <a [routerLink]="['/documents', cartId()]" class="btn-ghost">
        <app-icon name="arrowLeft" [size]="16" /> Retour au document
      </a>
      <div class="flex gap-2">
        <a routerLink="/documents" class="btn-secondary">Tous les documents</a>
        <button type="button" class="btn-primary" (click)="print()" [disabled]="loading()">
          <app-icon name="print" [size]="16" /> Imprimer / PDF
        </button>
      </div>
    </div>

    @if (loading()) {
      <div class="skeleton mx-auto h-[900px] w-full max-w-3xl"></div>
    } @else if (!cart()) {
      <div class="card card-pad mx-auto max-w-3xl text-center">
        <p class="muted">Document introuvable.</p>
      </div>
    } @else {
      <article
        class="print-area mx-auto max-w-3xl rounded-xl border border-ink-200 bg-white p-10 text-ink-900 shadow-card dark:border-ink-800"
      >
        <!-- En-tête -->
        <header class="flex flex-wrap items-start justify-between gap-6 border-b-2 border-ink-900 pb-6">
          <div>
            <div class="flex items-center gap-2.5">
              <span
                class="flex h-9 w-9 items-center justify-center rounded-lg bg-brand-600 text-white"
              >
                <app-icon name="snowflake" [size]="18" />
              </span>
              <div>
                <p class="text-lg font-bold tracking-tight text-ink-950">{{ company.name }}</p>
                <p class="text-[12px] text-ink-500">{{ company.tagline }}</p>
              </div>
            </div>
            <div class="mt-3 space-y-0.5 text-[12.5px] text-ink-600">
              <p>{{ company.address }}</p>
              <p>{{ company.email }} · {{ company.phone }}</p>
              <p>SIRET {{ company.siret }} · TVA {{ company.tvaNumber }}</p>
            </div>
          </div>

          <div class="text-right">
            <p class="text-2xl font-bold uppercase tracking-tight text-ink-950">
              {{ docLabel() }}
            </p>
            <p class="mt-1 font-mono text-sm font-semibold">{{ cart()!.crtRef | ref }}</p>
            <p class="mt-2 text-[12.5px] text-ink-600">
              Émis le {{ cart()!.crtCreateDate | frDate }}
            </p>
            @if (dueDate()) {
              <p class="text-[12.5px] text-ink-600">{{ dueLabel() }} : {{ dueDate() }}</p>
            }
          </div>
        </header>

        <!-- Client -->
        <section class="mt-6 flex flex-wrap justify-between gap-6">
          <div>
            <p class="text-[11px] font-semibold uppercase tracking-wider text-ink-400">
              Adressé à
            </p>
            @if (cart()!.customer; as c) {
              <p class="mt-1.5 font-semibold">
                {{ c.ctmFirstName | capitalize }} {{ c.ctmLastName | capitalize }}
              </p>
              <p class="text-[13px] text-ink-600">{{ addressLine() }}</p>
              <p class="text-[13px] text-ink-600">{{ c.ctmEmail }}</p>
              <p class="text-[13px] text-ink-600">{{ c.ctmPhone }}</p>
              @if (c.accountType) {
                <p class="mt-1 text-[12px] text-ink-500">
                  {{ c.accountType.accTypeLibelle | capitalize }}
                </p>
              }
            } @else {
              <p class="mt-1.5 text-[13px] text-ink-500">Client non renseigné</p>
            }
          </div>

          <div class="text-right text-[12.5px] text-ink-600">
            <p><span class="text-ink-400">Statut :</span> {{ statusLabel() }}</p>
            <p><span class="text-ink-400">Lignes :</span> {{ totals().lines.length }}</p>
            <p><span class="text-ink-400">Quantité totale :</span> {{ totals().itemCount }}</p>
          </div>
        </section>

        <!-- Lignes -->
        <section class="mt-8">
          <table class="w-full border-collapse text-[13px]">
            <thead>
              <tr class="border-b border-ink-300 text-left">
                <th class="pb-2 pr-3 text-[11px] font-semibold uppercase tracking-wider text-ink-500">
                  Désignation
                </th>
                <th class="pb-2 pr-3 text-right text-[11px] font-semibold uppercase tracking-wider text-ink-500">
                  P.U. HT
                </th>
                <th class="pb-2 pr-3 text-center text-[11px] font-semibold uppercase tracking-wider text-ink-500">
                  Qté
                </th>
                <th class="pb-2 pr-3 text-right text-[11px] font-semibold uppercase tracking-wider text-ink-500">
                  TVA
                </th>
                <th class="pb-2 text-right text-[11px] font-semibold uppercase tracking-wider text-ink-500">
                  Total HT
                </th>
              </tr>
            </thead>
            <tbody>
              @for (line of totals().lines; track line.articleId) {
                <tr class="border-b border-ink-100 align-top">
                  <td class="py-2.5 pr-3">
                    <p class="font-medium">{{ line.name | capitalize }}</p>
                    <p class="font-mono text-[11px] text-ink-400">{{ line.reference | ref }}</p>
                    @if (line.description) {
                      <p class="mt-0.5 max-w-md text-[12px] text-ink-500">{{ line.description }}</p>
                    }
                  </td>
                  <td class="whitespace-nowrap py-2.5 pr-3 text-right tabular-nums">{{ line.unitHt | eur }}</td>
                  <td class="py-2.5 pr-3 text-center tabular-nums">{{ line.quantity }}</td>
                  <td class="whitespace-nowrap py-2.5 pr-3 text-right tabular-nums text-ink-500">
                    {{ line.vatRate | tauxPct }}
                  </td>
                  <td class="whitespace-nowrap py-2.5 text-right font-medium tabular-nums">{{ line.totalHt | eur }}</td>
                </tr>
              } @empty {
                <tr>
                  <td colspan="5" class="py-8 text-center text-ink-400">Aucune ligne.</td>
                </tr>
              }
            </tbody>
          </table>
        </section>

        <!-- Totaux -->
        <section class="mt-6 flex justify-end">
          <dl class="w-full max-w-xs space-y-1.5 text-[13px]">
            <div class="flex justify-between">
              <dt class="text-ink-600">Total HT</dt>
              <dd class="tabular-nums font-medium">{{ totals().totalHt | eur }}</dd>
            </div>
            @for (b of totals().buckets; track b.rate) {
              <div class="flex justify-between">
                <dt class="text-ink-600">
                  TVA {{ b.rate | tauxPct }}
                  <span class="text-[11px] text-ink-400">(base {{ b.baseHt | eur }})</span>
                </dt>
                <dd class="tabular-nums">{{ b.amount | eur }}</dd>
              </div>
            }
            <div class="flex justify-between border-t-2 border-ink-900 pt-2">
              <dt class="text-base font-bold">Total TTC</dt>
              <dd class="tabular-nums text-base font-bold">{{ totals().totalTtc | eur }}</dd>
            </div>
          </dl>
        </section>

        <!-- Mentions légales -->
        <footer class="mt-10 space-y-3 border-t border-ink-200 pt-5 text-[11.5px] leading-relaxed text-ink-500">
          @if (isQuote()) {
            <p>
              <span class="font-semibold text-ink-700">Validité :</span> ce devis est valable
              30 jours à compter de sa date d'émission. Bon pour accord, date et signature du
              client à retourner pour validation de la commande.
            </p>
            <div class="mt-6 flex justify-end">
              <div class="w-56 border-t border-ink-300 pt-1.5 text-center text-[11px] text-ink-500">
                Bon pour accord — date et signature
              </div>
            </div>
          } @else if (isInvoice()) {
            <p>
              <span class="font-semibold text-ink-700">Règlement :</span> à 30 jours à compter de la
              date de facture, par virement bancaire — IBAN {{ company.iban }}.
            </p>
            <p>
              Pénalités de retard : taux d'intérêt légal majoré de 10 points. Indemnité forfaitaire
              pour frais de recouvrement : 40 € (art. L441-10 et D441-5 du Code de commerce).
              Pas d'escompte pour paiement anticipé.
            </p>
          }
          <p>
            {{ company.name }} — SIRET {{ company.siret }} — TVA intracommunautaire
            {{ company.tvaNumber }}.
          </p>
        </footer>
      </article>
    }
  `,
})
export class DocumentPrintComponent implements OnInit {
  private readonly cartApi = inject(CartService);
  private readonly lineApi = inject(OrderLineService);
  private readonly articleApi = inject(ArticleService);
  private readonly route = inject(ActivatedRoute);

  protected readonly company = environment.company;
  protected readonly cart = signal<Cart | null>(null);
  protected readonly loading = signal(true);
  protected readonly cartId = signal<number | null>(null);
  private readonly orderLines = signal<OrderLine[]>([]);
  private readonly articles = signal<Article[]>([]);

  protected readonly totals = computed(() =>
    computeTotals(this.orderLines(), new Map(this.articles().map((a) => [a.artId, a]))),
  );

  protected readonly isQuote = computed(
    () => statusMeta(this.cart()?.crtStatus).value === 'DEVIS',
  );

  protected readonly isInvoice = computed(() =>
    ['FACTURE', 'PAYEE'].includes(statusMeta(this.cart()?.crtStatus).value),
  );

  async ngOnInit(): Promise<void> {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.cartId.set(id);
    if (!id) {
      this.loading.set(false);
      return;
    }

    this.loading.set(true);
    try {
      const [cart, lines, articles] = await Promise.all([
        firstValueFrom(this.cartApi.getById(id)),
        firstValueFrom(this.lineApi.listByCart(id)).catch(() => [] as OrderLine[]),
        firstValueFrom(this.articleApi.list()).catch(() => [] as Article[]),
      ]);
      this.cart.set(cart);
      this.orderLines.set(lines);
      this.articles.set(articles);
    } catch {
      this.cart.set(null);
    } finally {
      this.loading.set(false);
    }
  }

  docLabel(): string {
    return statusMeta(this.cart()?.crtStatus).docLabel;
  }

  statusLabel(): string {
    return statusMeta(this.cart()?.crtStatus).label;
  }

  addressLine(): string {
    return AddressResolverService.format(this.cart()?.customer?.address);
  }

  dueLabel(): string {
    return this.isQuote() ? "Valable jusqu'au" : 'Échéance';
  }

  /** Devis : validité 30 jours. Facture : règlement à 30 jours. */
  dueDate(): string | null {
    const cart = this.cart();
    if (!cart || (!this.isQuote() && !this.isInvoice())) return null;
    const base = cart.crtCreateDate ? new Date(cart.crtCreateDate) : new Date();
    if (Number.isNaN(base.getTime())) return null;
    base.setDate(base.getDate() + 30);
    return base.toLocaleDateString('fr-FR', { day: '2-digit', month: 'short', year: 'numeric' });
  }

  print(): void {
    window.print();
  }
}
