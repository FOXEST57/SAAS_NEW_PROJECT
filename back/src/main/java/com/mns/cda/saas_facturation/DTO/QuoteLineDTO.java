package com.mns.cda.saas_facturation.DTO;


import java.math.BigDecimal;

public record QuoteLineDTO(
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
