package com.mns.cda.saas_facturation.DTO;

import com.mns.cda.saas_facturation.DTO.responseDTO.ArticleResponseSupplierDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.SupplierResponseDTO;
import com.mns.cda.saas_facturation.model.SupplierReference;

public record SupplierReferenceDTO(
        SupplierReference.SupplierReferenceId SplRefId,
        ArticleResponseSupplierDTO article,
        SupplierResponseDTO supplier,
        String splRefReference,
        int splRefStock
) {
}
