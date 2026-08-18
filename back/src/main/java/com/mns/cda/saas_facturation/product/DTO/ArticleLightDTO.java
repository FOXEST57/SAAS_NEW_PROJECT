package com.mns.cda.saas_facturation.product.DTO;

import java.math.BigDecimal;


public record ArticleLightDTO(
        Long artId,
        String artReference,
        String artName,
        String artDescription,
        boolean isActive,
        int artStock,
        BigDecimal artPriceTTC
) {
}