package com.mns.cda.saas_facturation.product.DTO.responseDTO;

import com.mns.cda.saas_facturation.product.model.SupplierReference;

import java.math.BigDecimal;
import java.util.List;

public record SupplierReferenceResponseArticleDTO(
        SupplierReference.SupplierReferenceId splRefId,
        SupplierResponseDTO supplier,
        String splRefReference,
        List<DeliveryResponseDTO> deliveries
) {
}
