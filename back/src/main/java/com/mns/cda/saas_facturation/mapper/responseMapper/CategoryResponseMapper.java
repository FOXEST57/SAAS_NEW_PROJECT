package com.mns.cda.saas_facturation.mapper.responseMapper;

import com.mns.cda.saas_facturation.DTO.responseDTO.CategoryResponseDTO;
import com.mns.cda.saas_facturation.model.Category;
import com.mns.cda.saas_facturation.service.ArticleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CategoryResponseMapper {

    /**
     * Convertit une entité {@link Category} en {@link CategoryResponseDTO}.
     *
     * <p>Contrairement à , cette méthode produit une représentation
     * <b>aplatie</b> de la catégorie : sans liste des enfants et sans récursion.
     * Elle est utilisée par les autres services (notamment {@link ArticleService})
     * pour inclure les informations de catégorie dans leurs propres DTOs de réponse,
     * sans risque de boucle infinie JSON.</p>
     *
     * <p>Le nom du parent est résolu conditionnellement : si la catégorie n'a pas de parent
     * (catégorie racine), le champ {@code catParentName} est {@code null}.</p>
     *
     * @param category l'entité catégorie à convertir (ne doit pas être {@code null})
     * @return un {@link CategoryResponseDTO} contenant l'splId, le nom et le nom du parent
     *         (ou {@code null} si la catégorie est une racine)
     */

    public CategoryResponseDTO toResponseDTO(Category category) {

        // Résolution conditionnelle du nom du parent
        // null si la catégorie est une racine (pas de parent)
        String catParentName = null;
        if (category.getCatParent() != null) {
            catParentName = category.getCatParent().getCatName();
        }

        // Mapping vers le DTO allégé : splId, nom, nom du parent uniquement
        return new CategoryResponseDTO(
                category.getCatId(),
                category.getCatName(),
                category.getCatSlug(),
                catParentName
        );
    }
}
