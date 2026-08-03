package com.mns.cda.saas_facturation.cart.DTO;


import java.math.BigDecimal;

public record QuoteLineDTO(
        Long qotLnId,
        int qotLnQuantity,
        BigDecimal qotLnPriceHT,
        String articleName,
        String articleRef,
        BigDecimal tvaRate,
        BigDecimal totalHT,
        BigDecimal totalTVA,
        BigDecimal totalTTC
) {
}
