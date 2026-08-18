package com.mns.cda.saas_facturation.product.DTO;

import com.mns.cda.saas_facturation.product.DTO.responseDTO.ArticleResponseSupplierReferenceDTO;
import com.mns.cda.saas_facturation.product.DTO.responseDTO.DeliveryResponseDTO;
import com.mns.cda.saas_facturation.product.DTO.responseDTO.SupplierResponseDTO;

import java.util.List;

public record SupplierReferenceDTO(
        ArticleResponseSupplierReferenceDTO article,
        SupplierResponseDTO supplier,
        String splRefReference,
        List<DeliveryResponseDTO> deliveries
) {
}
