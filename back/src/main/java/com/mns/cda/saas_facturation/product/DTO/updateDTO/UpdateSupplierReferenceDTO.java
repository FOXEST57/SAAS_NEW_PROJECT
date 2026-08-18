package com.mns.cda.saas_facturation.product.DTO.updateDTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

public record UpdateSupplierReferenceDTO (
        @NotBlank @Column(unique = true) String splRefReference
) {
}
