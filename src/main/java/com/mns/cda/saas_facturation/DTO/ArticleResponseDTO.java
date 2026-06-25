package com.mns.cda.saas_facturation.DTO;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ArticleResponseDTO (
        Long artId,
        String artReference,
        String artName,
        String artDescription,
        BigDecimal artPriceExcludeTaxes,
        int artStock,
        TvaResponseDTO tvaResponseDTO,
        SupplierResponseDTO supplierResponseDTO
){
}
