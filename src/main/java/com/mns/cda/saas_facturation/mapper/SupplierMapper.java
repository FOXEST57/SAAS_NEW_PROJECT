package com.mns.cda.saas_facturation.mapper;

import com.mns.cda.saas_facturation.DTO.SupplierDTO;
import com.mns.cda.saas_facturation.model.Supplier;
import com.mns.cda.saas_facturation.model.SupplierReference;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SupplierMapper {

    public SupplierDTO toDTO(Supplier supplier) {
        return new SupplierDTO(
                supplier.getSplId(),
                supplier.getSplName(),
                supplier.getSplEmail(),
                supplier.getSplPhone(),
                supplier.getSplAddress()
        );
    }

    public SupplierDTO ReferenceToDTO(SupplierReference supplierReference) {
        Supplier supplier = supplierReference.getSupplier();
        return new SupplierDTO(
                supplier.getSplId(),
                supplier.getSplName(),
                supplier.getSplEmail(),
                supplier.getSplPhone()
        );
    }
}
