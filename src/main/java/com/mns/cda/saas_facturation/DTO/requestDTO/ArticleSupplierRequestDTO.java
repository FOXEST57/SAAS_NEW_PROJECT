package com.mns.cda.saas_facturation.DTO.requestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ArticleSupplierRequestDTO(
        @NotNull Long articleId,
        @NotNull Long supplierId,
        @NotBlank String artSplReference,
        @NotNull int artSplStock
) {
}
