package com.mns.cda.saas_facturation.product.mapper;

import com.mns.cda.saas_facturation.product.DTO.SupplierReferenceDTO;
import com.mns.cda.saas_facturation.product.mapper.responseMapper.ArticleResponseSupplierReferenceMapper;
import com.mns.cda.saas_facturation.product.mapper.responseMapper.DeliveryResponseMapper;
import com.mns.cda.saas_facturation.product.mapper.responseMapper.SupplierResponseMapper;
import com.mns.cda.saas_facturation.product.model.SupplierReference;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SupplierReferenceMapper {

    private final SupplierResponseMapper supplierMapper;
    private final ArticleResponseSupplierReferenceMapper articleMapper;
    private final DeliveryResponseMapper deliveryResponseMapper;

    public SupplierReferenceDTO toDTO(SupplierReference supplierReference) {
        return new SupplierReferenceDTO(
                articleMapper.toResponseDTO(supplierReference.getArticle()),
                supplierMapper.toResponseDTO(supplierReference.getSupplier()),
                supplierReference.getSplRefReference(),
                supplierReference.getDeliveries().stream().map(deliveryResponseMapper::toResponseDto).toList()
        );
    }
}
