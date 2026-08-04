import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ValuedDocument } from '../../core/models/document-math';
import { DocumentStatus, statusMeta } from '../../core/models/document-status';
import { CommerceStore } from '../../core/services/commerce-store.service';
import { ConfirmService } from '../../core/services/confirm.service';
import { ToastService } from '../../core/services/toast.service';
import { CapitalizePipe, EurPipe, FrDatePipe, RefPipe } from '../../shared/pipes/format.pipes';
import { EmptyStateComponent } from '../../shared/ui/empty-state.component';
import { IconComponent } from '../../shared/ui/icon.component';
import { PageHeaderComponent } from '../../shared/ui/page-header.component';

type QueueKey = 'overdue' | 'toInvoice' | 'staleQuotes' | 'thinMargin' | 'supply';

function normRef(ref: string | null | undefined): string {
  return (ref ?? '').trim().toLowerCase();
}

/**
 * File de travail : ce qui demande une décision aujourd'hui.
 *
 * Un tableau de bord montre où l'on en est ; cet écran dit quoi faire. Les
 * files sont ordonnées par urgence financière — un impayé ancien de gros
 * montant passe avant un devis récent — et chaque ligne porte l'action qui la
 * fait disparaître de la liste.
 */
@Component({
  selector: 'app-action-queue',
  standalone: true,
  imports: [
    RouterLink,
    PageHeaderComponent,
    EmptyStateComponent,
    IconComponent,
    CapitalizePipe,
    EurPipe,
    FrDatePipe,
    RefPipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <app-page-header
      title="À traiter"
      subtitle="Les documents qui appellent une décision, du plus urgent au moins pressant."
    >
      <button type="button" class="btn-secondary" (click)="store.reload()" [disabled]="store.loading()">
        <app-icon name="refresh" [size]="16" /> Actualiser
      </button>
    </app-page-header>

    <!-- Sélecteur de file -->
    <div class="mb-5 flex flex-wrap gap-2">
      @for (q of queues(); track q.key) {
        <button
          type="button"
          class="flex items-center gap-2 rounded-lg border px-3.5 py-2 text-sm font-medium transition-colors"
          [class]="active() === q.key ? chipActive : chipIdle"
          (click)="active.set(q.key)"
        >
          <span [class]="active() === q.key ? '' : q.tone"><app-icon [name]="q.icon" [size]="15" /></span>
          {{ q.label }}
          <span
            class="num rounded-full px-1.5 text-[11.5px]"
            [class]="active() === q.key ? 'bg-white/20' : 'bg-ink-100 dark:bg-ink-800'"
            >{{ q.documents.length }}</span
          >
        </button>
      }
    </div>

    @if (store.loading()) {
      <div class="space-y-3">
        @for (i of [1, 2, 3]; track i) {
          <div class="skeleton h-20 w-full"></div>
        }
      </div>
    } @else if (current(); as queue) {
      <div class="card">
        <div class="flex flex-wrap items-start justify-between gap-3 border-b border-ink-200 p-5 dark:border-ink-800">
          <div>
            <h2 class="panel-title">{{ queue.label }}</h2>
            <p class="mt-0.5 max-w-2xl text-[13px] muted">{{ queue.description }}</p>
          </div>
          <div class="flex items-center gap-3">
            @if (queue.key === 'supply' && queue.documents.length) {
              <a routerLink="/approvisionnement" class="btn-secondary btn-sm">
                <app-icon name="truck" [size]="14" /> Voir par fournisseur
              </a>
            }
            @if (queue.documents.length) {
              <div class="text-right">
                <p class="num text-lg font-semibold">{{ queue.total | eur }}</p>
                <p class="text-[12px] muted">montant cumulé</p>
              </div>
            }
          </div>
        </div>

        @if (queue.documents.length === 0) {
          <app-empty-state
            icon="checkCircle"
            [title]="queue.emptyTitle"
            [message]="queue.emptyMessage"
          />
        } @else {
          <ul class="divide-y divide-ink-100 dark:divide-ink-800">
            @for (d of queue.documents; track d.kind + ':' + d.id) {
              <li class="flex flex-wrap items-center gap-4 p-4 transition-colors hover:bg-ink-50/60 dark:hover:bg-ink-800/30">
                <span
                  class="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg"
                  [class]="severityTone(d, queue.key)"
                >
                  <app-icon [name]="queue.icon" [size]="18" />
                </span>

                <div class="min-w-0 flex-1">
                  <div class="flex flex-wrap items-baseline gap-x-2.5 gap-y-1">
                    <a
                      [routerLink]="['/documents', d.kind, d.id]"
                      class="font-mono text-[13px] font-semibold text-brand-700 hover:underline dark:text-brand-400"
                    >
                      {{ d.reference | ref }}
                    </a>
                    <span class="truncate text-[13px] font-medium">
                      @if (d.customer) {
                        {{ d.customer.ctmFirstName | capitalize }} {{ d.customer.ctmLastName | capitalize }}
                      } @else {
                        <span class="italic muted">Client non identifié</span>
                      }
                    </span>
                  </div>
                  <p class="mt-1 flex flex-wrap items-center gap-x-3 gap-y-0.5 text-[12.5px] muted">
                    <span class="inline-flex items-center gap-1">
                      <app-icon name="clock" [size]="12" />
                      {{ urgencyLabel(d, queue.key) }}
                    </span>
                    <span>{{ d.date | frDate }}</span>
                    @if (d.customer?.ctmEmail) {
                      <span class="truncate">{{ d.customer?.ctmEmail }}</span>
                    }
                  </p>
                </div>

                <div class="shrink-0 text-right">
                  <p class="num font-semibold">{{ d.totals.totalTtc | eur }}</p>
                  @if (d.totals.marginRate !== null) {
                    <p class="num text-[12px]" [class]="marginClass(d.totals.marginRate)">
                      marge {{ pct(d.totals.marginRate) }}
                    </p>
                  }
                </div>

                <div class="flex shrink-0 gap-2">
                  @if (queue.action; as action) {
                    <button
                      type="button"
                      class="btn-primary btn-sm"
                      [disabled]="busy().has(d.kind + ':' + d.id)"
                      (click)="apply(d, action.target)"
                    >
                      <app-icon [name]="action.icon" [size]="14" />
                      {{ action.label }}
                    </button>
                  }
                  <a [routerLink]="['/documents', d.kind, d.id]" class="btn-secondary btn-sm">
                    Ouvrir
                  </a>
                </div>
              </li>
            }
          </ul>
        }
      </div>
    }
  `,
})
export class ActionQueueComponent implements OnInit {
  protected readonly store = inject(CommerceStore);
  private readonly toast = inject(ToastService);
  private readonly confirm = inject(ConfirmService);

  protected readonly chipActive =
    'border-brand-600 bg-brand-600 text-white dark:border-brand-500 dark:bg-brand-600';
  protected readonly chipIdle =
    'border-ink-200 bg-white text-ink-700 hover:bg-ink-50 dark:border-ink-700 dark:bg-ink-900 dark:text-ink-300 dark:hover:bg-ink-800';

  protected readonly active = signal<QueueKey>('overdue');
  /** Clés `kind:id` des documents dont une action est en cours. */
  protected readonly busy = signal<Set<string>>(new Set());

  protected readonly queues = computed(() => [
    {
      key: 'overdue' as const,
      label: 'Factures échues',
      icon: 'alert',
      tone: 'text-red-600 dark:text-red-400',
      description:
        "Factures émises depuis plus de 30 jours et toujours pas réglées. L'échéance légale de règlement est dépassée.",
      emptyTitle: 'Aucun impayé',
      emptyMessage: 'Toutes vos factures émises sont dans les délais.',
      documents: this.store.overdue(),
      total: this.store.overdue().reduce((s, d) => s + d.totals.totalTtc, 0),
      action: { target: 'PAYEE' as DocumentStatus, label: 'Marquer payée', icon: 'checkCircle' },
    },
    {
      key: 'toInvoice' as const,
      label: 'Commandes à facturer',
      icon: 'clipboard',
      tone: 'text-violet-600 dark:text-violet-400',
      description:
        'Commandes acceptées par le client dont la facture reste à émettre. Chaque jour de retard décale votre encaissement.',
      emptyTitle: 'Rien à facturer',
      emptyMessage: 'Toutes les commandes acceptées ont été facturées.',
      documents: this.store.toInvoice(),
      total: this.store.toInvoice().reduce((s, d) => s + d.totals.totalTtc, 0),
      action: { target: 'FACTURE' as DocumentStatus, label: 'Facturer', icon: 'invoice' },
    },
    {
      key: 'staleQuotes' as const,
      label: 'Devis sans réponse',
      icon: 'bell',
      tone: 'text-amber-600 dark:text-amber-400',
      description:
        "Devis transmis depuis plus de 7 jours sans retour du client. Une relance au bon moment transforme mieux qu'une remise.",
      emptyTitle: 'Aucun devis en souffrance',
      emptyMessage: 'Tous vos devis récents ont moins de sept jours.',
      documents: this.store.staleQuotes(),
      total: this.store.staleQuotes().reduce((s, d) => s + d.totals.totalTtc, 0),
      action: { target: 'COMMANDE' as DocumentStatus, label: 'Accepté', icon: 'check' },
    },
    {
      key: 'supply' as const,
      label: 'À commander',
      icon: 'truck',
      tone: 'text-heat-600 dark:text-heat-400',
      description:
        "Commandes fermes engageant du matériel absent du stock. Chaque jour compte : le client attend une date de pose.",
      emptyTitle: 'Stock suffisant',
      emptyMessage: 'Vos commandes fermes sont couvertes par le stock disponible.',
      documents: this.supplyDocuments(),
      total: this.supplyDocuments().reduce((s, d) => s + d.totals.totalTtc, 0),
      action: null,
    },
    {
      key: 'thinMargin' as const,
      label: 'Marge insuffisante',
      icon: 'target',
      tone: 'text-heat-600 dark:text-heat-400',
      description: `Documents en cours dont le taux de marge passe sous votre seuil de ${this.pct(this.store.marginTarget())}. À renégocier avant de s'engager.`,
      emptyTitle: 'Marges saines',
      emptyMessage: 'Aucun document en cours ne descend sous votre seuil de rentabilité.',
      documents: this.store.thinMargin(),
      total: this.store.thinMargin().reduce((s, d) => s + d.totals.totalTtc, 0),
      action: null,
    },
  ]);

  protected readonly current = computed(() =>
    this.queues().find((q) => q.key === this.active()) ?? null,
  );

  /** Références manquantes en stock, issues des besoins d'approvisionnement fermes. */
  private readonly shortRefs = computed(
    () => new Set(this.store.firmSupplyNeeds().map((n) => normRef(n.reference))),
  );

  /**
   * Commandes fermes dont au moins une ligne manque en stock.
   * On raisonne par document — c'est l'unité que l'utilisateur traite — plutôt
   * que par article, la vue par fournisseur étant l'affaire de l'écran
   * Approvisionnement.
   */
  private readonly supplyDocuments = computed(() => {
    const refs = this.shortRefs();
    if (refs.size === 0) return [];
    return this.store
      .of('COMMANDE')
      .filter((d) => d.totals.lines.some((l) => refs.has(normRef(l.reference))))
      .sort((a, b) => (b.ageDays ?? 0) - (a.ageDays ?? 0));
  });

  ngOnInit(): void {
    this.store.load();
  }

  protected async apply(doc: ValuedDocument, target: DocumentStatus): Promise<void> {
    const current = statusMeta(doc.status);
    if (!current.next.includes(target)) {
      this.toast.warning(
        'Transition impossible',
        `« ${current.label} » ne peut pas passer directement à « ${statusMeta(target).label} ».`,
      );
      return;
    }

    if (target === 'FACTURE') {
      const ok = await this.confirm.ask({
        title: 'Émettre la facture',
        message: `Le document ${doc.reference?.toUpperCase()} sera facturé et ses lignes figées. Confirmez-vous ?`,
        confirmLabel: 'Émettre la facture',
      });
      if (!ok) return;
    }

    const key = `${doc.kind}:${doc.id}`;
    this.busy.update((s) => new Set(s).add(key));

    try {
      let ok = false;
      if (doc.kind === 'quote' && target === 'COMMANDE') {
        ok = (await this.store.transitionToCommand(doc.id)) !== null;
      } else if (doc.kind === 'command' && target === 'FACTURE') {
        ok = (await this.store.transitionToInvoice(doc.id)) !== null;
      } else if (doc.kind === 'invoice' && target === 'PAYEE') {
        ok = await this.store.markInvoicePaid(doc.id);
      }

      if (ok) {
        this.toast.success(`Document passé au statut « ${statusMeta(target).label} »`);
      }
    } finally {
      this.busy.update((s) => {
        const next = new Set(s);
        next.delete(key);
        return next;
      });
    }
  }

  protected urgencyLabel(d: ValuedDocument, key: QueueKey): string {
    const days = d.ageDays ?? 0;
    switch (key) {
      case 'overdue':
        return `${days - 30} jour(s) de retard`;
      case 'staleQuotes':
        return `Sans réponse depuis ${days} jours`;
      case 'toInvoice':
        return days <= 1 ? 'Acceptée récemment' : `Acceptée il y a ${days} jours`;
      case 'thinMargin':
        return d.totals.marginRate === null
          ? 'Coût inconnu'
          : `Marge de ${this.pct(d.totals.marginRate)}`;
      case 'supply': {
        const count = this.shortageCount(d);
        return `${count} article(s) à commander · engagée il y a ${days} jours`;
      }
    }
  }

  protected severityTone(d: ValuedDocument, key: QueueKey): string {
    if (key === 'overdue') {
      const late = (d.ageDays ?? 0) - 30;
      return late > 30
        ? 'bg-red-100 text-red-700 dark:bg-red-500/15 dark:text-red-400'
        : 'bg-amber-100 text-amber-700 dark:bg-amber-500/15 dark:text-amber-400';
    }
    return {
      toInvoice: 'bg-violet-100 text-violet-700 dark:bg-violet-500/15 dark:text-violet-400',
      staleQuotes: 'bg-amber-100 text-amber-700 dark:bg-amber-500/15 dark:text-amber-400',
      thinMargin: 'bg-heat-100 text-heat-700 dark:bg-heat-500/15 dark:text-heat-400',
      supply: 'bg-heat-100 text-heat-700 dark:bg-heat-500/15 dark:text-heat-400',
    }[key as Exclude<QueueKey, 'overdue'>];
  }

  /** Nombre de références manquantes sur un document donné. */
  protected shortageCount(d: ValuedDocument): number {
    const refs = this.shortRefs();
    return d.totals.lines.filter((l) => refs.has(normRef(l.reference))).length;
  }

  protected pct(v: number): string {
    return `${new Intl.NumberFormat('fr-FR', { maximumFractionDigits: 0 }).format(v * 100)} %`;
  }

  protected marginClass(rate: number): string {
    if (rate >= this.store.marginTarget()) return 'text-emerald-700 dark:text-emerald-400';
    if (rate >= this.store.marginTarget() * 0.6) return 'text-amber-700 dark:text-amber-400';
    return 'text-red-700 dark:text-red-400';
  }
}
