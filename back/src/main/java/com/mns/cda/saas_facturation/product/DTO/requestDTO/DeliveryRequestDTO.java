package com.mns.cda.saas_facturation.product.DTO.requestDTO;

import com.mns.cda.saas_facturation.product.model.MakerReference;
import com.mns.cda.saas_facturation.product.model.SupplierReference;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DeliveryRequestDTO(
        @Min(1) int dlvQuantity,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal dlvBuyingPrice,
        MakerReference.MakerReferenceId mkrReferenceId,
        SupplierReference.SupplierReferenceId splReferenceId
) {}