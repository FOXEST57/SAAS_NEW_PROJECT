package com.mns.cda.saas_facturation.product.mapper;

import com.mns.cda.saas_facturation.location.mapper.AddressMapper;
import com.mns.cda.saas_facturation.product.DTO.SupplierDTO;
import com.mns.cda.saas_facturation.product.model.Supplier;
import com.mns.cda.saas_facturation.product.model.SupplierReference;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SupplierMapper {

    private final AddressMapper addressMapper;

    public SupplierDTO toDTO(Supplier supplier) {
        return new SupplierDTO(
                supplier.getSplId(),
                supplier.getSplName(),
                supplier.getSplEmail(),
                supplier.getSplPhone(),
               addressMapper.toDTO(supplier.getAddress())
        );
    }

    public SupplierDTO ReferenceToDTO(SupplierReference supplierReference) {
        Supplier supplier = supplierReference.getSupplier();
        return new SupplierDTO(
                supplier.getSplId(),
                supplier.getSplName(),
                supplier.getSplEmail(),
                supplier.getSplPhone(),
                addressMapper.toDTO(supplier.getAddress())
        );
    }
}
