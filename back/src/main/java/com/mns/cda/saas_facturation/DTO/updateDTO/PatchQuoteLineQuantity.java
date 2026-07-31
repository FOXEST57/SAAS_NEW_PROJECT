package com.mns.cda.saas_facturation.DTO.updateDTO;

import jakarta.validation.constraints.Min;

public record PatchQuoteLineQuantity(
        @Min(0) int qotLineQuantity
        ) {
}
