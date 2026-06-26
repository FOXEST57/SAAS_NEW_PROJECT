package com.mns.cda.saas_facturation.DTO;

import com.mns.cda.saas_facturation.model.Category;
import jakarta.validation.constraints.NotBlank;


public record CategoryRequestDTO(
        @NotBlank String catName,
        Long parentId
) {
}
