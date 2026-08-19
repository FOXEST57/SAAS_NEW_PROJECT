package com.mns.cda.saas_facturation.product.DTO.requestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SupplierReferenceRequestDTO(
        @NotNull Long articleId,
        @NotNull Long supplierId,
        @NotBlank String splRefReference,
        @NotNull List<Long> deliveryIds
) {
}
