package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.CategoryDTO;
import com.mns.cda.saas_facturation.DTO.CategoryRequestDTO;
import com.mns.cda.saas_facturation.DTO.CategoryResponseDTO;
import com.mns.cda.saas_facturation.Iservice.ICategoryService;
import com.mns.cda.saas_facturation.model.Category;
import com.mns.cda.saas_facturation.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service métier gérant la logique liée aux catégories ({@link Category}).
 *
 * <p>Implémente {@link ICategoryService} et délègue toutes les opérations
 * de persistance à {@link CategoryRepository}.</p>
 *
 * <p>Ce service est responsable de :</p>
 * <ul>
 *   <li>gérer la structure arborescente des catégories (parent / enfants)</li>
 *   <li>convertir les entités {@link Category} en {@link CategoryDTO} via {@link #toDTO(Category)}
 *       pour éviter les boucles infinies JSON liées à la relation bidirectionnelle
 *       auto-référentielle</li>
 *   <li>vérifier l'existence de la catégorie parente avant toute création ou modification</li>
 * </ul>
 *
 * <p>L'injection de dépendances se fait via le constructeur,
 * généré automatiquement par {@code @RequiredArgsConstructor} de Lombok.</p>
 *
 * @see ICategoryService
 * @see CategoryRepository
 * @see CategoryDTO
 * @see CategoryRequestDTO
 */
@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    protected final CategoryRepository categoryRepository;

    /**
     * Récupère la liste complète de toutes les catégories en base de données.
     *
     * <p>Chaque entité {@link Category} est convertie en {@link CategoryDTO}
     * via {@link #toDTO(Category)}, ce qui inclut la résolution de la hiérarchie
     * parent / enfants.</p>
     *
     * @return une {@link List} de {@link CategoryDTO} (vide si aucune catégorie n'existe)
     */
    @Override
    public List<CategoryDTO> findAll() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toDTO) // Conversion entité → DTO pour chaque élément du flux
                .toList();
    }

    /**
     * Recherche une catégorie par son identifiant unique.
     *
     * <p>Retourne un {@link Optional} vide si aucune catégorie ne correspond à l'id fourni,
     * sans lever d'exception — la vérification est laissée à la charge du contrôleur.</p>
     *
     * @param id l'identifiant unique de la catégorie à rechercher
     * @return un {@link Optional} contenant le {@link CategoryDTO} si trouvé, vide sinon
     */
    @Override
    public Optional<CategoryDTO> findById(Long id) {
        return categoryRepository.findById(id)
                .map(this::toDTO); // Conversion entité → DTO si l'Optional n'est pas vide
    }

    /**
     * Crée une nouvelle catégorie en base de données à partir des données fournies dans le DTO.
     *
     * <p>La catégorie parente est obligatoire : si l'id {@code parentId} fourni dans le DTO
     * ne correspond à aucune catégorie existante, une {@link CategoryNotFoundException}
     * est levée avant toute persistance.</p>
     *
     * @param dto les données de la catégorie à créer (nom et id de la catégorie parente)
     * @return le {@link CategoryDTO} de la catégorie créée avec son id généré
     * @throws CategoryNotFoundException si la catégorie parente référencée n'existe pas en base
     */
    @Override
    public CategoryDTO create(CategoryRequestDTO dto) throws CategoryNotFoundException {

        // Récupération de la catégorie parente — obligatoire, lève CategoryNotFoundException si absente
        Category categoryParent = categoryRepository.findById(dto.parentId())
                .orElseThrow(ICategoryService.CategoryNotFoundException::new);

        // Construction de la nouvelle catégorie avec son nom et sa catégorie parente
        Category category = new Category();
        category.setCatName(dto.catName());
        category.setCatParent(categoryParent);

        return toDTO(categoryRepository.save(category));
    }

    /**
     * Supprime une catégorie par son identifiant unique.
     *
     * <p>L'existence de la catégorie est vérifiée en amont dans le contrôleur
     * avant d'appeler cette méthode.</p>
     *
     * @param id l'identifiant unique de la catégorie à supprimer
     */
    @Override
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

    /**
     * Met à jour une catégorie existante à partir de son identifiant.
     *
     * <p>Le nom et la catégorie parente sont mis à jour. La catégorie parente
     * est vérifiée en base avant toute modification — si elle est introuvable,
     * une {@link CategoryNotFoundException} est levée.</p>
     *
     * @param id               l'identifiant unique de la catégorie à modifier
     * @param categoryToUpdate le DTO contenant le nouveau nom et le nouvel id de catégorie parente
     * @return le {@link CategoryDTO} de la catégorie après mise à jour
     * @throws CategoryNotFoundException si la catégorie ciblée ou la catégorie parente
     *                                   référencée n'existe pas en base
     */
    @Override
    public CategoryDTO update(long id, CategoryRequestDTO categoryToUpdate)
            throws CategoryNotFoundException {

        // Récupération de la catégorie à modifier — lève CategoryNotFoundException si absente
        Category category = categoryRepository.findById(id)
                .orElseThrow(CategoryNotFoundException::new);

        // Récupération de la nouvelle catégorie parente — lève CategoryNotFoundException si absente
        Category categoryParent = categoryRepository.findById(categoryToUpdate.parentId())
                .orElseThrow(CategoryNotFoundException::new);

        category.setCatName(categoryToUpdate.catName());
        category.setCatParent(categoryParent);

        return toDTO(categoryRepository.save(category));
    }

    /**
     * Convertit une entité {@link Category} en {@link CategoryDTO}.
     *
     * <p>Cette méthode est utilisée en interne par le service pour éviter
     * d'exposer les entités JPA directement à la couche contrôleur, et surtout
     * pour éviter les boucles infinies JSON liées à la relation bidirectionnelle
     * auto-référentielle ({@code catParent} / {@code catChildren}).</p>
     *
     * <p>La stratégie adoptée :</p>
     * <ul>
     *   <li>le parent est représenté par son nom uniquement (pas de récursion vers le haut)</li>
     *   <li>les enfants sont récursivement convertis en {@link CategoryDTO}
     *       (récursion vers le bas uniquement)</li>
     *   <li>si la liste des enfants est {@code null}, une liste vide est retournée</li>
     * </ul>
     *
     * <p>Cette méthode est déclarée {@code protected} pour être accessible depuis
     * {@link ArticleService} qui en a besoin lors du mapping des articles.</p>
     *
     * @param category l'entité catégorie à convertir (ne doit pas être {@code null})
     * @return un {@link CategoryDTO} contenant l'id, le nom, le nom du parent
     *         et la liste des enfants convertis
     */

    @Override
    public CategoryDTO toDTO(Category category) {
        return new CategoryDTO(
                category.getCatId(),
                category.getCatName(),
                // Le parent est représenté par son nom uniquement pour éviter la récursion vers le haut
                category.getCatParent() != null ? category.getCatParent().getCatName() : null,
                // Les enfants sont récursivement convertis — liste vide si null
                category.getCatChildren() != null
                        ? category.getCatChildren().stream()
                        .map(this::toDTO)
                        .toList()
                        : List.of()
        );
    }

    @Override
    public CategoryResponseDTO toResponseDTO(Category category) {

        String catParentName = null;

        if(category.getCatParent() != null) {
            catParentName = category.getCatParent().getCatName();
        }

        return new CategoryResponseDTO(
                category.getCatId(),
                category.getCatName(),
                catParentName
        );
    }
}