package com.mns.cda.saas_facturation.DTO.requestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record InventoryRequestDTO(
        @NotBlank int invStock,
        @NotNull Long articleId
) {
}
