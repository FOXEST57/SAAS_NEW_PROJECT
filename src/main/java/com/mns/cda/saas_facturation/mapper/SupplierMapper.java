package com.mns.cda.saas_facturation.mapper;

import com.mns.cda.saas_facturation.DTO.SupplierDTO;
import com.mns.cda.saas_facturation.model.Supplier;
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
}
