package com.mns.cda.saas_facturation.DTO.responseDTO;

import java.math.BigDecimal;

/**
 * DTO de réponse représentant une TVA (Taxe sur la Valeur Ajoutée).
 * <p>
 * Ce record est utilisé pour exposer les informations d'une TVA au client
 * (frontend) après une requête (lecture, création, mise à jour, etc.).
 * Il s'agit d'un objet en lecture uniquement, destiné à transporter les
 * données depuis l'entité {@code Tva} vers la couche de présentation,
 * sans exposer directement l'entité JPA.
 * </p>
 *
 * <p>
 * Étant un {@code record}, cette classe est immuable : tous les champs
 * sont {@code final} et générés automatiquement avec leurs accesseurs
 * (ex : {@code tvaId()}, {@code tvaName()}, {@code tvaTaux()}),
 * son constructeur, ainsi que les méthodes {@code equals()},
 * {@code hashCode()} et {@code toString()}.
 * </p>
 *
 * @param tvaId    identifiant unique de la TVA en base de données
 * @param tvaName  libellé/nom de la TVA (ex : "TVA normale", "TVA réduite")
 * @param tvaTaux  taux de la TVA exprimé en pourcentage (ex : 20.00 pour 20%)
 *                 (utilisation de {@link BigDecimal} pour garantir la précision
 *                 des calculs financiers, évitant les erreurs d'arrondi liées
 *                 aux types {@code float}/{@code double})
 */
public record TvaResponseDTO(
        Long tvaId,
        String tvaName,
        BigDecimal tvaTaux
) {

}