package com.mns.cda.saas_facturation.product.DTO.updateDTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateDeliveryDTO(
        @Min(1) int dlvQuantity,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal dlvBuyingPrice
        ) {
}
