package com.mns.cda.saas_facturation.DTO.requestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SupplierReferenceRequestDTO(
        @NotNull Long articleId,
        @NotNull Long supplierId,
        @NotBlank String splRefReference,
        @NotNull BigDecimal supplierPrice,
        @NotNull int splRefStock
) {
}
