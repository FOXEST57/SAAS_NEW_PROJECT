import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import {
  ArticleService,
  CartService,
  CustomerService,
  OrderLineService,
  SupplierService,
  TvaService,
} from '../../core/api';
import { Article, Cart, Customer, OrderLine, Supplier, Tva } from '../../core/models/api.models';
import {
  DocumentLine,
  buildLine,
  buildReference,
  familyOf,
  totalsOf,
} from '../../core/models/document-math';
import {
  DOCUMENT_STATUSES,
  DocumentStatus,
  normalizeStatus,
  reprefixReference,
  statusMeta,
  transitionLabel,
} from '../../core/models/document-status';
import {
  AD_HOC_PREFIX,
  AdHocArticleService,
} from '../../core/services/ad-hoc-article.service';
import { CommerceStore } from '../../core/services/commerce-store.service';
import { ConfirmService } from '../../core/services/confirm.service';
import { ToastService } from '../../core/services/toast.service';
import { MarginMeterComponent } from '../../shared/charts/margin-meter.component';
import { BackLinkComponent } from '../../shared/ui/back-link.component';
import { ModalComponent } from '../../shared/ui/modal.component';
import {
  AddressLinePipe,
  CapitalizePipe,
  EurPipe,
  RefPipe,
  TauxPctPipe,
} from '../../shared/pipes/format.pipes';
import { EmptyStateComponent } from '../../shared/ui/empty-state.component';
import { FieldErrorComponent } from '../../shared/ui/field-error.component';
import { IconComponent } from '../../shared/ui/icon.component';
import { PageHeaderComponent } from '../../shared/ui/page-header.component';
import { SearchInputComponent } from '../../shared/ui/search-input.component';
import { StatusBadgeComponent } from '../../shared/ui/status-badge.component';

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
    MarginMeterComponent,
    ModalComponent,
    BackLinkComponent,
    CapitalizePipe,
    EurPipe,
    RefPipe,
    TauxPctPipe,
    AddressLinePipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <app-page-header [title]="pageTitle()" [subtitle]="pageSubtitle()">
      <app-back-link fallbackUrl="/pipeline" fallbackLabel="au pipeline" />
      @if (cart()) {
        <a [routerLink]="['/documents', cart()!.crtId, 'impression']" class="btn-secondary">
          <app-icon name="print" [size]="16" /> Aperçu
        </a>
      }
      <button type="button" class="btn-primary" (click)="save()" [disabled]="saving() || !editable()">
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
      <!-- Fil du cycle de vie -->
      @if (cart()) {
        <ol class="mb-5 flex flex-wrap items-center gap-1.5 text-[12.5px]">
          @for (step of lifecycle(); track step.status; let last = $last) {
            <li class="flex items-center gap-1.5">
              <span
                class="inline-flex items-center gap-1.5 rounded-full px-2.5 py-1 font-medium transition-colors"
                [class]="step.reached ? step.activeClass : 'bg-ink-100 text-ink-400 dark:bg-ink-800 dark:text-ink-500'"
              >
                <app-icon [name]="step.icon" [size]="12" />
                {{ step.label }}
              </span>
              @if (!last) {
                <span class="text-ink-300 dark:text-ink-600"><app-icon name="chevronRight" [size]="12" /></span>
              }
            </li>
          }
        </ol>
      }

      <div class="grid gap-5 lg:grid-cols-3">
        <!-- Colonne principale -->
        <div class="space-y-5 lg:col-span-2">
          @if (editable()) {
            <div class="card">
              <div class="flex flex-wrap items-center justify-between gap-3 border-b border-ink-200 p-4 dark:border-ink-800">
                <h2 class="panel-title">Ajouter un article</h2>
                <div class="flex flex-wrap items-center gap-2">
                  <app-search-input
                    [value]="catalogSearch()"
                    (valueChange)="catalogSearch.set($event)"
                    placeholder="Référence, nom, catégorie…"
                  />
                  <button type="button" class="btn-secondary btn-sm" (click)="openAdHoc()">
                    <app-icon name="sparkles" [size]="14" /> Article hors catalogue
                  </button>
                </div>
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
                  @for (a of catalogResults(); track a.article.artId) {
                    <li class="flex items-center gap-3 px-4 py-2.5">
                      <span
                        class="flex h-9 w-9 shrink-0 items-center justify-center rounded-lg"
                        [class]="a.tone"
                      >
                        <app-icon [name]="a.icon" [size]="17" />
                      </span>
                      <div class="min-w-0 flex-1">
                        <p class="truncate text-sm font-medium">{{ a.article.artName | capitalize }}</p>
                        <p class="truncate font-mono text-[12px] muted">
                          {{ a.article.artReference | ref }}
                        </p>
                      </div>
                      <!-- La marge unitaire est visible avant même d'ajouter la ligne -->
                      <div class="hidden shrink-0 text-right sm:block">
                        @if (a.marginRate !== null) {
                          <p class="num text-[12px] font-medium" [class]="marginClass(a.marginRate)">
                            {{ pct(a.marginRate) }} marge
                          </p>
                        } @else {
                          <p class="text-[12px] muted">coût inconnu</p>
                        }
                        @if (a.article.artStock > 0) {
                          <p class="text-[12px] muted">
                            stock <span class="num">{{ a.article.artStock }}</span>
                          </p>
                        } @else {
                          <p class="text-[12px] font-medium text-heat-600 dark:text-heat-400">
                            à commander
                          </p>
                        }
                      </div>
                      <div class="shrink-0 text-right">
                        <p class="num text-sm font-semibold">{{ a.article.artPriceTTC | eur }}</p>
                      </div>
                      <!--
                        Le stock ne conditionne pas l'ajout : on chiffre avant
                        d'acheter. Le manque devient un besoin fournisseur.
                      -->
                      <button
                        type="button"
                        class="btn-primary btn-sm shrink-0"
                        (click)="addLine(a.article)"
                      >
                        <app-icon name="plus" [size]="14" /> Ajouter
                      </button>
                    </li>
                  }
                </ul>
              }
            </div>
          }

          <!-- Lignes -->
          <div class="card">
            <div class="flex items-center justify-between border-b border-ink-200 p-4 dark:border-ink-800">
              <h2 class="panel-title">Lignes du document</h2>
              <div class="flex items-center gap-2">
                <button
                  type="button"
                  class="btn-ghost btn-sm"
                  (click)="showCost.set(!showCost())"
                  [title]="showCost() ? 'Masquer les coûts' : 'Afficher les coûts d’achat'"
                >
                  <app-icon [name]="showCost() ? 'eye' : 'target'" [size]="14" />
                  {{ showCost() ? 'Masquer les coûts' : 'Voir les coûts' }}
                </button>
                <span class="badge-neutral num">{{ lines().length }}</span>
              </div>
            </div>

            @if (lines().length === 0) {
              <app-empty-state
                icon="cart"
                title="Aucune ligne"
                message="Ajoutez des articles depuis le catalogue ci-dessus."
              />
            } @else {
              <div class="table-wrap">
                <table class="table table-dense">
                  <thead>
                    <tr>
                      <th class="min-w-[130px]">Article</th>
                      @if (showCost()) {
                        <th class="w-[80px] whitespace-nowrap text-right">Achat</th>
                      }
                      <th class="w-[82px] whitespace-nowrap text-right">P.U. HT</th>
                      @if (!showCost()) {
                        <!-- La TVA cède la place aux colonnes de rentabilité -->
                        <th class="w-[62px] whitespace-nowrap text-right">TVA</th>
                      }
                      <th class="w-[96px] text-center">Qté</th>
                      <th class="w-[88px] whitespace-nowrap text-right">Total HT</th>
                      @if (showCost()) {
                        <th class="w-[74px] whitespace-nowrap text-right">Marge</th>
                      }
                      <th class="w-[90px] whitespace-nowrap text-right">Total TTC</th>
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
                          <p class="whitespace-nowrap font-mono text-[12px] muted">
                            {{ line.reference | ref }}
                          </p>
                          @if (line.quantity > line.stock) {
                            <p class="mt-1 flex items-center gap-1 text-[12px] font-medium text-heat-600 dark:text-heat-400">
                              <app-icon name="truck" [size]="12" />
                              {{ line.quantity - (line.stock > 0 ? line.stock : 0) }} à commander
                              @if (line.stock > 0) {
                                <span class="font-normal">({{ line.stock }} en stock)</span>
                              }
                            </p>
                          }
                          @if (showCost() && line.costSupplier) {
                            <p class="mt-0.5 text-[11.5px] muted">via {{ line.costSupplier | capitalize }}</p>
                          }
                        </td>
                        @if (showCost()) {
                          <td class="num whitespace-nowrap text-right muted">
                            {{ line.unitCost === null ? '—' : (line.unitCost | eur) }}
                          </td>
                        }
                        <td class="num whitespace-nowrap text-right">{{ line.unitHt | eur }}</td>
                        @if (!showCost()) {
                          <td class="num whitespace-nowrap text-right muted">{{ line.vatRate | tauxPct }}</td>
                        }
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
                                class="input num h-8 w-14 px-1.5 text-center"
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
                        @if (showCost()) {
                          <td
                            class="num whitespace-nowrap text-right font-medium"
                            [class]="marginClass(line.marginRate)"
                          >
                            {{ line.marginRate === null ? '—' : pct(line.marginRate) }}
                          </td>
                        }
                        <td class="num whitespace-nowrap text-right font-semibold">
                          {{ line.totalTtc | eur }}
                        </td>
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

          <!--
            Limite connue du backend : POST /order-line refuse une ligne dont la
            quantité dépasse le stock. Elle ne se manifeste que sur un document
            déjà enregistré — POST /cart, lui, ne contrôle rien. Plutôt que de
            laisser l'utilisateur le découvrir à l'enregistrement, on l'annonce
            dès l'ajout.
          -->
          @if (cart() && blockedLines().length > 0) {
            <div
              class="flex items-start gap-3 rounded-xl border border-red-200 bg-red-50 p-4 dark:border-red-500/30 dark:bg-red-500/10"
            >
              <span class="mt-0.5 shrink-0 text-red-600 dark:text-red-400">
                <app-icon name="alert" [size]="17" />
              </span>
              <div class="min-w-0">
                <p class="text-[13px] font-medium text-red-900 dark:text-red-200">
                  {{ blockedLines().length }} ligne(s) seront refusées à l'enregistrement
                </p>
                <p class="mt-1 text-[12.5px] text-red-800 dark:text-red-300/90">
                  Votre API refuse d'ajouter à un document existant une ligne dont la quantité
                  dépasse le stock :
                  {{ blockedNames() }}.
                  Deux issues immédiates — ajuster le stock depuis
                  <a routerLink="/articles" class="font-medium underline">le catalogue</a>,
                  ou créer un nouveau document, la création n'étant pas soumise à ce contrôle.
                </p>
                <p class="mt-1.5 text-[12px] text-red-700 dark:text-red-300/70">
                  Le correctif backend fourni dans <code class="font-mono">backend-patch/</code>
                  lève définitivement cette limite.
                </p>
              </div>
            </div>
          }

          <!-- Besoin d'approvisionnement de ce document -->
          @if (shortages().length > 0) {
            <div
              class="rounded-xl border border-heat-200 bg-heat-50 p-4 dark:border-heat-500/30 dark:bg-heat-500/10"
            >
              <div class="flex items-start gap-3">
                <span class="mt-0.5 shrink-0 text-heat-600 dark:text-heat-400">
                  <app-icon name="truck" [size]="17" />
                </span>
                <div class="min-w-0 flex-1">
                  <p class="text-[13px] font-medium text-heat-900 dark:text-heat-200">
                    {{ shortages().length }} article(s) à commander chez vos fournisseurs
                  </p>
                  <ul class="mt-2 space-y-1.5">
                    @for (s of shortages(); track s.reference) {
                      <li class="flex flex-wrap items-baseline gap-x-2 text-[12.5px] text-heat-800 dark:text-heat-300/90">
                        <span class="num font-semibold">{{ s.missing }} ×</span>
                        <span class="font-medium">{{ s.name | capitalize }}</span>
                        @if (s.supplierName) {
                          <span>chez {{ s.supplierName | capitalize }}</span>
                        } @else {
                          <span class="font-medium text-red-700 dark:text-red-400">
                            — aucun fournisseur référencé
                          </span>
                        }
                      </li>
                    }
                  </ul>
                  <p class="mt-2 text-[12px] text-heat-700 dark:text-heat-300/80">
                    Le besoin sera consolidé dans
                    <a routerLink="/approvisionnement" class="font-medium underline">
                      l'écran Approvisionnement</a
                    >
                    dès que ce document passera en commande.
                  </p>
                </div>
              </div>
            </div>
          }

          <!-- Lignes sous le seuil de marge -->
          @if (weakLines().length > 0) {
            <div
              class="flex items-start gap-3 rounded-xl border border-amber-200 bg-amber-50 p-4 dark:border-amber-500/30 dark:bg-amber-500/10"
            >
              <span class="mt-0.5 shrink-0 text-amber-600 dark:text-amber-400">
                <app-icon name="alert" [size]="17" />
              </span>
              <div class="min-w-0">
                <p class="text-[13px] font-medium text-amber-900 dark:text-amber-200">
                  {{ weakLines().length }} ligne(s) sous votre seuil de {{ pct(store.marginTarget()) }}
                </p>
                <ul class="mt-1.5 space-y-0.5 text-[12.5px] text-amber-800 dark:text-amber-300/90">
                  @for (l of weakLines(); track l.articleId) {
                    <li>
                      {{ l.name | capitalize }} — marge {{ pct(l.marginRate!) }}
                      ({{ l.margin! | eur }} sur {{ l.totalHt | eur }})
                    </li>
                  }
                </ul>
              </div>
            </div>
          }
        </div>

        <!-- Colonne latérale -->
        <div class="space-y-5">
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
                <div class="flex items-start gap-2">
                  <app-status-badge [status]="status()" />
                  <span class="text-[12.5px] muted">{{ statusDescription() }}</span>
                </div>
              </div>
            </form>
          </div>

          <!-- Rentabilité : le cœur du dispositif -->
          <div class="card card-pad">
            <h2 class="mb-4 panel-title">Rentabilité</h2>
            <app-margin-meter
              label="Marge de ce document"
              [rate]="totals().marginRate"
              [target]="store.marginTarget()"
              [coverage]="totals().costCoverage"
            />

            <dl class="mt-4 space-y-2 border-t border-ink-200 pt-4 text-[13px] dark:border-ink-800">
              <div class="flex justify-between">
                <dt class="muted">Vente HT</dt>
                <dd class="num font-medium">{{ totals().totalHt | eur }}</dd>
              </div>
              <div class="flex justify-between">
                <dt class="muted">Coût d'achat</dt>
                <dd class="num">{{ totals().totalCost | eur }}</dd>
              </div>
              <div class="flex justify-between font-semibold">
                <dt>Marge brute</dt>
                <dd class="num" [class]="marginClass(totals().marginRate)">{{ totals().margin | eur }}</dd>
              </div>
            </dl>
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
              <h2 class="mb-1 panel-title">Faire avancer</h2>
              <p class="mb-4 text-[13px] muted">Une facture émise n'est plus modifiable.</p>
              <div class="space-y-2">
                @for (next of nextStatuses(); track next; let first = $first) {
                  <button
                    type="button"
                    class="w-full justify-start"
                    [class]="first && next !== 'ANNULE' ? 'btn-primary' : 'btn-secondary'"
                    (click)="transition(next)"
                    [disabled]="saving()"
                  >
                    <app-icon [name]="iconFor(next)" [size]="16" />
                    {{ label(next) }}
                  </button>
                }
                @if (nextStatuses().length === 0) {
                  <p class="text-[13px] muted">Ce document est au terme de son cycle.</p>
                }
              </div>
            </div>
          } @else {
            <div class="card card-pad">
              <p class="text-[13px] muted">
                Le document sera créé au statut <span class="font-medium">Panier</span>, puis suivra
                le cycle devis → commande → facture.
              </p>
            </div>
          }
        </div>
      </div>
    }

    @if (adHocOpen()) {
      <app-modal
        title="Article hors catalogue"
        subtitle="Créé à la volée pour ce chiffrage, repérable par sa référence HC- et sa catégorie dédiée."
        widthClass="max-w-2xl"
        (closed)="adHocOpen.set(false)"
      >
        <form [formGroup]="adHocForm" id="adhoc-form" class="space-y-4" (ngSubmit)="createAdHoc()">
          <div>
            <label class="label" for="hc-name">Désignation</label>
            <input
              id="hc-name"
              type="text"
              class="input"
              [class.input-error]="adHocInvalid('name')"
              formControlName="name"
              placeholder="Grille de reprise inox 600×300"
            />
            <app-field-error
              [control]="adHocForm.controls.name"
              label="La désignation"
              [submitted]="adHocSubmitted()"
            />
          </div>

          <div>
            <label class="label" for="hc-desc">Description</label>
            <input
              id="hc-desc"
              type="text"
              class="input"
              formControlName="description"
              placeholder="Facultatif — reprend la désignation si vide"
            />
          </div>

          <div class="grid gap-4 sm:grid-cols-2">
            <div>
              <label class="label" for="hc-price">Prix de vente HT</label>
              <div class="relative">
                <input
                  id="hc-price"
                  type="number"
                  step="0.01"
                  min="0.01"
                  class="input pr-8"
                  [class.input-error]="adHocInvalid('priceHt')"
                  formControlName="priceHt"
                  placeholder="0.00"
                />
                <span class="pointer-events-none absolute right-3 top-1/2 -translate-y-1/2 text-sm muted">€</span>
              </div>
              <app-field-error
                [control]="adHocForm.controls.priceHt"
                label="Le prix de vente"
                [submitted]="adHocSubmitted()"
              />
            </div>

            <div>
              <label class="label" for="hc-tva">Taux de TVA</label>
              <select
                id="hc-tva"
                class="input"
                [class.input-error]="adHocInvalid('tvaId')"
                formControlName="tvaId"
              >
                <option [ngValue]="null" disabled>— Sélectionner —</option>
                @for (t of tvas(); track t.tvaId) {
                  <option [ngValue]="t.tvaId">
                    {{ t.tvaName | capitalize }} ({{ t.tvaTaux | tauxPct }})
                  </option>
                }
              </select>
              <app-field-error
                [control]="adHocForm.controls.tvaId"
                label="Le taux de TVA"
                [submitted]="adHocSubmitted()"
              />
            </div>
          </div>

          <div class="border-t border-ink-200 pt-4 dark:border-ink-800">
            <h3 class="panel-title">Approvisionnement</h3>
            <p class="mb-3 mt-1 text-[13px] muted">
              Renseigner le fournisseur et son prix rend la marge calculable et fait
              apparaître l'article dans les besoins d'achat. Facultatif, mais recommandé.
            </p>

            <div class="grid gap-4 sm:grid-cols-3">
              <div class="sm:col-span-2">
                <label class="label" for="hc-supplier">Fournisseur</label>
                <select id="hc-supplier" class="input" formControlName="supplierId">
                  <option [ngValue]="null">— Aucun pour l'instant —</option>
                  @for (sup of suppliers(); track sup.splId) {
                    <option [ngValue]="sup.splId">{{ sup.splName | capitalize }}</option>
                  }
                </select>
              </div>

              <div>
                <label class="label" for="hc-cost">Prix d'achat HT</label>
                <div class="relative">
                  <input
                    id="hc-cost"
                    type="number"
                    step="0.01"
                    min="0"
                    class="input pr-8"
                    formControlName="purchasePrice"
                    placeholder="0.00"
                  />
                  <span class="pointer-events-none absolute right-3 top-1/2 -translate-y-1/2 text-sm muted">€</span>
                </div>
              </div>

              <div class="sm:col-span-3">
                <label class="label" for="hc-supref">Référence fournisseur</label>
                <input
                  id="hc-supref"
                  type="text"
                  class="input font-mono text-[13px]"
                  formControlName="supplierReference"
                  placeholder="Facultatif — reprend la référence HC- si vide"
                />
              </div>
            </div>
          </div>

          @if (adHocMarginPreview() !== null) {
            <p
              class="rounded-lg px-3.5 py-2.5 text-[13px]"
              [class]="
                adHocMarginPreview()! >= store.marginTarget()
                  ? 'bg-emerald-50 text-emerald-800 dark:bg-emerald-500/10 dark:text-emerald-300'
                  : 'bg-amber-50 text-amber-800 dark:bg-amber-500/10 dark:text-amber-300'
              "
            >
              Marge à la revente :
              <span class="num font-semibold">{{ pct(adHocMarginPreview()!) }}</span>
              — seuil {{ pct(store.marginTarget()) }}
            </p>
          }
        </form>

        <div footer>
          <button type="button" class="btn-secondary" (click)="adHocOpen.set(false)">Annuler</button>
          <button type="submit" form="adhoc-form" class="btn-primary" [disabled]="adHocSaving()">
            <app-icon name="plus" [size]="16" />
            Créer et ajouter au document
          </button>
        </div>
      </app-modal>
    }
  `,
})
export class CartEditorComponent implements OnInit {
  private readonly cartApi = inject(CartService);
  private readonly lineApi = inject(OrderLineService);
  private readonly articleApi = inject(ArticleService);
  private readonly customerApi = inject(CustomerService);
  private readonly supplierApi = inject(SupplierService);
  private readonly tvaApi = inject(TvaService);
  private readonly adHoc = inject(AdHocArticleService);
  private readonly fb = inject(FormBuilder);
  private readonly toast = inject(ToastService);
  private readonly confirm = inject(ConfirmService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  protected readonly store = inject(CommerceStore);

  protected readonly cart = signal<Cart | null>(null);
  protected readonly articles = signal<Article[]>([]);
  protected readonly customers = signal<Customer[]>([]);
  protected readonly draft = signal<DraftLine[]>([]);
  private readonly persistedLines = signal<OrderLine[]>([]);
  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly submitted = signal(false);
  protected readonly catalogSearch = signal('');
  protected readonly status = signal<DocumentStatus>('PANIER');
  protected readonly showCost = signal(false);
  protected readonly suppliers = signal<Supplier[]>([]);
  protected readonly tvas = signal<Tva[]>([]);
  protected readonly adHocOpen = signal(false);
  protected readonly adHocSaving = signal(false);
  protected readonly adHocSubmitted = signal(false);

  protected readonly adHocForm = this.fb.group({
    name: ['', [Validators.required, Validators.maxLength(120)]],
    description: [''],
    priceHt: [null as number | null, [Validators.required, Validators.min(0.01)]],
    tvaId: [null as number | null, [Validators.required]],
    supplierId: [null as number | null],
    purchasePrice: [null as number | null],
    supplierReference: [''],
  });

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

  /** Lignes dont la marge passe sous le seuil : signalées, jamais bloquantes. */
  protected readonly weakLines = computed(() =>
    this.lines().filter(
      (l) => l.marginRate !== null && l.marginRate < this.store.marginTarget(),
    ),
  );

  /**
   * Lignes dont la quantité dépasse le stock : ce sont des commandes à passer,
   * pas des erreurs de saisie.
   */
  protected readonly shortages = computed(() =>
    this.lines()
      .filter((l) => l.quantity > l.stock)
      .map((l) => ({
        name: l.name,
        reference: l.reference,
        missing: l.quantity - Math.max(0, l.stock),
        supplierName: l.costSupplier,
      })),
  );

  /**
   * Lignes que le backend refusera lors de l'enregistrement.
   *
   * Uniquement pertinent sur un document déjà créé : `POST /cart` ne contrôle
   * pas le stock, `POST /order-line` si. Une ligne déjà persistée à la même
   * quantité ne pose pas de problème — seuls les ajouts et les hausses de
   * quantité sont soumis au contrôle.
   */
  protected readonly blockedLines = computed(() => {
    if (!this.cart()) return [];
    const persisted = new Map(
      this.persistedLines().map(
        (l) => [l.article?.artId ?? l.id?.articleId ?? 0, l.quantity ?? 0] as const,
      ),
    );
    return this.lines().filter((l) => {
      if (l.quantity <= l.stock) return false;
      const before = persisted.get(l.articleId);
      // Ligne inchangée : elle est déjà en base, le contrôle ne s'appliquera pas.
      return before === undefined || l.quantity > before;
    });
  });

  protected blockedNames(): string {
    return this.blockedLines()
      .map((l) => `${l.name} (${l.quantity} demandé${l.quantity > 1 ? 's' : ''}, ${Math.max(0, l.stock)} en stock)`)
      .join(' · ');
  }

  /** Marge prévisionnelle de l'article en cours de création. */
  protected readonly adHocMarginPreview = computed(() => {
    const price = Number(this.adHocForm.controls.priceHt.value ?? 0);
    const cost = Number(this.adHocForm.controls.purchasePrice.value ?? 0);
    if (!price || !cost || price <= 0) return null;
    return (price - cost) / price;
  });

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
      .slice(0, 40)
      .map((article) => {
        const preview = buildLine(article, 1);
        return {
          article,
          marginRate: preview.marginRate,
          icon: this.iconOf(article),
          tone: this.toneOf(article),
        };
      });
  });

  protected readonly nextStatuses = computed(() => statusMeta(this.status()).next);

  /** Étapes franchies, pour le fil d'Ariane du cycle de vie. */
  protected readonly lifecycle = computed(() => {
    const currentStage = statusMeta(this.status()).stage;
    return (['PANIER', 'DEVIS', 'COMMANDE', 'FACTURE', 'PAYEE'] as DocumentStatus[]).map(
      (s) => {
        const meta = DOCUMENT_STATUSES[s];
        return {
          status: s,
          label: meta.label,
          icon: meta.icon,
          activeClass: meta.badgeClass,
          reached: currentStage !== null && (meta.stage ?? 0) <= currentStage,
        };
      },
    );
  });

  async ngOnInit(): Promise<void> {
    const idParam = this.route.snapshot.paramMap.get('id');
    const id = idParam ? Number(idParam) : null;

    this.loading.set(true);
    try {
      const [articles, customers, suppliers, tvas] = await Promise.all([
        firstValueFrom(this.articleApi.list()).catch(() => [] as Article[]),
        firstValueFrom(this.customerApi.list()).catch(() => [] as Customer[]),
        firstValueFrom(this.supplierApi.list()).catch(() => [] as Supplier[]),
        firstValueFrom(this.tvaApi.list()).catch(() => [] as Tva[]),
      ]);
      this.articles.set(articles);
      this.customers.set(customers);
      this.suppliers.set(suppliers);
      this.tvas.set(tvas);

      if (id) {
        await this.loadCart(id);
      } else {
        const carts = await firstValueFrom(this.cartApi.list()).catch(() => [] as Cart[]);
        this.form.reset({
          crtRef: buildReference(
            DOCUMENT_STATUSES.PANIER.prefix,
            carts.map((c) => c.crtRef),
            new Date().getFullYear(),
          ),
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

  protected pageTitle(): string {
    const cart = this.cart();
    if (!cart) return 'Nouveau document';
    return `${statusMeta(cart.crtStatus).docLabel} ${cart.crtRef?.toUpperCase()}`;
  }

  protected pageSubtitle(): string {
    const cart = this.cart();
    if (!cart) return 'Composez un panier, puis transformez-le en devis, commande et facture.';
    return statusMeta(cart.crtStatus).description;
  }

  protected statusDescription(): string {
    return statusMeta(this.status()).description;
  }

  protected label(next: DocumentStatus): string {
    return transitionLabel(next);
  }

  protected iconFor(next: DocumentStatus): string {
    return next === 'PANIER' ? 'arrowLeft' : DOCUMENT_STATUSES[next].icon;
  }

  private iconOf(a: Article): string {
    return { clim: 'snowflake', chauffage: 'flame', autre: 'package' }[familyOf(a)];
  }

  private toneOf(a: Article): string {
    return {
      clim: 'bg-brand-50 text-brand-600 dark:bg-brand-500/10 dark:text-brand-400',
      chauffage: 'bg-heat-50 text-heat-600 dark:bg-heat-500/10 dark:text-heat-400',
      autre: 'bg-ink-100 text-ink-500 dark:bg-ink-800 dark:text-ink-400',
    }[familyOf(a)];
  }

  protected pct(v: number): string {
    return `${new Intl.NumberFormat('fr-FR', { maximumFractionDigits: 1 }).format(v * 100)} %`;
  }

  protected marginClass(rate: number | null): string {
    if (rate === null) return 'muted';
    if (rate >= this.store.marginTarget()) return 'text-emerald-700 dark:text-emerald-400';
    if (rate >= this.store.marginTarget() * 0.6) return 'text-amber-700 dark:text-amber-400';
    return 'text-red-700 dark:text-red-400';
  }

  protected invalid(name: string): boolean {
    const c = this.form.get(name);
    return !!c && c.invalid && (c.touched || c.dirty || this.submitted());
  }

  /* ---------------- Édition des lignes ---------------- */

  protected addLine(article: Article): void {
    if (this.draft().some((d) => d.articleId === article.artId)) return;
    this.draft.update((list) => [...list, { articleId: article.artId, quantity: 1 }]);
  }

  protected removeLine(articleId: number): void {
    this.draft.update((list) => list.filter((d) => d.articleId !== articleId));
  }

  protected changeQuantity(articleId: number, delta: number): void {
    this.draft.update((list) =>
      list.map((d) =>
        d.articleId === articleId ? { ...d, quantity: Math.max(1, d.quantity + delta) } : d,
      ),
    );
  }

  protected setQuantity(articleId: number, event: Event): void {
    const value = Math.max(1, Number((event.target as HTMLInputElement).value) || 1);
    this.draft.update((list) =>
      list.map((d) => (d.articleId === articleId ? { ...d, quantity: value } : d)),
    );
  }

  /* ---------------- Article hors catalogue ---------------- */

  protected openAdHoc(): void {
    this.adHocSubmitted.set(false);
    this.adHocForm.reset({
      name: this.catalogSearch().trim(),
      description: '',
      priceHt: null,
      tvaId: this.tvas()[0]?.tvaId ?? null,
      supplierId: null,
      purchasePrice: null,
      supplierReference: '',
    });
    this.adHocOpen.set(true);
  }

  protected adHocInvalid(name: string): boolean {
    const c = this.adHocForm.get(name);
    return !!c && c.invalid && (c.touched || c.dirty || this.adHocSubmitted());
  }

  /**
   * Crée l'article puis l'ajoute au document.
   *
   * Le catalogue local est rechargé pour que la ligne dispose immédiatement de
   * son coût d'achat et de sa marge.
   */
  protected async createAdHoc(): Promise<void> {
    this.adHocSubmitted.set(true);
    if (this.adHocForm.invalid) {
      this.adHocForm.markAllAsTouched();
      return;
    }

    this.adHocSaving.set(true);
    const raw = this.adHocForm.getRawValue();

    try {
      const article = await this.adHoc.create({
        name: raw.name ?? '',
        description: raw.description ?? '',
        priceHt: Number(raw.priceHt),
        tvaId: Number(raw.tvaId),
        supplierId: raw.supplierId ? Number(raw.supplierId) : null,
        purchasePrice: raw.purchasePrice ? Number(raw.purchasePrice) : null,
        supplierReference: raw.supplierReference || null,
      });

      const refreshed = await firstValueFrom(this.articleApi.list()).catch(
        () => [...this.articles(), article],
      );
      this.articles.set(refreshed);

      this.addLine(article);
      this.adHocOpen.set(false);
      this.toast.success(
        'Article créé',
        `${article.artReference?.toUpperCase()} — ajouté au document et à commander.`,
      );
      this.store.reload();
    } catch {
      /* déjà notifié par l'intercepteur */
    } finally {
      this.adHocSaving.set(false);
    }
  }

  /* ---------------- Persistance ---------------- */

  protected async save(): Promise<void> {
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
        const created = await firstValueFrom(
          this.cartApi.create({
            crtRef: (raw.crtRef ?? '').trim(),
            crtStatus: this.status(),
            ctmId: Number(raw.ctmId),
            orderLines: this.draft().map((d) => ({
              cartId: 0, // ignoré par le backend à la création
              articleId: d.articleId,
              quantity: d.quantity,
            })),
          }),
        );
        this.toast.success('Document créé', created.crtRef?.toUpperCase());
        this.store.reload();
        await this.router.navigate(['/documents', created.crtId]);
        return;
      }

      await firstValueFrom(
        this.cartApi.update(existing.crtId, {
          crtRef: (raw.crtRef ?? '').trim(),
          crtStatus: this.status(),
          ctmId: Number(raw.ctmId),
          orderLines: [],
        }),
      );

      const blocked = this.blockedLines().length;
      await this.syncLines(existing.crtId);
      if (blocked === 0) this.toast.success('Document enregistré');
      await this.loadCart(existing.crtId);
      this.store.reload();
    } catch {
      /* déjà notifié par l'intercepteur */
    } finally {
      this.saving.set(false);
    }
  }

  /**
   * Réconcilie les lignes locales avec celles du serveur.
   * `PUT /cart/{id}` ne prend pas les lignes en charge : chaque écart passe
   * par `/order-line`.
   */
  private async syncLines(cartId: number): Promise<void> {
    const before = new Map(
      this.persistedLines()
        .map((l) => [l.article?.artId ?? l.id?.articleId ?? 0, l.quantity ?? 0] as const)
        .filter(([id]) => id > 0),
    );
    const after = new Map(this.draft().map((d) => [d.articleId, d.quantity] as const));

    // Chaque ligne est traitée indépendamment : une ligne refusée par le
    // contrôle de stock ne doit pas emporter les autres avec elle.
    const tasks: { articleId: number; run: Promise<unknown> }[] = [];

    for (const [articleId, quantity] of after) {
      const previous = before.get(articleId);
      if (previous === undefined) {
        tasks.push({
          articleId,
          run: firstValueFrom(this.lineApi.create({ cartId, articleId, quantity }, true)),
        });
      } else if (previous !== quantity) {
        tasks.push({
          articleId,
          run: firstValueFrom(this.lineApi.update(articleId, cartId, { quantity }, true)),
        });
      }
    }

    for (const [articleId] of before) {
      if (!after.has(articleId)) {
        tasks.push({ articleId, run: firstValueFrom(this.lineApi.delete(articleId, cartId)) });
      }
    }

    const results = await Promise.allSettled(tasks.map((t) => t.run));
    const rejected = tasks
      .filter((_, i) => results[i].status === 'rejected')
      .map((t) => this.catalog().get(t.articleId)?.artName ?? `article ${t.articleId}`);

    if (rejected.length > 0) {
      this.toast.error(
        `${rejected.length} ligne(s) refusée(s) par le serveur`,
        `${rejected.join(', ')} — quantité supérieure au stock. Ajustez le stock au catalogue, ou appliquez le correctif backend fourni. Les autres lignes ont bien été enregistrées.`,
      );
    }
  }

  /** Change le statut et renumérote la référence selon la nouvelle étape. */
  protected async transition(next: DocumentStatus): Promise<void> {
    const existing = this.cart();
    if (!existing) return;

    const meta = DOCUMENT_STATUSES[next];

    // Le passage en commande est le moment où l'engagement devient ferme :
    // c'est là qu'il faut savoir ce qu'il reste à acheter.
    if (next === 'COMMANDE' && this.shortages().length > 0) {
      const detail = this.shortages()
        .map(
          (s) =>
            `${s.missing} × ${s.name}${s.supplierName ? ` (${s.supplierName})` : ' — aucun fournisseur référencé'}`,
        )
        .join(' · ');

      const ok = await this.confirm.ask({
        title: 'Approvisionnement nécessaire',
        message: `Cette commande engage du matériel que vous n'avez pas en stock : ${detail}. Le besoin sera ajouté à l'écran Approvisionnement. Confirmez-vous la validation ?`,
        confirmLabel: 'Valider la commande',
      });
      if (!ok) return;
    }

    if (next === 'FACTURE') {
      const ok = await this.confirm.ask({
        title: 'Émettre la facture',
        message:
          "Une fois facturé, le contenu du document ne sera plus modifiable. Confirmez-vous l'émission ?",
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

    const reference = reprefixReference(
      this.form.controls.crtRef.value?.trim() || existing.crtRef,
      next,
    );

    this.saving.set(true);
    try {
      await firstValueFrom(
        this.cartApi.update(existing.crtId, {
          crtRef: reference,
          crtStatus: next,
          ctmId: Number(this.form.controls.ctmId.value ?? existing.customer?.ctmId),
          orderLines: [],
        }),
      );
      this.status.set(next);
      const shortageCount = this.shortages().length;
      this.toast.success(
        `${meta.docLabel} ${reference.toUpperCase()}`,
        next === 'COMMANDE' && shortageCount > 0
          ? `${shortageCount} article(s) à commander — voir l'écran Approvisionnement.`
          : `Document passé au statut « ${meta.label} ».`,
      );
      await this.loadCart(existing.crtId);
      this.store.reload();
    } catch {
      /* déjà notifié */
    } finally {
      this.saving.set(false);
    }
  }
}
