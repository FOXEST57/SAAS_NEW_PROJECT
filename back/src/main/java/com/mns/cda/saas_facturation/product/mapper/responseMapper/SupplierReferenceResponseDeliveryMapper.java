package com.mns.cda.saas_facturation.product.mapper.responseMapper;

import com.mns.cda.saas_facturation.product.DTO.responseDTO.SupplierReferenceResponseDeliveryDTO;
import com.mns.cda.saas_facturation.product.model.SupplierReference;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SupplierReferenceResponseDeliveryMapper {

    private final SupplierResponseMapper supplierMapper;
    private final ArticleResponseSupplierReferenceMapper articleResponseSupplierReferenceMapper;

    public SupplierReferenceResponseDeliveryDTO toResponseDeliveryDTO(SupplierReference supplierReference) {
        return new SupplierReferenceResponseDeliveryDTO(
                supplierReference.getSplRefId(),
                articleResponseSupplierReferenceMapper.toResponseDTO(supplierReference.getArticle()),
                supplierMapper.toResponseDTO(supplierReference.getSupplier()),
                supplierReference.getSplRefReference()
        );
    }
}
