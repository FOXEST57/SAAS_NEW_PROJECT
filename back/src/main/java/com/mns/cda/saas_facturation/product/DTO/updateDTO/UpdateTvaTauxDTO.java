package com.mns.cda.saas_facturation.product.DTO.updateDTO;

import com.mns.cda.saas_facturation.product.DTO.requestDTO.TvaRequestDTO;
import com.mns.cda.saas_facturation.product.controller.TvaController;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

/**
 * DTO d'entrée représentant les données reçues par l'API pour modifier uniquement
 * le taux d'une TVA existante.
 *
 * <p>Ce record est utilisé exclusivement dans le cadre d'une mise à jour partielle
 * (sémantique HTTP PATCH sur {@code PATCH /tva/{splId}}) : seul le champ {@code tvaTaux}
 * est transmis, les autres champs de l'entité {@code Tva} restant inchangés.</p>
 *
 * <p>Il se distingue de {@link TvaRequestDTO} qui lui est utilisé pour un remplacement
 * complet (HTTP PUT) et embarque en plus le libellé {@code tvaName}.</p>
 *
 * <p>Le taux accepte la valeur {@code 0.0} (inclusive par défaut) afin de permettre
 * de représenter une TVA à 0% (ex : produits exonérés).</p>
 *
 * @param tvaTaux nouveau taux de TVA à appliquer — doit être supérieur ou égal à 0.0
 *
 * @see TvaController
 * @see TvaRequestDTO
 */
public record UpdateTvaTauxDTO(
        @DecimalMin(value = "0.0") BigDecimal tvaTaux
) {
}