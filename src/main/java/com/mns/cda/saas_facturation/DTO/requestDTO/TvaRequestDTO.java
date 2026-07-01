package com.mns.cda.saas_facturation.DTO.requestDTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

/**
 * DTO d'entrée représentant les données reçues par l'API pour créer ou modifier un taux de TVA.
 *
 * <p>Ce record est utilisé exclusivement en <strong>entrée</strong> (requête HTTP) :
 * il encapsule et valide les données envoyées par le client avant qu'elles
 * n'atteignent la couche service.</p>
 *
 * <p>Les contraintes appliquées sur chaque champ sont vérifiées automatiquement
 * par Spring grâce à l'annotation {@code @Valid} présente dans le contrôleur.
 * En cas d'échec, le {@code GlobalExceptionInterceptor} retourne un 400
 * avec le détail des champs invalides.</p>
 *
 * <p>Contrairement à {@link ArticleRequestDTO} qui utilise
 * {@code @DecimalMin(value = "0.0", inclusive = false)}, le taux ici accepte
 * la valeur {@code 0.0} (inclusive par défaut), ce qui permet de représenter
 * une TVA à 0% (ex : produits exonérés).</p>
 *
 * @param tvaName  libellé du taux de TVA (ex : "TVA 20%") — ne doit pas être vide
 * @param tvaTaux  valeur du taux de TVA — doit être supérieure ou égale à 0.0
 *
 * @see com.mns.cda.saas_facturation.controller.TvaController
 * @see com.mns.cda.saas_facturation.Iservice.ITvaService
 */
public record TvaRequestDTO(
        @NotBlank @Column(unique = true) String tvaName,
        @DecimalMin(value = "0.0") BigDecimal tvaTaux) {
}