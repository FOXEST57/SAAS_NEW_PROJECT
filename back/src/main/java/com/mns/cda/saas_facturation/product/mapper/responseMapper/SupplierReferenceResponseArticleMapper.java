package com.mns.cda.saas_facturation.product.mapper.responseMapper;

import com.mns.cda.saas_facturation.product.DTO.responseDTO.SupplierReferenceResponseArticleDTO;
import com.mns.cda.saas_facturation.product.model.SupplierReference;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SupplierReferenceResponseArticleMapper {

    private final SupplierResponseMapper supplierMapper;
    private final DeliveryResponseMapper deliveryResponseMapper;

    public SupplierReferenceResponseArticleDTO toResponseArticleDTO(SupplierReference supplierReference) {
        return new SupplierReferenceResponseArticleDTO(
                supplierReference.getSplRefId(),
                supplierMapper.toResponseDTO(supplierReference.getSupplier()),
                supplierReference.getSplRefReference(),
                supplierReference.getDeliveries().stream().map(deliveryResponseMapper::toResponseDto).toList()
        );
    }

}
