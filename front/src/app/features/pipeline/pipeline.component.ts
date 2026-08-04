import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DocumentStatus, statusMeta, transitionLabel } from '../../core/models/document-status';
import { ValuedDocument } from '../../core/models/document-math';
import { CommerceStore } from '../../core/services/commerce-store.service';
import { ConfirmService } from '../../core/services/confirm.service';
import { ToastService } from '../../core/services/toast.service';
import { CapitalizePipe, EurPipe, RefPipe } from '../../shared/pipes/format.pipes';
import { IconComponent } from '../../shared/ui/icon.component';
import { PageHeaderComponent } from '../../shared/ui/page-header.component';
import { SearchInputComponent } from '../../shared/ui/search-input.component';

/**
 * Pipeline commercial en colonnes.
 *
 * Le glisser-déposer utilise l'API HTML5 native — aucune dépendance. Il est
 * doublé d'un chemin clavier complet : sur une carte focalisée, les flèches
 * gauche/droite déplacent le document d'une étape.
 *
 * Depuis l'introduction de `Quote` / `Command` / `Invoice`, avancer un
 * document n'est plus un simple changement de champ (`PATCH /cart`) : c'est
 * la création d'une entité distincte, avec son propre id. Il n'y a donc plus
 * de mise à jour optimiste locale — la carte ne bouge qu'une fois l'appel
 * réseau confirmé, puis le magasin est rechargé.
 */
