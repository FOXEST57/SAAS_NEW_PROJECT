package com.mns.cda.saas_facturation.DTO;

import jakarta.validation.constraints.NotBlank;

public record UpdateMakerReferenceDTO(
        @NotBlank String reference
) {
}
