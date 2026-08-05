import { Injectable, inject } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { ArticleService, CategoryService, SupplierReferenceService } from '../api';
import { Article, Category } from '../models/api.models';
import { ToastService } from './toast.service';

/** Saisie express d'un article absent du catalogue. */
export interface AdHocArticleDraft {
  name: string;
  description: string;
  /** Prix de vente hors taxes. */
  priceHt: number;
  tvaId: number;
  /** Prix d'achat consenti par le fournisseur, facultatif. */
  purchasePrice: number | null;
  /** Fournisseur chez qui commander, facultatif. */
  supplierId: number | null;
  /** Référence du fournisseur, facultative. */
  supplierReference: string | null;
}

/** Catégorie qui isole les articles créés en cours de chiffrage. */
export const AD_HOC_CATEGORY = 'hors catalogue';
export const AD_HOC_SLUG = 'hors-catalogue';
export const AD_HOC_PREFIX = 'HC';

/**
 * Création d'un article en cours de chiffrage.
 *
 * Le MCD ne permet pas de ligne libre : `OrderLine` porte une clé composite
 * `(articleId, cartId)` et une clé étrangère vers `Article`. Toute ligne de
 * document référence donc obligatoirement un article existant en base.
 *
 * Plutôt que de renoncer, on crée un vrai article — mais **marqué**, de deux
 * façons : une catégorie « Hors catalogue » dédiée, et une référence préfixée
 * `HC-2026-0001`. Le catalogue reste ainsi navigable, ces articles restent
 * filtrables, et un article qui se vend régulièrement peut être promu en
 * article normal en changeant simplement sa catégorie.
 *
 * Si un fournisseur et un prix d'achat sont renseignés, la référence
 * fournisseur est créée dans la foulée : l'article entre alors immédiatement
 * dans le calcul de marge et dans les besoins d'approvisionnement.
 */
@Injectable({ providedIn: 'root' })
export class AdHocArticleService {
  private readonly articles = inject(ArticleService);
  private readonly categories = inject(CategoryService);
  private readonly supplierRefs = inject(SupplierReferenceService);
  private readonly toast = inject(ToastService);

  /**
   * Crée l'article, sa catégorie si nécessaire, et sa référence fournisseur.
   * Renvoie l'article tel que le backend l'a enregistré.
   */
  async create(draft: AdHocArticleDraft): Promise<Article> {
    const reference = await this.nextReference();
    const category = await this.ensureCategory().catch(() => null);

    const payload = {
      artReference: reference,
      artName: draft.name.trim(),
      artDescription: draft.description?.trim() || draft.name.trim(),
      artPriceExcludeTaxes: draft.priceHt,
      // Le stock n'est plus transmis : le serveur le calcule à partir des
      // références. Un article créé en cours de chiffrage n'en a aucune, il
      // ressort donc naturellement à zéro — ce qui déclenche le besoin d'achat.
      tvaId: draft.tvaId,
      suppliers: [],
    };

    let article: Article;
    try {
      article = await firstValueFrom(
        this.articles.create(
          { ...payload, categoryIds: category ? [category.catId] : [] },
          // On formule nous-mêmes le diagnostic plus bas : une notification
          // générique par tentative ferait trois messages pour un seul échec.
          true,
        ),
      );
    } catch (err) {
      // Deuxième tentative sans catégorie : elle contournait le défaut de
      // `setCategories`, qui ne se déclenchait qu'en présence de catégories.
      //
      // Elle ne suffit plus. `ArticleService.create` construit maintenant un
      // inventaire initial et fait `setInventories(List.of(...))` — une liste
      // immuable, sur un bloc sans condition. Toute création d'article échoue
      // donc au merge, quel que soit le contenu envoyé. On retente quand même,
      // pour rester utile si le correctif partiel est appliqué, mais on ne
      // masque plus l'échec : le message dit ce qui se passe réellement.
      if (category) {
        try {
          article = await firstValueFrom(
            this.articles.create({ ...payload, categoryIds: [] }, true),
          );
          this.toast.warning(
            'Catégorie non appliquée',
            `L'article ${reference} a bien été créé, mais votre API a refusé de lui associer la catégorie « Hors catalogue ». Il reste identifiable par sa référence HC-.`,
          );
          return this.attachSupplier(article, draft, reference);
        } catch {
          /* le message ci-dessous couvre les deux tentatives */
        }
      }

      this.toast.error(
        "Création d'article impossible",
        `Votre API refuse toute création d'article : \`ArticleService.create\` assigne une liste immuable à la collection d'inventaires (\`setInventories(List.of(...))\`, ligne 140), ce qui fait échouer le merge Hibernate. Le bloc n'a aucune condition, aucun contenu ne passe. Correctif : backend-patch/Inventory-blocages.patch, point 8.`,
      );
      throw err;
    }

    return this.attachSupplier(article, draft, reference);
  }

