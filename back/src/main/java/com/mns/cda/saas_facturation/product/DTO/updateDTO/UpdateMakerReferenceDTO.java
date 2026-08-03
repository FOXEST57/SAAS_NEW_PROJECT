package com.mns.cda.saas_facturation.product.DTO.updateDTO;

import com.mns.cda.saas_facturation.enumeration.DeliveryStatus;
import jakarta.persistence.Column;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateMakerReferenceDTO(
        @NotBlank @Column(unique = true) String reference,
        int artMkrStock,
        @NotNull @DecimalMin(value = "0.00") BigDecimal artMkrSellPrice,
        @NotNull DeliveryStatus status
        ) {
}
