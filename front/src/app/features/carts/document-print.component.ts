import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { environment } from '../../../environments/environment';
import { DocumentKind, statusMeta } from '../../core/models/document-status';
import { ValuedDocument } from '../../core/models/document-math';
import { CommerceStore } from '../../core/services/commerce-store.service';
import { AddressResolverService } from '../../core/services/address-resolver.service';
import { ConfirmService } from '../../core/services/confirm.service';
import { ToastService } from '../../core/services/toast.service';
import { CapitalizePipe, EurPipe, FrDatePipe, RefPipe, TauxPctPipe } from '../../shared/pipes/format.pipes';
import { BackLinkComponent } from '../../shared/ui/back-link.component';
import { IconComponent } from '../../shared/ui/icon.component';

/**
 * Aperçu imprimable — et écran de détail — d'un document commercial.
 *
 * Sert les quatre natures de document (`Cart`, `Quote`, `Command`,
 * `Invoice`). Seul le panier reste éditable en ligne (`cart-editor`) : les
 * trois autres n'ont plus de contenu modifiable une fois créés (lignes
 * figées côté serveur, ou pas de lignes propres du tout pour `Command`).
 * C'est donc ici, plutôt que dans un éditeur dédié à chacun, que se trouvent
 * les actions de transition (valider la commande, facturer, marquer payée)
 * quand le document le permet.
 *
 * L'impression passe par la fonction native du navigateur (`window.print()`),
 * qui permet également l'export PDF sans dépendance supplémentaire.
 */
