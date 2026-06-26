package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.SupplierDTO;
import com.mns.cda.saas_facturation.DTO.SupplierRequestDTO;

import java.util.List;

/**
 * Interface définissant le contrat du service métier de gestion des fournisseurs.
 *
 * <p>Cette interface centralise toutes les opérations disponibles sur la ressource
 * {@code Supplier}. L'implémentation concrète est injectée par Spring dans les classes
 * qui en dépendent, garantissant le découplage entre les couches controller et service.</p>
 *
 * <p>Contrairement à {@link IArticleService} qui utilise un {@link java.util.Optional}
 * pour les recherches par id, ce service lève directement une {@link SupplierNotFoundException}
 * lorsqu'un fournisseur est introuvable.</p>
 *
 * <p>La gestion des doublons de nom est assurée par la contrainte {@code unique = true}
 * sur le champ {@code splName} de l'entité {@code Supplier}, qui déclenche une
 * {@code DataIntegrityViolationException} interceptée par le {@code GlobalExceptionInterceptor}
 * — {@code ExistingSupplierException} n'est donc plus utilisée et a été retirée de cette interface.</p>
 *
 * @see com.mns.cda.saas_facturation.controller.SupplierController
 * @see SupplierDTO
 * @see SupplierRequestDTO
 */
public interface ISupplierService {

    /**
     * Exception levée lorsqu'un fournisseur recherché par son identifiant
     * n'existe pas en base de données.
     *
     * <p>Elle est levée par {@link #findById(Long)}, {@link #delete(Long)}
     * et {@link #modify(Long, SupplierRequestDTO)}, et remonte jusqu'au contrôleur
     * pour être gérée en réponse HTTP 404.</p>
     */
    public static class SupplierNotFoundException extends Exception {}

    /**
     * Récupère la liste complète de tous les fournisseurs en base de données.
     *
     * @return une {@link List} de {@link SupplierDTO} (vide si aucun fournisseur n'existe)
     */
    List<SupplierDTO> findAll();

    /**
     * Recherche un fournisseur par son identifiant unique.
     *
     * @param id l'identifiant unique du fournisseur à rechercher
     * @return le {@link SupplierDTO} correspondant au fournisseur trouvé
     * @throws SupplierNotFoundException si aucun fournisseur ne correspond à cet id
     */
    SupplierDTO findById(Long id) throws SupplierNotFoundException;

    /**
     * Crée un nouveau fournisseur en base de données à partir d'un DTO de requête.
     *
     * <p>Si un fournisseur portant le même nom existe déjà, la contrainte {@code unique = true}
     * sur {@code splName} déclenche une {@code DataIntegrityViolationException},
     * interceptée par le {@code GlobalExceptionInterceptor} en réponse HTTP 409.</p>
     *
     * @param dto les données du fournisseur à créer
     * @return le {@link SupplierDTO} du fournisseur créé avec son id généré
     */
    SupplierDTO create(SupplierRequestDTO dto);

    /**
     * Supprime un fournisseur par son identifiant unique.
     *
     * <p>Le service vérifie que le fournisseur existe avant toute tentative de suppression.</p>
     *
     * @param id l'identifiant unique du fournisseur à supprimer
     * @throws SupplierNotFoundException si aucun fournisseur ne correspond à cet id
     */
    void delete(Long id) throws SupplierNotFoundException;

    /**
     * Met à jour intégralement un fournisseur existant à partir de son identifiant.
     *
     * <p>Tous les champs du fournisseur sont remplacés par les valeurs fournies dans le DTO
     * (sémantique HTTP PUT). Si le nouveau nom est déjà utilisé par un autre fournisseur,
     * la contrainte {@code unique = true} déclenche un 409 via le {@code GlobalExceptionInterceptor}.</p>
     *
     * @param id  l'identifiant unique du fournisseur à modifier
     * @param dto les nouvelles données du fournisseur
     * @return le {@link SupplierDTO} du fournisseur après mise à jour
     * @throws SupplierNotFoundException si aucun fournisseur ne correspond à cet id
     */
    SupplierDTO modify(Long id, SupplierRequestDTO dto) throws SupplierNotFoundException;
}