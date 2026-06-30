package com.mns.cda.saas_facturation.DTO.responseDTO;

import java.math.BigDecimal;
import java.util.List;

public record ArticleResponseSupplierDTO(
        Long artId,
        String artReference,
        String artName,
        String artDescription,
        BigDecimal artPriceExcludeTaxes,
        int artStock,
        TvaResponseDTO tva,
        List<CategoryResponseDTO> categories
) {
}
