package com.mns.cda.saas_facturation.mapper;

import com.mns.cda.saas_facturation.DTO.CategoryDTO;
import com.mns.cda.saas_facturation.model.Category;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class CategoryMapper {


    public CategoryDTO toDTO(Category category) {
        return new CategoryDTO(
                category.getCatId(),
                category.getCatName(),
                category.getCatSlug(),
                // Le parent est représenté par son nom uniquement — évite la récursion vers le haut
                category.getCatParent() != null ? category.getCatParent().getCatName() : null,
                // Les enfants sont récursivement convertis en DTO — liste vide si null
                category.getCatChildren() != null
                        ? category.getCatChildren().stream()
                        .map(this::toDTO)
                        .toList()
                        : List.of()
        );
    }

}
