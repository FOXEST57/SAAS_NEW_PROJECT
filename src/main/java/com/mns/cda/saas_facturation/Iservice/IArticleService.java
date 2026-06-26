package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.ArticleRequestDTO;
import com.mns.cda.saas_facturation.DTO.ArticleDTO;

import java.util.List;
import java.util.Optional;

/**
 * Interface définissant le contrat du service métier de gestion des articles.
 *
 * <p>Cette interface centralise toutes les opérations disponibles sur la ressource
 * {@code Article}. L'implémentation concrète est injectée par Spring dans les classes
 * qui en dépendent, garantissant le découplage entre les couches controller et service.</p>
 *
 * <p>Les exceptions métier sont définies ici comme classes statiques internes,
 * ce qui les rattache sémantiquement au domaine article et évite de les disperser
 * dans des fichiers séparés.</p>
 *
 * @see com.mns.cda.saas_facturation.controller.ArticleController
 * @see ArticleDTO
 * @see ArticleRequestDTO
 */
public interface IArticleService {

    /**
     * Exception levée lorsqu'un article recherché par son identifiant
     * n'existe pas en base de données.
     *
     * <p>Elle est typiquement levée par {@link #findById(Long)} ou {@link #update(long, ArticleRequestDTO)}
     * et remonte jusqu'au contrôleur via {@code throws} pour être gérée
     * en réponse HTTP 404.</p>
     */
    public static class ArticleNotFoundException extends Exception {}

    /**
     * Récupère la liste complète de tous les articles en base de données.
     *
     * @return une {@link List} de {@link ArticleDTO} (vide si aucun article n'existe)
     */
    List<ArticleDTO> findAll();

    /**
     * Recherche un article par son identifiant unique.
     *
     * <p>Retourne un {@link Optional} vide si aucun article ne correspond à l'id fourni,
     * sans lever d'exception — la vérification est laissée à la charge du contrôleur.</p>
     *
     * @param id l'identifiant unique de l'article à rechercher
     * @return un {@link Optional} contenant l'{@link ArticleDTO} si trouvé, vide sinon
     */
    Optional<ArticleDTO> findById(Long id);

    /**
     * Récupère la liste des articles associés à un fournisseur spécifique.
     *
     * <p>Si le fournisseur identifié par {@code id} n'existe pas en base,
     * une {@link ISupplierService.SupplierNotFoundException} est levée
     * avant tout accès aux articles.</p>
     *
     * @param id l'identifiant unique du fournisseur dont on veut les articles
     * @return une {@link List} de {@link ArticleDTO} associés à ce fournisseur
     *         (vide si le fournisseur n'a aucun article)
     * @throws ISupplierService.SupplierNotFoundException si le fournisseur n'existe pas en base
     */
    List<ArticleDTO> findBySupplier(Long id) throws ISupplierService.SupplierNotFoundException;

    /**
     * Crée un nouvel article en base de données à partir d'un DTO de requête.
     *
     * <p>Le service vérifie que la TVA et le fournisseur référencés dans le DTO
     * existent bien en base avant de persister l'article.</p>
     *
     * @param dto les données de l'article à créer
     * @return l'{@link ArticleDTO} de l'article créé, avec son id généré
     * @throws ITvaService.TvaNotFoundException           si la TVA référencée n'existe pas en base
     * @throws ISupplierService.SupplierNotFoundException si le fournisseur référencé n'existe pas en base
     */
    ArticleDTO create(ArticleRequestDTO dto) throws ITvaService.TvaNotFoundException, ISupplierService.SupplierNotFoundException, ICategoryService.CategoryNotFoundException;

    /**
     * Supprime un article par son identifiant unique.
     *
     * <p>L'existence de l'article est vérifiée en amont dans le contrôleur
     * avant d'appeler cette méthode.</p>
     *
     * @param id l'identifiant unique de l'article à supprimer
     */
    void delete(Long id);

    /**
     * Met à jour intégralement un article existant à partir de son identifiant.
     *
     * <p>Tous les champs de l'article sont remplacés par les valeurs fournies dans le DTO
     * (sémantique HTTP PUT). Le service vérifie que l'article, la TVA et le fournisseur
     * référencés existent bien en base avant toute modification.</p>
     *
     * @param id  l'identifiant unique de l'article à modifier
     * @param dto les nouvelles données de l'article
     * @return l'{@link ArticleDTO} de l'article après mise à jour
     * @throws ArticleNotFoundException                   si l'article ciblé n'existe pas en base
     * @throws ITvaService.TvaNotFoundException           si la TVA référencée n'existe pas en base
     * @throws ISupplierService.SupplierNotFoundException si le fournisseur référencé n'existe pas en base
     */
    ArticleDTO update(long id, ArticleRequestDTO dto) throws ArticleNotFoundException, ITvaService.TvaNotFoundException, ISupplierService.SupplierNotFoundException, ICategoryService.CategoryNotFoundException;
}