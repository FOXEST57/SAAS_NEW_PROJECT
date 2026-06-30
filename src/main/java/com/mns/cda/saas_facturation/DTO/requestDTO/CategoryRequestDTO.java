package com.mns.cda.saas_facturation.DTO.requestDTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO d'entrée représentant les données reçues par l'API pour créer ou modifier une catégorie.
 *
 * <p>Ce record est utilisé exclusivement en <strong>entrée</strong> (requête HTTP) :
 * il encapsule et valide les données envoyées par le client avant qu'elles
 * n'atteignent la couche service.</p>
 *
 * <p>La contrainte {@code @NotBlank} garantit que le nom de la catégorie
 * n'est ni {@code null}, ni vide, ni composé uniquement d'espaces blancs.
 * En cas d'échec, le {@code GlobalExceptionInterceptor} retourne un 400
 * avec le détail du champ invalide.</p>
 *
 * @param catName nom de la catégorie — ne doit pas être vide ou blanc
 *
 * @see com.mns.cda.saas_facturation.controller.CategoryController
 * @see com.mns.cda.saas_facturation.Iservice.ICategoryService
 */
public record CategoryRequestDTO(
        @NotBlank String catName,
        @NotBlank String catSlug,
        @Column(unique = true) Long parentId
) {
}
