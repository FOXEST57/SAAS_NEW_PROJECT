package com.mns.cda.saas_facturation.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateMakerReferenceDTO(
        @NotBlank String mkrRefReference,
        @NotNull Long mkrRefStock
) {
}
