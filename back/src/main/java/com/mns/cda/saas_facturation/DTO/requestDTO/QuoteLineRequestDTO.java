package com.mns.cda.saas_facturation.DTO.requestDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record QuoteLineRequestDTO(
        @NotNull @Min(1) int qotLnQuantity,
        @NotNull @Min(0) BigDecimal qotLnPriceHT,
        @NotBlank String articleName,
        @NotBlank String articleRef,
        @NotNull BigDecimal tvaRate,
        @NotNull Long quoteId
) {
}
