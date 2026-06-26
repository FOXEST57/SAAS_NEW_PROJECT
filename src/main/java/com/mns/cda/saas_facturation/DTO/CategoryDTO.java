package com.mns.cda.saas_facturation.DTO;


import java.util.List;

public record CategoryDTO(
        Long catId,
        String catName,
        String catParentName,
        List<CategoryDTO> children
) {
}