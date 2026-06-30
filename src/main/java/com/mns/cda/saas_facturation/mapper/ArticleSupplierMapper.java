package com.mns.cda.saas_facturation.mapper;

import com.mns.cda.saas_facturation.DTO.ArticleSupplierDTO;
import com.mns.cda.saas_facturation.mapper.responseMapper.ArticleResponseSupplierMapper;
import com.mns.cda.saas_facturation.mapper.responseMapper.SupplierResponseMapper;
import com.mns.cda.saas_facturation.model.ArticleSupplier;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ArticleSupplierMapper {

    protected final SupplierResponseMapper supplierMapper;
    protected final ArticleResponseSupplierMapper articleMapper;


    public ArticleSupplierDTO toDTO(ArticleSupplier articleSupplier) {
        return new ArticleSupplierDTO(
                articleSupplier.getArtSplId(),
                articleMapper.toResponseDTO(articleSupplier.getArticle()),
                supplierMapper.toResponseDTO(articleSupplier.getSupplier()),
                articleSupplier.getArtSplReference(),
                articleSupplier.getArtSplStock()
        );
    }
}
