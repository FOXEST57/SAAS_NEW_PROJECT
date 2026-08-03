package com.mns.cda.saas_facturation.product.DTO.responseDTO;

import com.mns.cda.saas_facturation.product.model.SupplierReference;

import java.math.BigDecimal;

public record SupplierReferenceResponseDTO(
        SupplierReference.SupplierReferenceId splRefId,
        SupplierResponseDTO supplier,
        String splRefReference,
        BigDecimal supplierPrice,
        int splRefStock
) {
}
