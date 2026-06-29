package com.mns.cda.saas_facturation.DTO.responseDTO;

public record CategoryResponseDTO(
        Long catId,
        String catName,
        String catParentName
) {
}
