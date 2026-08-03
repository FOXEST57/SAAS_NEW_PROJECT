package com.mns.cda.saas_facturation.product.DTO;


import java.util.List;

public record CategoryDTO(
        Long catId,
        String catName,
        String catSlug,
        String catParentName,
        List<CategoryDTO> children
) {
}