  /**
   * Rattache la référence fournisseur, si l'utilisateur en a saisi une.
   * Extrait pour être partagé entre la création directe et le repli.
   */
  private async attachSupplier(
    article: Article,
    draft: AdHocArticleDraft,
    reference: string,
  ): Promise<Article> {
    if (draft.supplierId && draft.purchasePrice !== null && draft.purchasePrice > 0) {
      // Échec non bloquant : l'article existe, seule la marge sera inconnue.
      await firstValueFrom(
        this.supplierRefs.create({
          articleId: article.artId,
          supplierId: draft.supplierId,
          splRefReference: draft.supplierReference?.trim() || reference,
          splRefSellPrice: draft.purchasePrice,
          splRefStock: 0,
          // Article créé en cours de chiffrage : rien n'est encore commandé.
          // `PENDING` traduit fidèlement cet état — la marchandise reste à
          // approvisionner.
          status: 'PENDING',
        }),
      ).catch(() => null);

      // On relit l'article pour récupérer la référence fournisseur, dont
      // dépendent le coût de revient et la marge.
      return firstValueFrom(this.articles.getById(article.artId)).catch(() => article);
    }

    return article;
  }

  /** Un article hors catalogue peut être promu en article normal. */
  isAdHoc(article: Article | null | undefined): boolean {
    if (!article) return false;
    if ((article.artReference ?? '').toUpperCase().startsWith(`${AD_HOC_PREFIX}-`)) return true;
    return (article.categories ?? []).some(
      (c) => c.catName?.toLowerCase() === AD_HOC_CATEGORY,
    );
  }

  private async ensureCategory(): Promise<Category> {
    const all = await firstValueFrom(this.categories.list()).catch(() => [] as Category[]);
    const found = flatten(all).find((c) => c.catName?.toLowerCase() === AD_HOC_CATEGORY);
    if (found) return found;

    return firstValueFrom(
      this.categories.create({
        catName: 'Hors catalogue',
        catSlug: AD_HOC_SLUG,
        parentId: null,
      }),
    );
  }

  /** Numérotation annuelle continue : `HC-2026-0001`, `HC-2026-0002`… */
  private async nextReference(): Promise<string> {
    const year = new Date().getFullYear();
    const all = await firstValueFrom(this.articles.list()).catch(() => [] as Article[]);
    const pattern = new RegExp(`^${AD_HOC_PREFIX}-${year}-(\\d+)$`, 'i');

    const max = all.reduce((acc, a) => {
      const match = pattern.exec((a.artReference ?? '').trim());
      return match ? Math.max(acc, Number(match[1])) : acc;
    }, 0);

    return `${AD_HOC_PREFIX}-${year}-${String(max + 1).padStart(4, '0')}`;
  }
}

function flatten(nodes: readonly Category[]): Category[] {
  const out: Category[] = [];
  const walk = (list: readonly Category[] | null | undefined) => {
    for (const n of list ?? []) {
      out.push(n);
      walk(n.children);
    }
  };
  walk(nodes);
  return out;
}
