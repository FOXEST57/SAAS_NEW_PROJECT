package com.mns.cda.saas_facturation.cart.DTO.updateDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record PatchQuoteLineQuantity(
        @NotBlank String artRef,
        @Min(0) int qotLineQuantity
        ) {
}
