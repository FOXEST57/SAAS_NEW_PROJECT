package com.mns.cda.saas_facturation.cart.DTO;

import java.math.BigDecimal;

public record InvoiceLineDTO (
        Long invLnId,
        int invLnQuantity,
        BigDecimal invLnPriceHT,
        String articleName,
        String articleRef,
        BigDecimal tvaRate,
        BigDecimal totalHT,
        BigDecimal totalTVA,
        BigDecimal totalTTC
) {
}
