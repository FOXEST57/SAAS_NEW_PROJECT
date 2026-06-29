package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.*;
import com.mns.cda.saas_facturation.DTO.requestDTO.ArticleRequestDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.CategoryResponseDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.SupplierResponseDTO;
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
@Service             // Déclare cette classe comme un composant Spring de couche service
@RequiredArgsConstructor // Lombok génère un constructeur avec tous les champs final (injection automatique)
public class ArticleService implements IArticleService {

    // Repositories : interfaces Spring Data JPA qui gèrent les accès base de données
    private final ArticleRepository articleRepository;
    private final TvaRepository tvaRepository;
    private final SupplierRepository supplierRepository;
    private final CategoryRepository categoryRepository;

    // Services délégués pour le mapping vers leurs DTOs respectifs
    private final ICategoryService categoryService;
    private final ITvaService tvaService;
    private final ISupplierService supplierService;

    /**
     * Récupère la liste complète de tous les articles en base de données.
     *
     * <p>Chaque entité {@link Article} est convertie en {@link ArticleDTO}
     * via {@link #toDTO(Article)}, ce qui inclut le calcul du prix TTC.</p>
     *
     * <p><b>Note :</b> à envisager si le volume d'articles devient important,
     * l'usage de {@code Pageable} (page, size) pour limiter les données retournées.</p>
     *
     * @return une {@link List} de {@link ArticleDTO} (vide si aucun article n'existe)
     */
    @Override
    public List<ArticleDTO> findAll() {
        return articleRepository.findAll()  // Récupère toutes les lignes de la table Article
                .stream()                   // Transforme la liste en flux pour le traitement
                .map(this::toDTO)           // Convertit chaque entité Article en ArticleDTO
                .toList();                  // Collecte le résultat dans une liste immuable
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
        return articleRepository.findById(id) // Recherche par clé primaire — retourne un Optional
                .map(this::toDTO);            // Si présent, convertit l'entité en DTO avant retour
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

        // Vérification préalable : inutile d'interroger les articles si le fournisseur est inexistant
        if (supplier.isEmpty()) {
            throw new ISupplierService.SupplierNotFoundException();
        }

        // findBySupplierSplId est une méthode dérivée Spring Data : SELECT * FROM article WHERE spl_id = ?
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
     *   <li>Vérification de l'existence de la catégorie associée (optionnelle)</li>
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

        // La TVA est obligatoire : orElseThrow lève l'exception si l'id est inconnu
        Tva articleTva = tvaRepository.findById(dto.tvaId())
                .orElseThrow(ITvaService.TvaNotFoundException::new);

        // La catégorie est optionnelle : on ne la recherche que si un categoryId est fourni
        Category category = null;
        if (dto.categoryId() != null) {
            category = categoryRepository.findById(dto.categoryId())
                    .orElseThrow(ICategoryService.CategoryNotFoundException::new);
        }

        // Le fournisseur est optionnel (cardinalité 0,1) : null si non renseigné
        Supplier supplier = null;
        if (dto.supplierId() != null) {
            // Vérification explicite : évite une association vers un fournisseur inexistant en base
            supplier = supplierRepository.findById(dto.supplierId())
                    .orElseThrow(ISupplierService.SupplierNotFoundException::new);
        }

        // Construction de l'entité Article : l'id est null car généré automatiquement par la BDD (@GeneratedValue)
        Article article = new Article(
                null,
                dto.artReference(),
                dto.artName(),
                dto.artDescription(),
                dto.artPriceExcludeTaxes(),
                dto.artStock(),
                articleTva,
                supplier,
                category
        );

        // save() persiste l'entité et retourne la version avec l'id généré par la base
        return toDTO(articleRepository.save(article));
    }

    /**
     * Supprime un article par son identifiant unique.
     *
     * <p>L'existence de l'article est vérifiée en amont dans le contrôleur
     * via {@link #findById(Long)} avant d'appeler cette méthode.</p>
     *
     * @param id l'identifiant unique de l'article à supprimer
     */
    @Override
    public void delete(Long id) {
        // deleteById génère un DELETE FROM article WHERE art_id = ?
        articleRepository.deleteById(id);
    }

    /**
     * Met à jour intégralement un article existant à partir de son identifiant (comportement PUT).
     *
     * <p>Les étapes réalisées sont les suivantes :</p>
     * <ol>
     *   <li>Récupération de l'entité existante — lève {@link ArticleNotFoundException} si absente</li>
     *   <li>Mise à jour des champs simples (référence, nom, description, prix, stock)</li>
     *   <li>Mise à jour de la TVA associée</li>
     *   <li>Mise à jour de la catégorie associée (optionnelle)</li>
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

        // Récupération de l'entité existante : on travaille sur l'objet BDD pour conserver son id
        Article article = articleRepository.findById(id)
                .orElseThrow(ArticleNotFoundException::new);

        // Mise à jour des champs primitifs : chaque setter modifie la valeur en mémoire
        // JPA détecte les changements et génère un UPDATE SQL lors du save()
        article.setArtReference(dto.artReference());
        article.setArtName(dto.artName());
        article.setArtDescription(dto.artDescription());
        article.setArtPriceExcludeTaxes(dto.artPriceExcludeTaxes());
        article.setArtStock(dto.artStock());

        // Mise à jour de la relation TVA : on charge l'entité Tva depuis sa clé étrangère
        Tva tva = tvaRepository.findById(dto.tvaId())
                .orElseThrow(ITvaService.TvaNotFoundException::new);
        article.setTva(tva);

        // La catégorie est optionnelle : on ne met à jour la relation que si un id est fourni
        Category category = null;
        if (dto.categoryId() != null) {
            category = categoryRepository.findById(dto.categoryId())
                    .orElseThrow(ICategoryService.CategoryNotFoundException::new);
            article.setCategory(category);
        }

        // Le fournisseur est optionnel : on passe null si absent, ce qui supprime la relation en base
        Supplier supplier = null;
        if (dto.supplierId() != null) {
            supplier = supplierRepository.findById(dto.supplierId())
                    .orElseThrow(ISupplierService.SupplierNotFoundException::new);
        }
        article.setSupplier(supplier);

        // Persistance des modifications puis conversion en DTO pour la réponse HTTP
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
    @Override
    public ArticleDTO toDTO(Article article) {

        // Calcul du prix TTC : prixHT × (1 + tauxTVA)
        // BigDecimal est utilisé à la place de double pour éviter les erreurs d'arrondi monétaires
        BigDecimal priceTTC = article.getArtPriceExcludeTaxes()
                .multiply(BigDecimal.ONE.add(article.getTva().getTvaTaux()));

        // Mapping conditionnel du fournisseur : null si l'article n'a pas de fournisseur associé
        SupplierResponseDTO supplierResponse = article.getSupplier() != null
                ? supplierService.toResponseDTO(article.getSupplier())
                : null;

        // Mapping conditionnel de la catégorie : null si l'article n'a pas de catégorie associée
        CategoryResponseDTO categoryResponse = article.getCategory() != null
                ? categoryService.toResponseDTO(article.getCategory())
                : null;

        // Construction du DTO de réponse avec toutes les données calculées et mappées
        return new ArticleDTO(
                article.getArtId(),
                article.getArtReference(),
                article.getArtName(),
                article.getArtDescription(),
                article.getArtPriceExcludeTaxes(), // Prix HT
                article.getArtStock(),
                tvaService.toResponseDto(article.getTva()),
                priceTTC,                          // Prix TTC calculé dynamiquement
                categoryResponse,
                supplierResponse
        );
    }
}