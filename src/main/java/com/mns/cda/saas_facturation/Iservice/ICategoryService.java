package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.CategoryRequestDTO;
import com.mns.cda.saas_facturation.model.Category;

import java.util.List;
import java.util.Optional;

/**
 * Interface définissant le contrat du service métier de gestion des catégories.
 *
 * <p>Cette interface centralise toutes les opérations disponibles sur la ressource
 * {@code Category}. L'implémentation concrète est injectée par Spring dans les classes
 * qui en dépendent, garantissant le découplage entre les couches controller et service.</p>
 *
 * <p>Contrairement à {@link IArticleService}, ce service retourne directement l'entité
 * {@link Category} sans couche DTO de sortie dédiée.</p>
 *
 * <p>Les exceptions métier sont définies ici comme classes statiques internes,
 * ce qui les rattache sémantiquement au domaine catégorie.</p>
 *
 * @see com.mns.cda.saas_facturation.controller.CategoryController
 * @see Category
 * @see CategoryRequestDTO
 */
public interface ICategoryService {

    /**
     * Exception levée lorsqu'une catégorie recherchée par son identifiant
     * n'existe pas en base de données.
     *
     * <p>Elle est typiquement levée par {@link #update(long, String)} et remonte
     * jusqu'au contrôleur via {@code throws} pour être gérée en réponse HTTP 404.</p>
     */
    public static class CategoryNotFoundException extends Exception {}

    /**
     * Récupère la liste complète de toutes les catégories en base de données.
     *
     * @return une {@link List} de {@link Category} (vide si aucune catégorie n'existe)
     */
    List<Category> findAll();

    /**
     * Recherche une catégorie par son identifiant unique.
     *
     * <p>Retourne un {@link Optional} vide si aucune catégorie ne correspond à l'id fourni,
     * sans lever d'exception — la vérification est laissée à la charge du contrôleur.</p>
     *
     * @param id l'identifiant unique de la catégorie à rechercher
     * @return un {@link Optional} contenant la {@link Category} si trouvée, vide sinon
     */
    Optional<Category> findById(Long id);

    /**
     * Crée une nouvelle catégorie en base de données à partir d'un DTO de requête.
     *
     * @param dto les données de la catégorie à créer
     * @return la {@link Category} créée avec son id généré
     */
    Category create(CategoryRequestDTO dto);

    /**
     * Supprime une catégorie par son identifiant unique.
     *
     * <p>L'existence de la catégorie est vérifiée en amont dans le contrôleur
     * avant d'appeler cette méthode.</p>
     *
     * @param id l'identifiant unique de la catégorie à supprimer
     */
    void delete(Long id);

    /**
     * Met à jour le nom d'une catégorie existante à partir de son identifiant.
     *
     * <p>Seul le champ {@code catName} est modifié. Le service vérifie que la catégorie
     * existe bien en base avant toute modification.</p>
     *
     * @param id      l'identifiant unique de la catégorie à modifier
     * @param catName le nouveau nom à appliquer à la catégorie
     * @return la {@link Category} après mise à jour
     * @throws CategoryNotFoundException si aucune catégorie ne correspond à l'id fourni
     */
    Category update(long id, String catName) throws CategoryNotFoundException;
}