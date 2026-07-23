package com.mns.cda.saas_facturation.DTO;

import java.math.BigDecimal;


public record ArticleLightDTO(
        Long artId,
        String artReference,
        String artName,
        String artDescription,
        int artStock,
        BigDecimal artPriceTTC
) {
}