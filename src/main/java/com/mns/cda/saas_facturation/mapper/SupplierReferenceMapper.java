package com.mns.cda.saas_facturation.mapper;

import com.mns.cda.saas_facturation.DTO.SupplierReferenceDTO;
import com.mns.cda.saas_facturation.mapper.responseMapper.ArticleResponseSupplierMapper;
import com.mns.cda.saas_facturation.mapper.responseMapper.SupplierResponseMapper;
import com.mns.cda.saas_facturation.model.SupplierReference;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SupplierReferenceMapper {

    protected final SupplierResponseMapper supplierMapper;
    protected final ArticleResponseSupplierMapper articleMapper;


    public SupplierReferenceDTO toDTO(SupplierReference supplierReference) {
        return new SupplierReferenceDTO(
                articleMapper.toResponseDTO(supplierReference.getArticle()),
                supplierMapper.toResponseDTO(supplierReference.getSupplier()),
                supplierReference.getSplRefReference(),
                supplierReference.getSupplierPrice(),
                supplierReference.getSplRefStock()
        );
    }
}
