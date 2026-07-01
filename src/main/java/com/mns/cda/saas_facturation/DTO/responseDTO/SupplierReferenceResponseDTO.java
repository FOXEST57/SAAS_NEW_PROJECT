package com.mns.cda.saas_facturation.DTO.responseDTO;

import com.mns.cda.saas_facturation.model.SupplierReference;

public record SupplierReferenceResponseDTO(
        SupplierReference.SupplierReferenceId splRefId,
        SupplierResponseDTO supplier,
        String splRefReference,
        int splRefStock
) {
}
