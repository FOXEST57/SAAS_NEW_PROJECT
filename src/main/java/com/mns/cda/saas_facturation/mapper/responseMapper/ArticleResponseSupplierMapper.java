package com.mns.cda.saas_facturation.mapper.responseMapper;

import com.mns.cda.saas_facturation.DTO.responseDTO.ArticleResponseSupplierDTO;
import com.mns.cda.saas_facturation.model.Article;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ArticleResponseSupplierMapper {

    private final CategoryResponseMapper categoryMapper;
    private final TvaResponseMapper tvaResponseMapper;

    public ArticleResponseSupplierDTO toResponseDTO(Article article) {

        return new ArticleResponseSupplierDTO(
                article.getArtId(),
                article.getArtReference(),
                article.getArtName(),
                article.getArtDescription(),
                article.getArtPriceExcludeTaxes(),
                article.getArtStock(),
                tvaResponseMapper.toResponseDto(article.getTva()),
                categoryMapper.toResponseDTO(article.getCategory())
        );
    }
}
