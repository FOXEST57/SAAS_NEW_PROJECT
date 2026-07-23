package com.mns.cda.saas_facturation.DTO.updateDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderLineDTO (
        @NotNull @Min(1) Integer quantity
) {
}
