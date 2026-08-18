package com.mns.cda.saas_facturation.product.mapper.responseMapper;

import com.mns.cda.saas_facturation.product.DTO.responseDTO.ArticleResponseMakerReferenceDTO;
import com.mns.cda.saas_facturation.product.DTO.responseDTO.SupplierReferenceResponseArticleDTO;
import com.mns.cda.saas_facturation.product.model.Article;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ArticleResponseMakerReferenceMapper {

    protected final SupplierReferenceResponseArticleMapper supplierReferenceResponseArticleMapper;

    public ArticleResponseMakerReferenceDTO toResponseDto(Article article) {

        List<SupplierReferenceResponseArticleDTO> supplierReferenceResponseArticleDTOList = article
                .getSupplierReferences()
                .stream()
                .map(supplierReferenceResponseArticleMapper::toResponseArticleDTO)
                .toList();

        return new ArticleResponseMakerReferenceDTO(
                article.getArtId(),
                article.getArtName(),
                article.getArtReference(),
                supplierReferenceResponseArticleDTOList
        );
    }
}
