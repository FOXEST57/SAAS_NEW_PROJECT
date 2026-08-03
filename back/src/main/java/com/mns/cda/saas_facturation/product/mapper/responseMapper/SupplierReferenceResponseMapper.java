package com.mns.cda.saas_facturation.product.mapper.responseMapper;

import com.mns.cda.saas_facturation.product.DTO.responseDTO.SupplierReferenceResponseDTO;
import com.mns.cda.saas_facturation.product.model.SupplierReference;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SupplierReferenceResponseMapper {

    private final SupplierResponseMapper supplierMapper;

    public SupplierReferenceResponseDTO toResponseDTO(SupplierReference supplierReference) {
        return new SupplierReferenceResponseDTO(
                supplierReference.getSplRefId(),
                supplierMapper.toResponseDTO(supplierReference.getSupplier()),
                supplierReference.getSplRefReference(),
                supplierReference.getSplRefSellPrice(),
                supplierReference.getSplRefStock()
        );
    }
}
