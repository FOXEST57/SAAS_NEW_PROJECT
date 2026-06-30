package com.mns.cda.saas_facturation.DTO.responseDTO;

import java.math.BigDecimal;
import java.util.List;

public record ArticleResponseCustomerDTO(
        Long artId,
        String artReference,
        String artName,
        String artDescription,
        BigDecimal artPriceTTC,
        int artStock,
        List<CategoryResponseDTO> categories,
        List<SupplierResponseDTO> supplier
) {
}
