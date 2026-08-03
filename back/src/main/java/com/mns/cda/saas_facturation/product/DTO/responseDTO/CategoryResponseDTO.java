package com.mns.cda.saas_facturation.product.DTO.responseDTO;

import com.mns.cda.saas_facturation.product.DTO.ArticleDTO;
import com.mns.cda.saas_facturation.product.DTO.CategoryDTO;
import com.mns.cda.saas_facturation.product.model.Category;
import com.mns.cda.saas_facturation.product.service.CategoryService;

/**
 * DTO de réponse représentant une catégorie sous forme aplatie.
 *
 * <p>Contrairement à {@link CategoryDTO} qui inclut la liste récursive des enfants,
 * ce DTO ne contient que les informations essentielles de la catégorie :
 * son identifiant, son nom et le nom de sa catégorie parente.</p>
 *
 * <p>Il est utilisé comme DTO imbriqué dans d'autres réponses (ex : {@link ArticleDTO})
 * pour éviter les boucles infinies JSON liées à la relation auto-référentielle
 * de l'entité {@link Category}.</p>
 *
 * <p>Un {@code record} Java est immuable par nature : les champs sont finaux
 * et les accesseurs sont générés automatiquement ({@code catId()}, {@code catName()},
 * {@code catParentName()}).</p>
 *
 * @param catId        l'identifiant unique de la catégorie
 * @param catName      le nom de la catégorie
 * @param catParentName le nom de la catégorie parente, ou {@code null} si la catégorie est une racine
 *
 * @see CategoryService#toResponseDTO
 * @see CategoryDTO
 */
public record CategoryResponseDTO(
        Long catId,
        String catName,
        String catSlug,
        String catParentName
) {
}
