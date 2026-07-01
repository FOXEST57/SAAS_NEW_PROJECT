package com.mns.cda.saas_facturation.DTO.updateDTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateSupplierReferenceDTO (
        @NotBlank @Column(unique = true) String splRefReference,
        @NotNull int splRefStock
) {
}
