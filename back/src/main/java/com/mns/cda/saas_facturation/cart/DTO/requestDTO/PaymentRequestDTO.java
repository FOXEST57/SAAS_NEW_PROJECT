package com.mns.cda.saas_facturation.cart.DTO.requestDTO;

import com.mns.cda.saas_facturation.enumeration.PaymentType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentRequestDTO(
        @NotNull PaymentType payType,
        @NotNull Boolean payAccount,
        @NotNull @DecimalMin(value = "0.01") BigDecimal payAmount,
        @NotNull Long invoiceId
) {
}