import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { AuthService } from '../../core/auth';
import { ThemeService } from '../../core/services/theme.service';
import { IconComponent } from '../../shared/ui/icon.component';

/**
 * Écran de connexion.
 *
 * Occupe toute la page, hors du gabarit applicatif : la barre latérale et
 * l'en-tête n'ont pas lieu d'être avant identification.
 */
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, IconComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="flex min-h-screen">
      <!-- Colonne de présentation, masquée sur petit écran -->
      <aside
        class="relative hidden w-[45%] max-w-2xl flex-col justify-between overflow-hidden bg-brand-700 p-12 text-white lg:flex"
      >
        <div
          class="pointer-events-none absolute -right-24 -top-24 h-96 w-96 rounded-full bg-white/10 blur-3xl"
        ></div>
        <div
          class="pointer-events-none absolute -bottom-32 -left-20 h-96 w-96 rounded-full bg-black/10 blur-3xl"
        ></div>

        <div class="relative flex items-center gap-3">
          <span
            class="flex h-11 w-11 items-center justify-center rounded-xl bg-white/15 backdrop-blur"
          >
            <app-icon name="snowflake" [size]="22" />
          </span>
          <div>
            <p class="text-lg font-bold tracking-tight">Klimafact</p>
            <p class="text-[12.5px] text-white/70">Climatisation &amp; Chauffage</p>
          </div>
        </div>

        <div class="relative max-w-md">
          <h1 class="text-3xl font-bold leading-tight tracking-tight">
            Du devis à la facture, sans ressaisie.
          </h1>
          <p class="mt-4 text-[15px] leading-relaxed text-white/80">
            Chiffrez une installation, suivez la marge en temps réel, transformez le devis en
            commande puis en facture — le tout dans un seul flux.
          </p>

          <ul class="mt-8 space-y-3 text-[14px] text-white/85">
            @for (item of highlights; track item) {
              <li class="flex items-start gap-2.5">
                <span class="mt-0.5 shrink-0 text-white/60">
                  <app-icon name="checkCircle" [size]="16" />
                </span>
                {{ item }}
              </li>
            }
          </ul>
        </div>

        <p class="relative text-[12px] text-white/50">
          © {{ year }} Klimafact SARL — Tous droits réservés
        </p>
      </aside>

      <!-- Formulaire -->
      <main class="flex flex-1 items-center justify-center px-5 py-10 sm:px-10">
        <div class="w-full max-w-sm">
          <!-- Bascule de thème, discrète -->
          <div class="mb-8 flex items-center justify-between">
            <div class="flex items-center gap-2.5 lg:hidden">
              <span
                class="flex h-9 w-9 items-center justify-center rounded-lg bg-brand-600 text-white"
              >
                <app-icon name="snowflake" [size]="18" />
              </span>
              <div>
                <p class="text-[15px] font-bold tracking-tight">Klimafact</p>
                <p class="text-[11px] muted">Climatisation &amp; Chauffage</p>
              </div>
            </div>
            <button
              type="button"
              class="btn-icon ml-auto"
              (click)="theme.toggle()"
              [attr.aria-label]="theme.isDark() ? 'Passer en thème clair' : 'Passer en thème sombre'"
            >
              <app-icon [name]="theme.isDark() ? 'sun' : 'moon'" [size]="18" />
            </button>
          </div>

          <h2 class="text-2xl font-bold tracking-tight">Connexion</h2>
          <p class="mt-1.5 text-[13.5px] muted">
            Identifiez-vous pour accéder à votre espace commercial.
          </p>

          <form class="mt-8 space-y-4" (ngSubmit)="submit()">
            <div>
              <label class="label" for="email">Adresse e-mail</label>
              <input
                id="email"
                name="email"
                type="email"
                class="input"
                [class.input-error]="showEmailError()"
                autocomplete="username"
                inputmode="email"
                placeholder="prenom.nom@klimafact.fr"
                [(ngModel)]="email"
                (blur)="emailTouched.set(true)"
                [disabled]="busy()"
                required
              />
              @if (showEmailError()) {
                <p class="field-error">Renseignez une adresse e-mail valide.</p>
              }
            </div>

            <div>
              <label class="label" for="password">Mot de passe</label>
              <div class="relative">
                <input
                  id="password"
                  name="password"
                  [type]="revealed() ? 'text' : 'password'"
                  class="input pr-11"
                  [class.input-error]="showPasswordError()"
                  autocomplete="current-password"
                  placeholder="••••••••"
                  [(ngModel)]="password"
                  (blur)="passwordTouched.set(true)"
                  [disabled]="busy()"
                  required
                />
                <button
                  type="button"
                  class="absolute right-1.5 top-1/2 flex h-8 w-8 -translate-y-1/2 items-center justify-center rounded-md text-ink-400 transition hover:bg-ink-100 hover:text-ink-600 dark:hover:bg-ink-800 dark:hover:text-ink-200"
                  (click)="revealed.set(!revealed())"
                  [attr.aria-label]="revealed() ? 'Masquer le mot de passe' : 'Afficher le mot de passe'"
                  tabindex="-1"
                >
                  <app-icon name="eye" [size]="16" />
                </button>
              </div>
              @if (showPasswordError()) {
                <p class="field-error">Saisissez votre mot de passe.</p>
              }
            </div>

            @if (error(); as message) {
              <div
                class="flex items-start gap-2.5 rounded-lg border border-red-200 bg-red-50 p-3 dark:border-red-500/30 dark:bg-red-500/10"
                role="alert"
              >
                <span class="mt-px shrink-0 text-red-600 dark:text-red-400">
                  <app-icon name="alert" [size]="16" />
                </span>
                <p class="text-[13px] text-red-800 dark:text-red-300">{{ message }}</p>
              </div>
            }

            <button type="submit" class="btn-primary w-full justify-center" [disabled]="busy()">
              @if (busy()) {
                <app-icon name="refresh" [size]="16" class="animate-spin" />
                Connexion…
              } @else {
                Se connecter
                <app-icon name="arrowRight" [size]="16" />
              }
            </button>
          </form>

          <p class="mt-8 text-center text-[12.5px] muted">
            Pas encore de compte ? Contactez un administrateur pour en faire créer un.
          </p>
        </div>
      </main>
    </div>
  `,
})
export class LoginComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  protected readonly theme = inject(ThemeService);

  protected readonly year = new Date().getFullYear();
  protected readonly highlights = [
    'Marge calculée à la ligne, avant de transmettre le devis',
    'Pipeline visuel du panier à la facture payée',
    'Besoins d’approvisionnement groupés par fournisseur',
  ];

  protected email = '';
  protected password = '';
  protected readonly revealed = signal(false);
  protected readonly busy = signal(false);
  protected readonly error = signal<string | null>(null);

  protected readonly emailTouched = signal(false);
  protected readonly passwordTouched = signal(false);
  private readonly submitted = signal(false);

  protected readonly showEmailError = computed(
    () => (this.emailTouched() || this.submitted()) && !isEmail(this.email),
  );
  protected readonly showPasswordError = computed(
    () => (this.passwordTouched() || this.submitted()) && this.password.length === 0,
  );

  protected async submit(): Promise<void> {
    this.submitted.set(true);
    this.error.set(null);

    if (!isEmail(this.email) || this.password.length === 0) return;

    this.busy.set(true);
    try {
      await firstValueFrom(
        this.auth.login({ ctmEmail: this.email.trim(), password: this.password }),
      );

      // Retour à l'écran initialement demandé, s'il y en avait un.
      const next = this.route.snapshot.queryParamMap.get('suite');
      await this.router.navigateByUrl(next ?? '/tableau-de-bord');
    } catch (err) {
      this.error.set(describe(err));
    } finally {
      this.busy.set(false);
    }
  }
}

function isEmail(value: string): boolean {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value.trim());
}

/**
 * Traduit l'échec en message utile.
 *
 * `JwtFilter` ne protège pas son analyse du jeton : une erreur d'authentification
 * peut donc remonter en 500 plutôt qu'en 401. On ne se fie pas au seul code de
 * statut pour formuler le message.
 */
function describe(err: unknown): string {
  if (!(err instanceof HttpErrorResponse)) {
    return 'Connexion impossible. Réessayez dans un instant.';
  }

  switch (err.status) {
    case 0:
      return "Le serveur est injoignable. Vérifiez qu'il est démarré et accessible.";
    case 401:
    case 403:
      return 'Adresse e-mail ou mot de passe incorrect.';
    case 400:
      return 'Identifiants refusés : vérifiez le format de l’adresse e-mail.';
    default:
      return `Le serveur a répondu ${err.status}. Réessayez, puis signalez-le si cela persiste.`;
  }
}
