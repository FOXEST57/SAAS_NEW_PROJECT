package com.mns.cda.saas_facturation.mapper.responseMapper;

import com.mns.cda.saas_facturation.DTO.responseDTO.SupplierReferenceResponseDTO;
import com.mns.cda.saas_facturation.model.SupplierReference;
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
                supplierReference.getSupplierPrice(),
                supplierReference.getSplRefStock()
        );
    }
}
