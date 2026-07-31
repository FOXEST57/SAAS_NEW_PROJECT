package com.mns.cda.saas_facturation.DTO.requestDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record InventoryRequestDTO(
        @Min(0) int invStock,
        @NotNull Long articleId
) {
}
