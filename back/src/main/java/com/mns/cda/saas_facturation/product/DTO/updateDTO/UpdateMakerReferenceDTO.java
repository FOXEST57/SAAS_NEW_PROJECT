package com.mns.cda.saas_facturation.product.DTO.updateDTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

public record UpdateMakerReferenceDTO(
        @NotBlank @Column(unique = true) String reference
        ) {
}