@Component({
  selector: 'app-document-print',
  standalone: true,
  imports: [
    RouterLink,
    IconComponent,
    BackLinkComponent,
    CapitalizePipe,
    EurPipe,
    FrDatePipe,
    RefPipe,
    TauxPctPipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <!-- Barre d'actions (masquée à l'impression) -->
    <div class="no-print mb-5 flex flex-wrap items-center justify-between gap-3">
      <app-back-link fallbackUrl="/documents" fallbackLabel="aux documents" />
      <div class="flex flex-wrap gap-2">
        @if (kind() === 'cart') {
          <a [routerLink]="['/documents', 'cart', id()]" class="btn-secondary">
            <app-icon name="edit" [size]="15" /> Modifier
          </a>
        }
        @if (doc()?.status === 'DEVIS') {
          <button type="button" class="btn-secondary" (click)="toCommand()" [disabled]="acting()">
            <app-icon name="clipboard" [size]="15" /> Valider la commande
          </button>
        }
        @if (doc()?.status === 'COMMANDE') {
          <button type="button" class="btn-secondary" (click)="toInvoice()" [disabled]="acting()">
            <app-icon name="invoice" [size]="15" /> Facturer
          </button>
        }
        @if (doc()?.status === 'FACTURE') {
          <button type="button" class="btn-secondary" (click)="markPaid()" [disabled]="acting()">
            <app-icon name="checkCircle" [size]="15" /> Marquer comme payée
          </button>
        }
        <button type="button" class="btn-primary" (click)="print()" [disabled]="loading()">
          <app-icon name="print" [size]="16" /> Imprimer / PDF
        </button>
      </div>
    </div>

    @if (loading()) {
      <div class="skeleton mx-auto h-[900px] w-full max-w-3xl"></div>
    } @else if (!doc()) {
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
            <p class="mt-1 font-mono text-sm font-semibold">{{ doc()!.reference | ref }}</p>
            <p class="mt-2 text-[12.5px] text-ink-600">Émis le {{ doc()!.date | frDate }}</p>
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
            @if (doc()!.customer; as c) {
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
              <p class="mt-1.5 text-[13px] text-ink-500">
                Client non identifiable pour ce document
                @if (kind() === 'invoice' || kind() === 'command') {
                  — le backend ne relie pas encore cette étape au client d'origine.
                }
              </p>
            }
          </div>

          <div class="text-right text-[12.5px] text-ink-600">
            <p><span class="text-ink-400">Statut :</span> {{ statusLabel() }}</p>
            <p><span class="text-ink-400">Lignes :</span> {{ doc()!.totals.lines.length }}</p>
            <p><span class="text-ink-400">Quantité totale :</span> {{ doc()!.totals.itemCount }}</p>
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
              @for (line of doc()!.totals.lines; track line.reference) {
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
              <dd class="tabular-nums font-medium">{{ doc()!.totals.totalHt | eur }}</dd>
            </div>
            @for (b of doc()!.totals.buckets; track b.rate) {
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
              <dd class="tabular-nums text-base font-bold">{{ doc()!.totals.totalTtc | eur }}</dd>
            </div>
          </dl>
        </section>

        <!-- Mentions légales -->
        <footer class="mt-10 space-y-3 border-t border-ink-200 pt-5 text-[11.5px] leading-relaxed text-ink-500">
          @if (isOrder()) {
            <p>
              <span class="font-semibold text-ink-700">Commande ferme :</span> ce bon de commande
              vaut acceptation du devis correspondant et engagement des deux parties. La date
              d'intervention sera convenue par téléphone sous 5 jours ouvrés.
            </p>
            <p>
              Un acompte de 30 % du montant TTC peut être demandé à la commande, le solde étant
              exigible à la réception des travaux.
            </p>
          } @else if (isQuote()) {
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
  private readonly store = inject(CommerceStore);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly toast = inject(ToastService);
  private readonly confirm = inject(ConfirmService);

  protected readonly company = environment.company;
  protected readonly loading = signal(true);
  protected readonly acting = signal(false);
  protected readonly kind = signal<DocumentKind>('cart');
  protected readonly id = signal<number>(0);

  protected readonly doc = computed<ValuedDocument | undefined>(() =>
    this.store.find(this.kind(), this.id()),
  );

  protected readonly isQuote = computed(() => this.doc()?.status === 'DEVIS');
  protected readonly isOrder = computed(() => this.doc()?.status === 'COMMANDE');
  protected readonly isInvoice = computed(() =>
    ['FACTURE', 'PAYEE'].includes(this.doc()?.status ?? ''),
  );

  async ngOnInit(): Promise<void> {
    const kindParam = (this.route.snapshot.paramMap.get('kind') ?? 'cart') as DocumentKind;
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.kind.set(kindParam);
    this.id.set(id);

    this.loading.set(true);
    try {
      await this.store.load();
    } finally {
      this.loading.set(false);
    }
  }

  docLabel(): string {
    return statusMeta(this.doc()?.status).docLabel;
  }

  statusLabel(): string {
    return statusMeta(this.doc()?.status).label;
  }

  addressLine(): string {
    return AddressResolverService.format(this.doc()?.customer?.address);
  }

  dueLabel(): string {
    if (this.isQuote()) return "Valable jusqu'au";
    if (this.isOrder()) return 'Intervention avant le';
    return 'Échéance';
  }

  /**
   * Devis : validité 30 jours. Commande : intervention à planifier sous
   * 30 jours. Facture : règlement à 30 jours.
   */
  dueDate(): string | null {
    const doc = this.doc();
    if (!doc || (!this.isQuote() && !this.isInvoice() && !this.isOrder())) return null;
    const base = doc.date ? new Date(doc.date) : new Date();
    if (Number.isNaN(base.getTime())) return null;
    base.setDate(base.getDate() + 30);
    return base.toLocaleDateString('fr-FR', { day: '2-digit', month: 'short', year: 'numeric' });
  }

  print(): void {
    window.print();
  }

  async toCommand(): Promise<void> {
    const doc = this.doc();
    if (!doc) return;
    this.acting.set(true);
    try {
      const newId = await this.store.transitionToCommand(doc.id);
      if (newId !== null) {
        this.toast.success('Commande créée', `${doc.reference?.toUpperCase()} est validé en commande.`);
        await this.router.navigate(['/documents', 'command', newId]);
      }
    } finally {
      this.acting.set(false);
    }
  }

  async toInvoice(): Promise<void> {
    const doc = this.doc();
    if (!doc) return;
    const ok = await this.confirm.ask({
      title: 'Émettre la facture',
      message: `Le document ${doc.reference?.toUpperCase()} sera facturé et ses lignes figées définitivement.`,
      confirmLabel: 'Émettre la facture',
    });
    if (!ok) return;

    this.acting.set(true);
    try {
      const newId = await this.store.transitionToInvoice(doc.id);
      if (newId !== null) {
        this.toast.success('Facture émise', `${doc.reference?.toUpperCase()} est facturé.`);
        await this.router.navigate(['/documents', 'invoice', newId]);
      }
    } finally {
      this.acting.set(false);
    }
  }

  async markPaid(): Promise<void> {
    const doc = this.doc();
    if (!doc) return;
    this.acting.set(true);
    try {
      const ok = await this.store.markInvoicePaid(doc.id);
      if (ok) this.toast.success('Facture réglée', `${doc.reference?.toUpperCase()} est marquée payée.`);
    } finally {
      this.acting.set(false);
    }
  }
}
