package com.mns.cda.saas_facturation.DTO.responseDTO;

import java.math.BigDecimal;

public record ArticleResponseSupplierDTO(
        Long artId,
        String artReference,
        String artName,
        String artDescription,
        BigDecimal artPriceExcludeTaxes,
        int artStock,
        TvaResponseDTO tva,
        CategoryResponseDTO category
) {
}
