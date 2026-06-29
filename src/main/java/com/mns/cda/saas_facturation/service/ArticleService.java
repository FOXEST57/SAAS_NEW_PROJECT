package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.*;
import com.mns.cda.saas_facturation.Iservice.IArticleService;
import com.mns.cda.saas_facturation.Iservice.ICategoryService;
import com.mns.cda.saas_facturation.Iservice.ISupplierService;
import com.mns.cda.saas_facturation.Iservice.ITvaService;
import com.mns.cda.saas_facturation.model.Article;
import com.mns.cda.saas_facturation.model.Category;
import com.mns.cda.saas_facturation.model.Supplier;
import com.mns.cda.saas_facturation.model.Tva;
import com.mns.cda.saas_facturation.repository.ArticleRepository;
import com.mns.cda.saas_facturation.repository.CategoryRepository;
import com.mns.cda.saas_facturation.repository.SupplierRepository;
import com.mns.cda.saas_facturation.repository.TvaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service métier gérant la logique liée aux articles ({@link Article}).
 *
 * <p>Implémente {@link IArticleService} et délègue toutes les opérations
 * de persistance aux repositories correspondants.</p>
 *
 * <p>Ce service est responsable de :</p>
 * <ul>
 *   <li>vérifier l'existence des entités liées (TVA, fournisseur, catégorie)
 *       avant toute création ou modification d'article</li>
 *   <li>construire les entités {@link Article} à partir des DTOs reçus en entrée</li>
 *   <li>calculer le prix TTC dynamiquement lors du mapping vers {@link ArticleDTO}</li>
 *   <li>convertir les entités JPA en DTOs via {@link #toDTO(Article)}
 *       avant de les retourner au contrôleur</li>
 * </ul>
 *
 * <p>L'injection de dépendances se fait via le constructeur,
 * généré automatiquement par {@code @RequiredArgsConstructor} de Lombok.</p>
 *
 * @see IArticleService
 * @see ArticleRepository
 * @see ArticleDTO
 * @see ArticleRequestDTO
 */
@Service
@RequiredArgsConstructor
public class ArticleService implements IArticleService {

    private final ArticleRepository articleRepository;
    private final TvaRepository tvaRepository;
    private final SupplierRepository supplierRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;

    /**
     * Récupère la liste complète de tous les articles en base de données.
     *
     * <p>Chaque entité {@link Article} est convertie en {@link ArticleDTO}
     * via {@link #toDTO(Article)}, ce qui inclut le calcul du prix TTC.</p>
     *
     * @return une {@link List} de {@link ArticleDTO} (vide si aucun article n'existe)
     */
    // Voir pagination — Pageable (page, size) à envisager si le volume d'articles devient important
    @Override
    public List<ArticleDTO> findAll() {
        return articleRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Recherche un article par son identifiant unique.
     *
     * <p>Retourne un {@link Optional} vide si aucun article ne correspond à l'id fourni,
     * sans lever d'exception — la vérification est laissée à la charge du contrôleur.</p>
     *
     * @param id l'identifiant unique de l'article à rechercher
     * @return un {@link Optional} contenant l'{@link ArticleDTO} si trouvé, vide sinon
     */
    @Override
    public Optional<ArticleDTO> findById(Long id) {
        return articleRepository.findById(id)
                .map(this::toDTO); // Conversion entité → DTO si l'Optional n'est pas vide
    }

    /**
     * Récupère la liste des articles associés à un fournisseur spécifique.
     *
     * <p>L'existence du fournisseur est vérifiée avant toute requête sur les articles.
     * Si le fournisseur est introuvable, une {@link ISupplierService.SupplierNotFoundException}
     * est levée immédiatement.</p>
     *
     * @param id l'identifiant unique du fournisseur dont on veut récupérer les articles
     * @return une {@link List} de {@link ArticleDTO} associés à ce fournisseur
     *         (vide si le fournisseur n'a aucun article)
     * @throws ISupplierService.SupplierNotFoundException si le fournisseur n'existe pas en base
     */
    @Override
    public List<ArticleDTO> findBySupplier(Long id) throws ISupplierService.SupplierNotFoundException {

        Optional<Supplier> supplier = supplierRepository.findById(id);

        // Vérification préalable : on lève l'exception si le fournisseur est inexistant
        if (supplier.isEmpty()) {
            throw new ISupplierService.SupplierNotFoundException();
        }

        return articleRepository.findBySupplierSplId(id)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Crée un nouvel article en base de données à partir des données fournies dans le DTO.
     *
     * <p>Les étapes réalisées sont les suivantes :</p>
     * <ol>
     *   <li>Vérification de l'existence de la TVA associée</li>
     *   <li>Vérification de l'existence de la catégorie associée</li>
     *   <li>Récupération optionnelle du fournisseur si un {@code supplierId} est fourni</li>
     *   <li>Construction et persistance de l'entité {@link Article}</li>
     *   <li>Mapping de l'entité sauvegardée vers un {@link ArticleDTO} via {@link #toDTO(Article)}</li>
     * </ol>
     *
     * @param dto les données de l'article à créer
     * @return un {@link ArticleDTO} contenant les informations de l'article créé,
     *         incluant la TVA, la catégorie et le fournisseur associé (ou {@code null} si aucun fournisseur)
     * @throws ITvaService.TvaNotFoundException           si la TVA référencée n'existe pas en base
     * @throws ISupplierService.SupplierNotFoundException si le fournisseur référencé n'existe pas en base
     * @throws ICategoryService.CategoryNotFoundException si la catégorie référencée n'existe pas en base
     */
    @Override
    public ArticleDTO create(ArticleRequestDTO dto) throws ITvaService.TvaNotFoundException,
            ISupplierService.SupplierNotFoundException, ICategoryService.CategoryNotFoundException {

        // Récupération de la TVA — obligatoire, lève une TvaNotFoundException si l'id est inconnu
        Tva articleTva = tvaRepository.findById(dto.tvaId())
                .orElseThrow(ITvaService.TvaNotFoundException::new);

        // Récupération de la catégorie — obligatoire, lève une CategoryNotFoundException si l'id est inconnu
        Category articleCategory = categoryRepository.findById(dto.categoryId())
                .orElseThrow(ICategoryService.CategoryNotFoundException::new);

        // Le fournisseur est optionnel (0,1) — on initialise à null par défaut
        Supplier supplier = null;

        // Si un supplierId est fourni, on vérifie qu'il existe bien en base
        // Lève une SupplierNotFoundException si l'id est inconnu, pour éviter une association invalide
        if (dto.supplierId() != null) {
            supplier = supplierRepository.findById(dto.supplierId())
                    .orElseThrow(ISupplierService.SupplierNotFoundException::new);
        }

        // Construction de l'entité Article à partir du DTO
        // L'id est null car il sera généré automatiquement par la base de données
        Article article = new Article(
                null,
                dto.artReference(),
                dto.artName(),
                dto.artDescription(),
                dto.artPriceExcludeTaxes(),
                dto.artStock(),
                articleTva,
                supplier,
                articleCategory
        );

        // Persistance en base puis mapping vers le DTO de réponse
        // L'entité retournée par save() contient l'id généré par la base
        return toDTO(articleRepository.save(article));
    }

    /**
     * Supprime un article par son identifiant unique.
     *
     * <p>L'existence de l'article est vérifiée en amont dans le contrôleur
     * avant d'appeler cette méthode.</p>
     *
     * @param id l'identifiant unique de l'article à supprimer
     */
    @Override
    public void delete(Long id) {
        articleRepository.deleteById(id);
    }

    /**
     * Met à jour intégralement un article existant à partir de son identifiant.
     *
     * <p>Les étapes réalisées sont les suivantes :</p>
     * <ol>
     *   <li>Récupération de l'entité existante — lève {@link ArticleNotFoundException} si absente</li>
     *   <li>Mise à jour des champs simples (référence, nom, description, prix, stock)</li>
     *   <li>Mise à jour de la TVA associée</li>
     *   <li>Mise à jour de la catégorie associée</li>
     *   <li>Mise à jour optionnelle du fournisseur ({@code null} si non fourni)</li>
     *   <li>Persistance et mapping vers {@link ArticleDTO}</li>
     * </ol>
     *
     * @param id  l'identifiant unique de l'article à modifier
     * @param dto les nouvelles données de l'article
     * @return l'{@link ArticleDTO} de l'article après mise à jour complète
     * @throws ArticleNotFoundException                   si l'article ciblé n'existe pas en base
     * @throws ISupplierService.SupplierNotFoundException si le fournisseur référencé n'existe pas en base
     * @throws ITvaService.TvaNotFoundException           si la TVA référencée n'existe pas en base
     * @throws ICategoryService.CategoryNotFoundException si la catégorie référencée n'existe pas en base
     */
    @Override
    public ArticleDTO update(long id, ArticleRequestDTO dto) throws ArticleNotFoundException,
            ISupplierService.SupplierNotFoundException,
            ITvaService.TvaNotFoundException, ICategoryService.CategoryNotFoundException {

        // Récupération de l'entité existante — lève ArticleNotFoundException si absente
        Article article = articleRepository.findById(id)
                .orElseThrow(ArticleNotFoundException::new);

        // Mise à jour des champs simples de l'article
        // Note : pas de vérification si la référence existe déjà en base pour une autre article
        article.setArtReference(dto.artReference());
        article.setArtName(dto.artName());
        article.setArtDescription(dto.artDescription());
        article.setArtPriceExcludeTaxes(dto.artPriceExcludeTaxes());
        article.setArtStock(dto.artStock());

        // Mise à jour de la TVA — lève TvaNotFoundException si l'id est inconnu
        Tva tva = tvaRepository.findById(dto.tvaId())
                .orElseThrow(ITvaService.TvaNotFoundException::new);
        article.setTva(tva);

        // Mise à jour de la catégorie — lève CategoryNotFoundException si l'id est inconnu
        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(ICategoryService.CategoryNotFoundException::new);
        article.setCategory(category);

        // Le fournisseur est optionnel (0,1) — on initialise à null par défaut
        Supplier supplier = null;

        // Si un supplierId est fourni, on vérifie qu'il existe bien en base
        // Lève une SupplierNotFoundException si l'id est inconnu, pour éviter une association invalide
        if (dto.supplierId() != null) {
            supplier = supplierRepository.findById(dto.supplierId())
                    .orElseThrow(ISupplierService.SupplierNotFoundException::new);
        }

        article.setSupplier(supplier);

        Article saved = articleRepository.save(article);
        return toDTO(saved);
    }

    /**
     * Convertit une entité {@link Article} en {@link ArticleDTO}.
     *
     * <p>Cette méthode est utilisée en interne par le service pour éviter
     * d'exposer les entités JPA directement à la couche contrôleur.</p>
     *
     * <p>Elle effectue deux opérations supplémentaires par rapport à un simple mapping :</p>
     * <ul>
     *   <li>calcul du prix TTC : {@code prixHT × (1 + tauxTVA)}</li>
     *   <li>mapping du fournisseur vers {@link SupplierResponseDTO} —
     *       retourne {@code null} si aucun fournisseur n'est associé</li>
     * </ul>
     *
     * @param article l'entité article à convertir (ne doit pas être {@code null})
     * @return un {@link ArticleDTO} contenant toutes les informations de l'article,
     *         le prix TTC calculé et le fournisseur mappé en {@link SupplierResponseDTO}
     */
    private ArticleDTO toDTO(Article article) {

        // Calcul du prix TTC : prixHT × (1 + tauxTVA/100)
        BigDecimal priceTTC = article.getArtPriceExcludeTaxes()
                .multiply(BigDecimal.ONE.add(article.getTva().getTvaTaux()));

        // Mapping du fournisseur vers son DTO de réponse
        // Si aucun fournisseur n'est associé, on retourne null pour ce champ
        SupplierResponseDTO supplierResponse = article.getSupplier() != null
                ? new SupplierResponseDTO(
                article.getSupplier().getSplId(),
                article.getSupplier().getSplName())
                : null;

        return new ArticleDTO(
                article.getArtId(),
                article.getArtReference(),
                article.getArtName(),
                article.getArtDescription(),
                article.getArtPriceExcludeTaxes(),
                article.getArtStock(),
                article.getTva(),
                priceTTC,
                categoryService.toDTO(article.getCategory()),
                supplierResponse
        );
    }
}