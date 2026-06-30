package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.requestDTO.TvaRequestDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.TvaResponseDTO;
import com.mns.cda.saas_facturation.model.Tva;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Interface définissant le contrat du service métier de gestion des taux de TVA.
 *
 * <p>Cette interface centralise toutes les opérations disponibles sur la ressource
 * {@code Tva}. L'implémentation concrète est injectée par Spring dans les classes
 * qui en dépendent, garantissant le découplage entre les couches controller et service.</p>
 *
 * <p>Ce service propose deux types de mise à jour, contrairement aux autres services
 * qui n'en proposent qu'un :</p>
 * <ul>
 *   <li>{@link #update(long, TvaRequestDTO)} — remplacement complet (HTTP PUT)</li>
 *   <li>{@link #patchTaux(long, BigDecimal)} — mise à jour du taux uniquement (HTTP PATCH)</li>
 * </ul>
 *
 * <p>Comme {@link ICategoryService}, ce service retourne directement l'entité
 * {@link Tva} sans couche DTO de sortie dédiée.</p>
 *
 * @see com.mns.cda.saas_facturation.controller.TvaController
 * @see Tva
 * @see TvaRequestDTO
 */
public interface ITvaService {

//    TvaResponseDTO toResponseDto(Tva tva);

    /**
     * Exception levée lorsqu'un taux de TVA recherché par son identifiant
     * n'existe pas en base de données.
     *
     * <p>Elle est levée par {@link #patchTaux(long, BigDecimal)} et {@link #update(long, TvaRequestDTO)},
     * et remonte jusqu'au contrôleur via {@code throws} pour être gérée en réponse HTTP 404.</p>
     */
    public static class TvaNotFoundException extends Exception {}

    /**
     * Récupère la liste complète de tous les taux de TVA en base de données.
     *
     * @return une {@link List} de {@link Tva} (vide si aucune TVA n'existe)
     */
    List<Tva> findAll();

    /**
     * Recherche un taux de TVA par son identifiant unique.
     *
     * <p>Retourne un {@link Optional} vide si aucune TVA ne correspond à l'id fourni,
     * sans lever d'exception — la vérification est laissée à la charge du contrôleur.</p>
     *
     * @param id l'identifiant unique de la TVA à rechercher
     * @return un {@link Optional} contenant la {@link Tva} si trouvée, vide sinon
     */
    Optional<Tva> findById(Long id);

    /**
     * Crée un nouveau taux de TVA en base de données à partir d'un DTO de requête.
     *
     * @param tva les données de la TVA à créer
     * @return la {@link Tva} créée avec son id généré
     */
    Tva create(TvaRequestDTO tva);

    /**
     * Supprime un taux de TVA par son identifiant unique.
     *
     * <p>L'existence de la TVA est vérifiée en amont dans le contrôleur
     * avant d'appeler cette méthode.</p>
     *
     * @param id l'identifiant unique de la TVA à supprimer
     */
    void delete(Long id);

    /**
     * Met à jour uniquement le taux d'une TVA existante.
     *
     * <p>Cette méthode correspond à une mise à jour partielle (sémantique HTTP PATCH) :
     * seul le champ {@code tvaTaux} est modifié, le libellé {@code tvaName}
     * restant inchangé.</p>
     *
     * @param id      l'identifiant unique de la TVA à modifier
     * @param tvaTaux la nouvelle valeur du taux à appliquer
     * @return la {@link Tva} après mise à jour du taux
     * @throws TvaNotFoundException si aucune TVA ne correspond à l'id fourni
     */
    Tva patchTaux(long id, BigDecimal tvaTaux) throws TvaNotFoundException;

    /**
     * Met à jour intégralement un taux de TVA existant à partir de son identifiant.
     *
     * <p>Tous les champs de la TVA sont remplacés par les valeurs fournies dans le DTO
     * (sémantique HTTP PUT) : le libellé {@code tvaName} et le taux {@code tvaTaux}
     * sont tous les deux écrasés.</p>
     *
     * @param id  l'identifiant unique de la TVA à modifier
     * @param tva le DTO contenant les nouvelles valeurs
     * @return la {@link Tva} après mise à jour complète
     * @throws TvaNotFoundException si aucune TVA ne correspond à l'id fourni
     */
    Tva update(long id, TvaRequestDTO tva) throws TvaNotFoundException;
}