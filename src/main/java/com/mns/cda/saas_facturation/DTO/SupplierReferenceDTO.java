package com.mns.cda.saas_facturation.DTO;

import com.mns.cda.saas_facturation.DTO.responseDTO.ArticleResponseSupplierDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.SupplierResponseDTO;
import com.mns.cda.saas_facturation.model.SupplierReference;

import java.math.BigDecimal;

public record SupplierReferenceDTO(
        ArticleResponseSupplierDTO article,
        SupplierResponseDTO supplier,
        String splRefReference,
        BigDecimal supplierPrice,
        int splRefStock
) {
}
