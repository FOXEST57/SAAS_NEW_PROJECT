package com.mns.cda.saas_facturation.DTO.responseDTO;

import java.math.BigDecimal;

public record ArticleResponseCustomerDTO(
        Long artId,
        String artReference,
        String artName,
        String artDescription,
        BigDecimal artPriceTTC,
        int artStock,
        CategoryResponseDTO category,
        SupplierResponseDTO supplier
) {
}
