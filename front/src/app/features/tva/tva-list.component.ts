import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { ArticleService, TvaService } from '../../core/api';
import { Article, Tva } from '../../core/models/api.models';
import { ConfirmService } from '../../core/services/confirm.service';
import { ToastService } from '../../core/services/toast.service';
import { CapitalizePipe, TauxPctPipe } from '../../shared/pipes/format.pipes';
import { EmptyStateComponent } from '../../shared/ui/empty-state.component';
import { FieldErrorComponent } from '../../shared/ui/field-error.component';
import { IconComponent } from '../../shared/ui/icon.component';
import { ModalComponent } from '../../shared/ui/modal.component';
import { PageHeaderComponent } from '../../shared/ui/page-header.component';

@Component({
  selector: 'app-tva-list',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    PageHeaderComponent,
    EmptyStateComponent,
    ModalComponent,
    IconComponent,
    FieldErrorComponent,
    CapitalizePipe,
    TauxPctPipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <app-page-header
      title="Taux de TVA"
      subtitle="Taux applicables aux articles. En CVC, la pose d'un équipement en rénovation relève souvent d'un taux réduit."
    >
      <button type="button" class="btn-secondary" (click)="load()">
        <app-icon name="refresh" [size]="16" /> Actualiser
      </button>
      <button type="button" class="btn-primary" (click)="openCreate()">
        <app-icon name="plus" [size]="16" /> Nouveau taux
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
        <app-empty-state icon="percent" title="Aucun taux de TVA" message="Créez un taux avant d'ajouter des articles.">
          <button type="button" class="btn-primary" (click)="openCreate()">
            <app-icon name="plus" [size]="16" /> Nouveau taux
          </button>
        </app-empty-state>
      } @else {
        <div class="table-wrap">
          <table class="table">
            <thead>
              <tr>
                <th class="w-20">ID</th>
                <th>Libellé</th>
                <th class="w-32 text-right">Taux</th>
                <th class="w-40">Articles</th>
                <th class="w-36 text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              @for (t of items(); track t.tvaId) {
                <tr>
                  <td class="num muted">#{{ t.tvaId }}</td>
                  <td class="font-medium">{{ t.tvaName | capitalize }}</td>
                  <td class="num text-right font-semibold">{{ t.tvaTaux | tauxPct }}</td>
                  <td><span class="badge-neutral num">{{ articleCount(t.tvaId) }}</span></td>
                  <td>
                    <div class="flex justify-end gap-1">
                      <button
                        type="button"
                        class="btn-icon"
                        (click)="openPatch(t)"
                        title="Modifier uniquement le taux (PATCH)"
                      >
                        <app-icon name="percent" [size]="16" />
                      </button>
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
        [title]="modalTitle()"
        [subtitle]="mode() === 'patch' ? 'Seul le taux sera envoyé au serveur (PATCH /tva/{id}).' : undefined"
        widthClass="max-w-md"
        (closed)="closeModal()"
      >
        <form [formGroup]="form" (ngSubmit)="submit()" id="tva-form" class="space-y-4">
          @if (mode() !== 'patch') {
            <div>
              <label class="label" for="tvaName">Libellé</label>
              <input
                id="tvaName"
                type="text"
                class="input"
                [class.input-error]="form.controls.tvaName.invalid && submitted()"
                formControlName="tvaName"
                placeholder="taux normal"
              />
              <app-field-error [control]="form.controls.tvaName" label="Le libellé" [submitted]="submitted()" />
            </div>
          }

          <div>
            <label class="label" for="tvaPercent">Taux (en %)</label>
            <div class="relative">
              <input
                id="tvaPercent"
                type="number"
                step="0.1"
                min="0"
                max="100"
                class="input pr-8"
                [class.input-error]="form.controls.percent.invalid && submitted()"
                formControlName="percent"
                placeholder="20"
              />
              <span class="pointer-events-none absolute right-3 top-1/2 -translate-y-1/2 text-sm muted">%</span>
            </div>
            <app-field-error [control]="form.controls.percent" label="Le taux" [submitted]="submitted()" />
            <p class="hint">
              Enregistré en base sous forme décimale :
              <span class="font-medium num">{{ decimalPreview() }}</span>
            </p>
          </div>
        </form>

        <div footer>
          <button type="button" class="btn-secondary" (click)="closeModal()">Annuler</button>
          <button type="submit" form="tva-form" class="btn-primary" [disabled]="saving()">
            <app-icon name="save" [size]="16" />
            {{ mode() === 'create' ? 'Créer' : 'Enregistrer' }}
          </button>
        </div>
      </app-modal>
    }
  `,
})
export class TvaListComponent implements OnInit {
  private readonly api = inject(TvaService);
  private readonly articleApi = inject(ArticleService);
  private readonly fb = inject(FormBuilder);
  private readonly toast = inject(ToastService);
  private readonly confirm = inject(ConfirmService);

  protected readonly items = signal<Tva[]>([]);
  protected readonly articles = signal<Article[]>([]);
  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly modalOpen = signal(false);
  protected readonly submitted = signal(false);
  protected readonly editing = signal<Tva | null>(null);
  protected readonly mode = signal<'create' | 'edit' | 'patch'>('create');

  protected readonly form = this.fb.group({
    tvaName: ['', [Validators.required, Validators.maxLength(60)]],
    percent: [20 as number | null, [Validators.required, Validators.min(0), Validators.max(100)]],
  });

  ngOnInit(): void {
    this.load();
  }

  modalTitle(): string {
    return {
      create: 'Nouveau taux de TVA',
      edit: 'Modifier le taux de TVA',
      patch: 'Ajuster le taux',
    }[this.mode()];
  }

  /** Aperçu de la valeur réellement persistée (0.2 pour 20 %). */
  decimalPreview(): string {
    const p = Number(this.form.controls.percent.value ?? 0);
    return Number.isFinite(p) ? String(p / 100) : '—';
  }

  articleCount(tvaId: number): number {
    return this.articles().filter((a) => a.tva?.tvaId === tvaId).length;
  }

  async load(): Promise<void> {
    this.loading.set(true);
    try {
      const [tvas, articles] = await Promise.all([
        firstValueFrom(this.api.list()),
        firstValueFrom(this.articleApi.list()).catch(() => [] as Article[]),
      ]);
      this.items.set(tvas);
      this.articles.set(articles);
    } catch {
      this.items.set([]);
    } finally {
      this.loading.set(false);
    }
  }

  openCreate(): void {
    this.mode.set('create');
    this.editing.set(null);
    this.submitted.set(false);
    this.form.reset({ tvaName: '', percent: 20 });
    this.form.controls.tvaName.enable();
    this.modalOpen.set(true);
  }

  openEdit(tva: Tva): void {
    this.mode.set('edit');
    this.editing.set(tva);
    this.submitted.set(false);
    this.form.reset({ tvaName: tva.tvaName, percent: toPercent(tva.tvaTaux) });
    this.form.controls.tvaName.enable();
    this.modalOpen.set(true);
  }

  openPatch(tva: Tva): void {
    this.mode.set('patch');
    this.editing.set(tva);
    this.submitted.set(false);
    this.form.reset({ tvaName: tva.tvaName, percent: toPercent(tva.tvaTaux) });
    // Le libellé n'est pas envoyé : on le désactive pour éviter toute confusion.
    this.form.controls.tvaName.disable();
    this.modalOpen.set(true);
  }

  closeModal(): void {
    this.form.controls.tvaName.enable();
    this.modalOpen.set(false);
  }

  async submit(): Promise<void> {
    this.submitted.set(true);
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    const raw = this.form.getRawValue();
    const taux = Number(raw.percent) / 100;
    const current = this.editing();

    try {
      if (this.mode() === 'patch' && current) {
        await firstValueFrom(this.api.patchTaux(current.tvaId, { tvaTaux: taux }));
        this.toast.success('Taux ajusté', `${raw.percent} %`);
      } else if (this.mode() === 'edit' && current) {
        await firstValueFrom(
          this.api.update(current.tvaId, { tvaName: (raw.tvaName ?? '').trim(), tvaTaux: taux }),
        );
        this.toast.success('Taux de TVA modifié');
      } else {
        await firstValueFrom(
          this.api.create({ tvaName: (raw.tvaName ?? '').trim(), tvaTaux: taux }),
        );
        this.toast.success('Taux de TVA créé');
      }
      this.closeModal();
      await this.load();
    } catch {
      /* déjà notifié */
    } finally {
      this.saving.set(false);
    }
  }

  async remove(tva: Tva): Promise<void> {
    const used = this.articleCount(tva.tvaId);
    if (used > 0) {
      this.toast.warning(
        'Suppression impossible',
        `${used} article(s) utilisent encore ce taux. Réaffectez-les d'abord.`,
      );
      return;
    }
    const ok = await this.confirm.askDelete(`le taux « ${tva.tvaName} »`);
    if (!ok) return;
    try {
      await firstValueFrom(this.api.delete(tva.tvaId));
      this.toast.success('Taux supprimé');
      await this.load();
    } catch {
      /* déjà notifié */
    }
  }
}

/** Convertit la valeur stockée (0.2 ou 20) en pourcentage affichable. */
function toPercent(taux: number | null | undefined): number {
  const n = Number(taux ?? 0);
  if (!Number.isFinite(n)) return 0;
  return n <= 1 ? Math.round(n * 10000) / 100 : n;
}
