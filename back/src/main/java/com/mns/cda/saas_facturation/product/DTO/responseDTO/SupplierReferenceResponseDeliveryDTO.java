package com.mns.cda.saas_facturation.product.DTO.responseDTO;

import com.mns.cda.saas_facturation.product.model.SupplierReference;

public record SupplierReferenceResponseDeliveryDTO(
        SupplierReference.SupplierReferenceId splRefId,
        ArticleResponseSupplierReferenceDTO article,
        SupplierResponseDTO supplier,
        String splRefReference
) {
}
