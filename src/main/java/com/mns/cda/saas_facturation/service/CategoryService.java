package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.CategoryDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.CategoryRequestDTO;
import com.mns.cda.saas_facturation.Iservice.ICategoryService;
import com.mns.cda.saas_facturation.model.Category;
import com.mns.cda.saas_facturation.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import com.mns.cda.saas_facturation.mapper.CategoryMapper;
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
 *   <li>convertir les entités {@link Category} en {@link CategoryDTO} via
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
@Service             // Déclare cette classe comme un composant Spring de couche service
@RequiredArgsConstructor // Lombok génère un constructeur avec tous les champs final (injection automatique)
public class CategoryService implements ICategoryService {

    // Repository Spring Data JPA : gère tous les accès base de données pour l'entité Category
    protected final CategoryRepository categoryRepository;

    protected final CategoryMapper categoryMapper;

    /**
     * Récupère la liste complète de toutes les catégories en base de données.
     *
     * <p>Chaque entité {@link Category} est convertie en {@link CategoryDTO}
     * via , ce qui inclut la résolution de la hiérarchie
     * parent / enfants.</p>
     *
     * @return une {@link List} de {@link CategoryDTO} (vide si aucune catégorie n'existe)
     */
    @Override
    public List<CategoryDTO> findAll() {
        return categoryRepository.findAll() // SELECT * FROM category
                .stream()
                .map(categoryMapper::toDTO)           // Conversion entité → DTO pour chaque élément
                .toList();
    }

    /**
     * Recherche une catégorie par son identifiant unique.
     *
     * <p>Retourne un {@link Optional} vide si aucune catégorie ne correspond à l'splId fourni,
     * sans lever d'exception — la vérification est laissée à la charge du contrôleur.</p>
     *
     * @param id l'identifiant unique de la catégorie à rechercher
     * @return un {@link Optional} contenant le {@link CategoryDTO} si trouvé, vide sinon
     */
    @Override
    public Optional<CategoryDTO> findById(Long id) {
        return categoryRepository.findById(id) // SELECT * FROM category WHERE cat_id = ?
                .map(categoryMapper::toDTO);             // Conversion entité → DTO si l'Optional n'est pas vide
    }

    /**
     * Crée une nouvelle catégorie en base de données à partir des données fournies dans le DTO.
     *
     * <p>La catégorie parente est obligatoire : si l'splId {@code parentId} fourni dans le DTO
     * ne correspond à aucune catégorie existante, une {@link CategoryNotFoundException}
     * est levée avant toute persistance.</p>
     *
     * @param dto les données de la catégorie à créer (nom et splId de la catégorie parente)
     * @return le {@link CategoryDTO} de la catégorie créée avec son splId généré
     * @throws CategoryNotFoundException si la catégorie parente référencée n'existe pas en base
     */
    @Override
    public CategoryDTO create(CategoryRequestDTO dto) throws CategoryNotFoundException {

        // Vérification et récupération de la catégorie parente — obligatoire
        // orElseThrow lève CategoryNotFoundException si l'splId est inconnu
        Category categoryParent = null;
        if (dto.parentId() != null) {
            categoryParent = categoryRepository.findById(dto.parentId())
                    .orElseThrow(ICategoryService.CategoryNotFoundException::new);
        }


        // Construction de la nouvelle entité Category à partir du DTO
        // L'splId n'est pas défini : il sera généré automatiquement par la base via @GeneratedValue
        Category category = new Category();
        category.setCatName(dto.catName());
        category.setCatSlug(dto.catSlug());
        category.setCatParent(categoryParent); // Association vers la catégorie parente

        // INSERT INTO category — save() retourne l'entité avec son splId généré, puis conversion en DTO
        return categoryMapper.toDTO(categoryRepository.save(category));
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
        // DELETE FROM category WHERE cat_id = ?
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
     * @param categoryToUpdate le DTO contenant le nouveau nom et le nouvel splId de catégorie parente
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

        // Vérification et récupération de la nouvelle catégorie parente
        // lève CategoryNotFoundException si l'splId est inconnu
        Category categoryParent = categoryRepository.findById(categoryToUpdate.parentId())
                .orElseThrow(CategoryNotFoundException::new);

        // Mise à jour des champs — JPA détecte les changements et génère un UPDATE lors du save()
        category.setCatName(categoryToUpdate.catName());
        category.setCatSlug(categoryToUpdate.catSlug());
        category.setCatParent(categoryParent);

        // UPDATE category SET cat_name = ?, cat_parent_id = ? WHERE cat_id = ?
        return categoryMapper.toDTO(categoryRepository.save(category));
    }
}