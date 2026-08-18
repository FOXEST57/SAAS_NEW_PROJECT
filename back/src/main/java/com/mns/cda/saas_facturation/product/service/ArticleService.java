package com.mns.cda.saas_facturation.product.service;

import com.mns.cda.saas_facturation.product.DTO.requestDTO.ArticleRequestDTO;
import com.mns.cda.saas_facturation.product.DTO.requestDTO.InventoryRequestDTO;
import com.mns.cda.saas_facturation.product.DTO.requestDTO.MakerReferenceRequestDTO;
import com.mns.cda.saas_facturation.product.DTO.requestDTO.SupplierReferenceRequestDTO;
import com.mns.cda.saas_facturation.product.DTO.updateDTO.ArticleActiveUpdateDTO;
import com.mns.cda.saas_facturation.product.DTO.updateDTO.ArticleUpdateDTO;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.product.DTO.ArticleDTO;
import com.mns.cda.saas_facturation.product.DTO.InventoryDTO;
import com.mns.cda.saas_facturation.product.Iservice.IArticleService;
import com.mns.cda.saas_facturation.product.model.*;
import com.mns.cda.saas_facturation.product.repository.*;
import com.mns.cda.saas_facturation.referencement.ReferenceCounterService;
import com.mns.cda.saas_facturation.referencement.ReferenceType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.mns.cda.saas_facturation.product.mapper.ArticleMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
 *   <li>convertir les entités JPA en DTOs via
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

    private final ArticleRepository articleRepository;
    private final TvaRepository tvaRepository;
    private final SupplierRepository supplierRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierReferenceRepository supplierReferenceRepository;
    private final ArticleMapper articleMapper;
    private final MakerReferenceRepository makerReferenceRepository;
    private final InventoryService inventoryService;
    private final InventoryRepository inventoryRepository;
    private final MakerRepository makerRepository;
    private final DeliveryRepository deliveryRepository;
    private final ReferenceCounterService referenceCounterService;

    /**
     * Récupère la liste complète de tous les articles en base de données.
     *
     * <p>Chaque entité {@link Article} est convertie en {@link ArticleDTO}
     * via {@link ArticleMapper}, ce qui inclut le calcul du prix TTC.</p>
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
                .map(articleMapper::toDTO)           // Convertit chaque entité Article en ArticleDTO
                .toList();                  // Collecte le résultat dans une liste immuable
    }

    @Override
    public List<ArticleDTO> findAllIsActive() {
        return articleRepository.findAll()
                .stream()
                .filter(Article::isActive)
                .map(articleMapper::toDTO)
                .toList();
    }

    /**
     * Recherche un article par son identifiant unique.
     *
     * <p>Retourne un {@link Optional} vide si aucun article ne correspond au splId fourni,
     * sans lever d'exception — la vérification est laissée à la charge du contrôleur.</p>
     *
     * @param id l'identifiant unique de l'article à rechercher
     * @return un {@link Optional} contenant l'{@link ArticleDTO} si trouvé, vide sinon
     */
    @Override
    public Optional<ArticleDTO> findById(Long id) {
        return articleRepository.findById(id) // Recherche par clé primaire — retourne un Optional
                .map(articleMapper::toDTO);            // Si présent, convertit l'entité en DTO avant retour
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
     *   <li>Mapping de l'entité sauvegardée vers un {@link ArticleDTO} via </li>
     * </ol>
     *
     * @param dto les données de l'article à créer
     * @return un {@link ArticleDTO} contenant les informations de l'article créé,
     *         incluant la TVA, la catégorie et le fournisseur associé (ou {@code null} si aucun fournisseur)
     * @throws ResourceNotFoundException si la TVA, le fournisseur ou la catégorie référencée n'existe pas en base
     */
    @Override
    @Transactional(rollbackOn = ResourceNotFoundException.class)
    public ArticleDTO create(ArticleRequestDTO dto) throws ResourceNotFoundException {

        // La TVA est obligatoire : orElseThrow lève l'exception si le splId est inconnu
        Tva articleTva = tvaRepository.findById(dto.tvaId())
                .orElseThrow(() -> new ResourceNotFoundException("TVA non existante"));


        // Construction de l'entité Article : le splId est null car généré automatiquement par la BDD (@GeneratedValue) avec une liste vide pour les suppliers
        Article article = new Article();

                article.setArtReference(referenceCounterService
                        .generateReference(null, ReferenceType.ARTICLE));
                article.setArtName(dto.artName());
                article.setArtDescription(dto.artDescription());
                article.setArtPriceExcludeTaxes(dto.artPriceExcludeTaxes());
                article.setTva(articleTva);


        // Création de l'article tel qu'il est pour créer l'entité et lui associer une ID
        article = articleRepository.save(article);

        // Construction d'un inventaire initial
        InventoryRequestDTO inventoryRequestDTO = new InventoryRequestDTO(0, article.getArtId());
        InventoryDTO inventoryDTO = inventoryService.create(inventoryRequestDTO);
        Inventory inventory = inventoryRepository.findById(inventoryDTO.invId()).orElseThrow(() -> new ResourceNotFoundException("Inventaire non existant"));
        article.getInventories().add(inventory);
        article = articleRepository.save(article);

        if (dto.categoryIds() != null && !dto.categoryIds().isEmpty()) {
            List<Category> categories = new ArrayList<>(dto.categoryIds()
                    .stream()
                    .map(catId -> categoryRepository.findById(catId)
                            .orElseThrow(() -> new ResourceNotFoundException("Catégorie non existante")))
                    .toList());

            article.setCategories(categories);
        }


        //Vérification si la request contient ou non des relations article_supplier ajouter ou non si elle n'en contient pas, on laisse la liste à vide sinon
        // on boucle pour chaque supplier ajouté pour créer la relation article_supplier nécessite donc le stock et la référence produit fournisseur.
        if (dto.suppliers() != null) {
            for (SupplierReferenceRequestDTO splRef : dto.suppliers()) {

                Supplier supplier = supplierRepository.findById(splRef.supplierId())
                        .orElseThrow(() -> new ResourceNotFoundException("Fournisseur non existant"));

                SupplierReference link = new SupplierReference(
                        new SupplierReference.SupplierReferenceId(article.getArtId(), supplier.getSplId()),
                        article,
                        supplier,
                        splRef.deliveryIds().stream().map(id ->
                                deliveryRepository.findById(id).orElseThrow(
                                        () -> new ResourceNotFoundException("Le fournisseur avec l'id " + id + " n'existe pas")
                                )).toList(),
                        splRef.splRefReference()
                );
                //On sauvegarde la nouvelle relation qu'on vient de créer
                supplierReferenceRepository.save(link);
            }

        }
        //Vérification si la request contient ou non des relations article_maker ajouter ou non si elle n'en contient pas, on laisse la liste à vide sinon
        // on boucle pour chaque maker ajouté pour créer la relation maker_supplier nécessite donc le stock et la référence produit fabricant.
        if (dto.makers() != null) {
            for (MakerReferenceRequestDTO mkrRef : dto.makers()) {

                Maker maker = makerRepository.findById(mkrRef.mkrId())
                        .orElseThrow(() -> new ResourceNotFoundException("Fabricant non existant"));

                MakerReference link = new MakerReference(
                        new MakerReference.MakerReferenceId(article.getArtId(), maker.getMkrId()),
                        article,
                        maker,
                        mkrRef.deliveryIds().stream().map(id ->
                                deliveryRepository.findById(id).orElseThrow(
                                        () -> new ResourceNotFoundException("La commande avec l'id " + id + " n'existe pas")
                                )).toList(),
                        mkrRef.artMkrReference()
                );
                //On sauvegarde la nouvelle relation qu'on vient de créer
                makerReferenceRepository.save(link);
            }
        }

        // save() persiste l'entité et retourne la version avec le splId généré par la base
        return articleMapper.toDTO(articleRepository.save(article));
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
    @Transactional(rollbackOn = ResourceNotFoundException.class)
    public void delete(Long id) throws ResourceNotFoundException {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article non existant"));

        supplierReferenceRepository.deleteBySplRefId_ArticleId(id);
        makerReferenceRepository.deleteByMkrRefId_ArticleId(id);

        article.getCategories().clear();
        articleRepository.save(article);

        // deleteById génère un DELETE FROM article WHERE art_id = ?
        articleRepository.deleteById(id);
    }

    /**
     * Met à jour intégralement un article existant à partir de son identifiant (comportement PUT).
     *
     * <p>Les étapes réalisées sont les suivantes :</p>
     * <ol>
     *   <li>Récupération de l'entité existante — lève {@link ResourceNotFoundException} si absente</li>
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
     * @throws ResourceNotFoundException si l'article ciblé, le fournisseur référencé, la TVA référencée ou la catégorie référencée n'existe pas en base
     */
    @Override
    public ArticleDTO update(long id, ArticleUpdateDTO dto)
            throws ResourceNotFoundException {

        // Récupération de l'entité existante : on travaille sur l'objet BDD pour conserver son splId
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article non existant"));

        // Mise à jour des champs primitifs : chaque setter modifie la valeur en mémoire
        // JPA détecte les changements et génère un UPDATE SQL lors du save()
        article.setArtReference(dto.artReference());
        article.setArtName(dto.artName());
        article.setArtDescription(dto.artDescription());
        article.setArtPriceExcludeTaxes(dto.artPriceExcludeTaxes());

        // Mise à jour de la relation TVA : on charge l'entité Tva depuis sa clé étrangère
        Tva tva = tvaRepository.findById(dto.tvaId())
                .orElseThrow(() -> new ResourceNotFoundException("TVA non existante"));
        article.setTva(tva);

        // La catégorie est optionnelle : on ne met à jour la relation que si un splId est fourni
        if (dto.categoryIds() != null) {
            List<Category> categories = new ArrayList<>(dto.categoryIds().stream()
                    .map(catId -> categoryRepository.findById(catId)
                            .orElseThrow(() -> new ResourceNotFoundException("Catégorie non existante")))
                    .toList());

            article.setCategories(categories);
        }

        // Persistance des modifications puis conversion en DTO pour la réponse HTTP
        return articleMapper.toDTO(articleRepository.save(article));
    }

    @Override
    public ArticleDTO updateActive(long id, ArticleActiveUpdateDTO dto) throws ResourceNotFoundException {

        // Récupération de l'entité existante : on travaille sur l'objet BDD pour conserver son splId
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article non existant"));

        article.setActive(dto.isActive());
        return articleMapper.toDTO(articleRepository.save(article));

    }


}