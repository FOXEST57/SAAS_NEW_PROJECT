package com.mns.cda.saas_facturation.cart.DTO;

import com.mns.cda.saas_facturation.enumeration.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentDTO(
        Long payId,
        LocalDateTime payCreatedDate,
        PaymentType payType,
        Boolean payAccount,
        BigDecimal payAmount,
        Long invoiceId
) {
}