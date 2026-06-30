package com.mns.cda.saas_facturation.mapper.responseMapper;

import com.mns.cda.saas_facturation.DTO.responseDTO.ArticleSupplierResponseDTO;
import com.mns.cda.saas_facturation.mapper.SupplierMapper;
import com.mns.cda.saas_facturation.model.ArticleSupplier;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ArticleSupplierResponseMapper {

    private final SupplierResponseMapper supplierMapper;

    public ArticleSupplierResponseDTO toResponseDTO(ArticleSupplier articleSupplier) {
        return new ArticleSupplierResponseDTO(
                articleSupplier.getArtSplId(),
                supplierMapper.toResponseDTO(articleSupplier.getSupplier()),
                articleSupplier.getArtSplReference(),
                articleSupplier.getArtSplStock()
        );
    }
}
