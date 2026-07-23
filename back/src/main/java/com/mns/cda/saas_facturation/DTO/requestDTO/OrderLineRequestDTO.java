package com.mns.cda.saas_facturation.DTO.requestDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderLineRequestDTO(
        @NotNull Long cartId,
        @NotNull Long articleId,
        @NotNull @Min(1) Integer quantity
) {
}
