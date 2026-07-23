package com.mns.cda.saas_facturation.DTO.responseDTO;

import com.mns.cda.saas_facturation.DTO.ArticleDTO;
import com.mns.cda.saas_facturation.DTO.CategoryDTO;

/**
 * DTO de réponse représentant une catégorie sous forme aplatie.
 *
 * <p>Contrairement à {@link CategoryDTO} qui inclut la liste récursive des enfants,
 * ce DTO ne contient que les informations essentielles de la catégorie :
 * son identifiant, son nom et le nom de sa catégorie parente.</p>
 *
 * <p>Il est utilisé comme DTO imbriqué dans d'autres réponses (ex : {@link ArticleDTO})
 * pour éviter les boucles infinies JSON liées à la relation auto-référentielle
 * de l'entité {@link com.mns.cda.saas_facturation.model.Category}.</p>
 *
 * <p>Un {@code record} Java est immuable par nature : les champs sont finaux
 * et les accesseurs sont générés automatiquement ({@code catId()}, {@code catName()},
 * {@code catParentName()}).</p>
 *
 * @param catId        l'identifiant unique de la catégorie
 * @param catName      le nom de la catégorie
 * @param catParentName le nom de la catégorie parente, ou {@code null} si la catégorie est une racine
 *
 * @see com.mns.cda.saas_facturation.service.CategoryService#toResponseDTO
 * @see CategoryDTO
 */
public record CategoryResponseDTO(
        Long catId,
        String catName,
        String catSlug,
        String catParentName
) {
}
