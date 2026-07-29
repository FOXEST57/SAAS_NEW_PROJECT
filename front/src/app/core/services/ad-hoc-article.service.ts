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
      // Un article créé en cours de chiffrage n'est par définition pas en
      // stock : c'est précisément ce qui déclenche le besoin d'achat.
      artStock: 0,
      tvaId: draft.tvaId,
      suppliers: [],
    };

    let article: Article;
    try {
      article = await firstValueFrom(
        this.articles.create({
          ...payload,
          categoryIds: category ? [category.catId] : [],
        }),
      );
    } catch (err) {
      // `ArticleService.create` assigne une liste immuable à la collection de
      // catégories : Hibernate lève UnsupportedOperationException lors du
      // merge, mais uniquement si des catégories sont transmises. On retente
      // sans, plutôt que de bloquer la saisie sur un défaut backend.
      //
      // Le marquage « hors catalogue » n'est pas perdu pour autant : la
      // référence HC- suffit à `isAdHoc()`. Voir backend-patch/.
      if (!category) throw err;

      article = await firstValueFrom(
        this.articles.create({ ...payload, categoryIds: [] }),
      );

      this.toast.warning(
        'Catégorie non appliquée',
        `L'article ${reference} a bien été créé, mais votre API a refusé de lui associer la catégorie « Hors catalogue » (défaut connu, voir backend-patch/). Il reste identifiable par sa référence HC-.`,
      );
    }

    if (draft.supplierId && draft.purchasePrice !== null && draft.purchasePrice > 0) {
      // Échec non bloquant : l'article existe, seule la marge sera inconnue.
      await firstValueFrom(
        this.supplierRefs.create({
          articleId: article.artId,
          supplierId: draft.supplierId,
          splRefReference: draft.supplierReference?.trim() || reference,
          splRefSellPrice: draft.purchasePrice,
          splRefStock: 0,
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
