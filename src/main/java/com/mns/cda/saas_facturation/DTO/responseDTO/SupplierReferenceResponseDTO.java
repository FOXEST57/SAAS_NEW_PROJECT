package com.mns.cda.saas_facturation.DTO.responseDTO;

import com.mns.cda.saas_facturation.model.SupplierReference;

import java.math.BigDecimal;

public record SupplierReferenceResponseDTO(
        SupplierReference.SupplierReferenceId splRefId,
        SupplierResponseDTO supplier,
        String splRefReference,
        BigDecimal supplierPrice,
        int splRefStock
) {
}