@Component({
  selector: 'app-pipeline',
  standalone: true,
  imports: [
    RouterLink,
    PageHeaderComponent,
    SearchInputComponent,
    IconComponent,
    CapitalizePipe,
    EurPipe,
    RefPipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <app-page-header
      title="Pipeline commercial"
      subtitle="Faites glisser un document pour le faire avancer. Au clavier : flèches gauche et droite sur une carte."
    >
      <app-search-input
        [value]="search()"
        (valueChange)="search.set($event)"
        placeholder="Référence ou client…"
      />
      <button type="button" class="btn-secondary" (click)="store.reload()" [disabled]="store.loading()">
        <app-icon name="refresh" [size]="16" /> Actualiser
      </button>
      <a routerLink="/documents/nouveau" class="btn-primary">
        <app-icon name="plus" [size]="16" /> Nouveau
      </a>
    </app-page-header>

    @if (store.loading()) {
      <div class="grid gap-4 lg:grid-cols-5">
        @for (i of [1, 2, 3, 4, 5]; track i) {
          <div class="skeleton h-80"></div>
        }
      </div>
    } @else {
      <div class="grid gap-4 lg:grid-cols-5">
        @for (col of columns(); track col.status) {
          <section
            class="flex max-h-[calc(100vh-15rem)] min-h-[20rem] flex-col rounded-xl border bg-ink-100/50 transition-colors dark:bg-ink-900/40"
            [class]="dropTarget() === col.status ? 'border-brand-400 bg-brand-50/60 dark:border-brand-500 dark:bg-brand-500/10' : 'border-ink-200 dark:border-ink-800'"
            (dragover)="onDragOver($event, col.status)"
            (dragleave)="onDragLeave(col.status)"
            (drop)="onDrop($event, col.status)"
            [attr.aria-label]="col.meta.label + ' — ' + col.documents.length + ' document(s)'"
          >
            <header class="flex items-center gap-2 border-b border-ink-200 px-3.5 py-3 dark:border-ink-800">
              <span [class]="col.meta.badgeClass">{{ col.meta.label }}</span>
              <span class="num ml-auto text-[12.5px] muted">{{ col.documents.length }}</span>
            </header>

            <p class="num border-b border-ink-200 px-3.5 py-2 text-[13px] font-semibold dark:border-ink-800">
              {{ col.total | eur }}
            </p>

            <div class="flex-1 space-y-2 overflow-y-auto p-2.5">
              @for (d of col.documents; track d.kind + ':' + d.id) {
                <article
                  class="group cursor-grab rounded-lg border border-ink-200 bg-white p-3 shadow-card transition-shadow hover:shadow-pop focus:outline-none focus-visible:ring-2 focus-visible:ring-brand-500 active:cursor-grabbing dark:border-ink-700 dark:bg-ink-900"
                  draggable="true"
                  tabindex="0"
                  [attr.aria-label]="cardLabel(d)"
                  (dragstart)="onDragStart($event, d)"
                  (dragend)="onDragEnd()"
                  (keydown)="onCardKeydown($event, d)"
                >
                  <div class="flex items-start justify-between gap-2">
                    <a
                      [routerLink]="['/documents', d.kind, d.id]"
                      class="font-mono text-[12.5px] font-semibold text-brand-700 hover:underline dark:text-brand-400"
                    >
                      {{ d.reference | ref }}
                    </a>
                    <div class="flex shrink-0 items-center gap-0.5">
                      <!--
                        Accès direct au document généré (devis, bon de commande
                        ou facture selon l'étape), sans passer par l'éditeur.
                      -->
                      <a
                        [routerLink]="['/documents', d.kind, d.id, 'impression']"
                        class="rounded p-1 text-ink-400 transition-colors hover:bg-ink-100 hover:text-brand-700 dark:hover:bg-ink-800 dark:hover:text-brand-400"
                        [title]="'Ouvrir le document : ' + docLabel(d)"
                        [attr.aria-label]="'Ouvrir le document : ' + docLabel(d)"
                        (click)="$event.stopPropagation()"
                      >
                        <app-icon name="eye" [size]="14" />
                      </a>
                      <span class="text-ink-300 dark:text-ink-600">
                        <app-icon name="grip" [size]="14" />
                      </span>
                    </div>
                  </div>

                  <p class="mt-1.5 truncate text-[13px] font-medium">
                    @if (d.customer) {
                      {{ d.customer.ctmFirstName | capitalize }} {{ d.customer.ctmLastName | capitalize }}
                    } @else {
                      <span class="italic muted">Client non identifié</span>
                    }
                  </p>

                  <div class="mt-2 flex items-end justify-between gap-2">
                    <span class="num text-[13px] font-semibold">{{ d.totals.totalTtc | eur }}</span>
                    @if (d.totals.marginRate !== null) {
                      <span
                        class="num text-[11.5px] font-medium"
                        [class]="marginClass(d.totals.marginRate)"
                        [title]="'Marge : ' + (d.totals.margin | eur)"
                      >
                        {{ pct(d.totals.marginRate) }}
                      </span>
                    }
                  </div>

                  <div class="mt-2 flex items-center gap-2 whitespace-nowrap text-[11.5px] muted">
                    <app-icon name="clock" [size]="11" />
                    <span>{{ ageLabel(d) }}</span>
                    @if (d.totals.lines.length) {
                      <span class="ml-auto">{{ d.totals.lines.length }} lig.</span>
                    }
                  </div>

                  <!-- Le document produit à cette étape, accessible d'un clic -->
                  <a
                    [routerLink]="['/documents', d.kind, d.id, 'impression']"
                    class="mt-2 flex items-center justify-center gap-1.5 rounded-md border border-ink-200 py-1.5 text-[11.5px] font-medium text-ink-700 transition-colors hover:border-brand-300 hover:bg-brand-50 hover:text-brand-700 dark:border-ink-700 dark:text-ink-300 dark:hover:border-brand-500/40 dark:hover:bg-brand-500/10 dark:hover:text-brand-400"
                  >
                    <app-icon [name]="col.meta.icon" [size]="12" />
                    {{ docLabel(d) }}
                  </a>

                  <!-- Actions visibles au survol et au focus clavier -->
                  <div
                    class="mt-2 flex gap-1 opacity-0 transition-opacity group-hover:opacity-100 group-focus-within:opacity-100"
                  >
                    @for (next of col.meta.next; track next) {
                      @if (next !== 'ANNULE') {
                        <button
                          type="button"
                          class="btn-secondary btn-sm flex-1 text-[11.5px]"
                          [disabled]="moving().has(d.kind + ':' + d.id)"
                          (click)="moveTo(d, next)"
                          [title]="label(next)"
                        >
                          {{ label(next) }}
                        </button>
                      }
                    }
                  </div>
                </article>
              } @empty {
                <p class="px-2 py-8 text-center text-[12.5px] muted">
                  @if (search()) {
                    Aucun résultat
                  } @else {
                    Aucun document
                  }
                </p>
              }
            </div>
          </section>
        }
      </div>

      <p class="mt-4 text-[12.5px] muted">
        Une facture émise n'est plus modifiable : le passage à l'étape
        <span class="font-medium">Facture</span> demande confirmation.
      </p>
    }
  `,
})
export class PipelineComponent implements OnInit {
  protected readonly store = inject(CommerceStore);
  private readonly toast = inject(ToastService);
  private readonly confirm = inject(ConfirmService);

  protected readonly search = signal('');
  protected readonly dragged = signal<ValuedDocument | null>(null);
  protected readonly dropTarget = signal<DocumentStatus | null>(null);
  /** Clés `kind:id` des documents dont une transition est en cours. */
  protected readonly moving = signal<Set<string>>(new Set());

  protected readonly columns = computed(() => {
    const q = this.search().trim().toLowerCase();

    return this.store.pipelineColumns().map((col) => {
      if (!q) return col;
      const documents = col.documents.filter((d) =>
        [d.reference, d.customer?.ctmFirstName, d.customer?.ctmLastName]
          .filter(Boolean)
          .join(' ')
          .toLowerCase()
          .includes(q),
      );
      return {
        ...col,
        documents,
        total: documents.reduce((s, d) => s + d.totals.totalTtc, 0),
      };
    });
  });

  ngOnInit(): void {
    this.store.load();
  }

  /* ---------------- Glisser-déposer ---------------- */

  protected onDragStart(event: DragEvent, doc: ValuedDocument): void {
    this.dragged.set(doc);
    event.dataTransfer?.setData('text/plain', `${doc.kind}:${doc.id}`);
    if (event.dataTransfer) event.dataTransfer.effectAllowed = 'move';
  }

  protected onDragEnd(): void {
    this.dragged.set(null);
    this.dropTarget.set(null);
  }

  protected onDragOver(event: DragEvent, status: DocumentStatus): void {
    const doc = this.dragged();
    if (!doc || !this.canMove(doc, status)) return;
    // Sans preventDefault, le navigateur refuse le dépôt.
    event.preventDefault();
    if (event.dataTransfer) event.dataTransfer.dropEffect = 'move';
    this.dropTarget.set(status);
  }

  protected onDragLeave(status: DocumentStatus): void {
    if (this.dropTarget() === status) this.dropTarget.set(null);
  }

  protected async onDrop(event: DragEvent, status: DocumentStatus): Promise<void> {
    event.preventDefault();
    const doc = this.dragged();
    this.dropTarget.set(null);
    this.dragged.set(null);
    if (!doc) return;
    await this.moveTo(doc, status);
  }

  /* ---------------- Clavier ---------------- */

  protected onCardKeydown(event: KeyboardEvent, doc: ValuedDocument): void {
    if (event.key !== 'ArrowRight' && event.key !== 'ArrowLeft') return;

    const order = this.store.pipelineColumns().map((c) => c.status);
    const index = order.indexOf(doc.status);
    const target = order[index + (event.key === 'ArrowRight' ? 1 : -1)];

    if (!target || !this.canMove(doc, target)) return;
    event.preventDefault();
    void this.moveTo(doc, target);
  }

  /* ---------------- Transition ---------------- */

  private canMove(doc: ValuedDocument, target: DocumentStatus): boolean {
    if (doc.status === target) return false;
    return statusMeta(doc.status).next.includes(target);
  }

  protected async moveTo(doc: ValuedDocument, target: DocumentStatus): Promise<void> {
    if (!this.canMove(doc, target)) {
      this.toast.warning(
        'Transition impossible',
        `Un document au statut « ${statusMeta(doc.status).label} » ne peut pas passer directement à « ${statusMeta(target).label} ».`,
      );
      return;
    }

    if (target === 'FACTURE') {
      const ok = await this.confirm.ask({
        title: 'Émettre la facture',
        message: `Le document ${doc.reference?.toUpperCase()} sera facturé et ses lignes figées définitivement. Confirmez-vous l'émission ?`,
        confirmLabel: 'Émettre la facture',
      });
      if (!ok) return;
    }

    if (doc.kind === 'quote' && doc.customer === null) {
      // Un devis sans client resterait un document orphelin en commande.
      this.toast.error('Client manquant', 'Ce document doit être rattaché à un client.');
      return;
    }

    const key = `${doc.kind}:${doc.id}`;
    this.moving.update((s) => new Set(s).add(key));

    try {
      let ok = false;
      switch (doc.kind) {
        case 'cart':
          ok = (await this.store.transitionToQuote(doc.id)) !== null;
          break;
        case 'quote':
          ok = (await this.store.transitionToCommand(doc.id)) !== null;
          break;
        case 'command':
          ok = (await this.store.transitionToInvoice(doc.id)) !== null;
          break;
        case 'invoice':
          ok = target === 'PAYEE' ? await this.store.markInvoicePaid(doc.id) : false;
          break;
      }

      if (ok) {
        this.toast.success(
          `${statusMeta(target).docLabel} créé${target === 'PAYEE' ? 'e' : ''}`,
          `${doc.reference?.toUpperCase()} est passé à l'étape « ${statusMeta(target).label} ».`,
        );
      }
      // L'intercepteur a déjà notifié l'erreur éventuelle ; sinon on recharge.
    } finally {
      this.moving.update((s) => {
        const next = new Set(s);
        next.delete(key);
        return next;
      });
    }
  }

  /* ---------------- Affichage ---------------- */

  protected label(next: DocumentStatus): string {
    return transitionLabel(next);
  }

  /** Nom du document produit à l'étape courante (devis, bon de commande…). */
  protected docLabel(d: ValuedDocument): string {
    return statusMeta(d.status).docLabel;
  }

  protected cardLabel(d: ValuedDocument): string {
    return `${d.reference?.toUpperCase()}, ${statusMeta(d.status).label}. Utilisez les flèches gauche et droite pour changer d'étape.`;
  }

  protected ageLabel(d: ValuedDocument): string {
    const days = d.ageDays;
    if (days === null) return 'Date inconnue';
    if (days === 0) return "Aujourd'hui";
    if (days === 1) return 'Hier';
    if (days < 30) return `${days} j`;
    if (days < 365) return `${Math.round(days / 30)} mois`;
    return `${Math.floor(days / 365)} an(s)`;
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
