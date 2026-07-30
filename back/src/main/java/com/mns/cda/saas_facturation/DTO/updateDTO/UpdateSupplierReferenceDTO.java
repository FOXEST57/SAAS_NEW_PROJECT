package com.mns.cda.saas_facturation.DTO.updateDTO;

import com.mns.cda.saas_facturation.enumeration.DeliveryStatus;
import jakarta.persistence.Column;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateSupplierReferenceDTO (
        @NotBlank @Column(unique = true) String splRefReference,
        @NotNull @DecimalMin(value = "0.00") BigDecimal splRefSellPrice,
        int splRefStock,
        @NotNull DeliveryStatus status
) {
}
