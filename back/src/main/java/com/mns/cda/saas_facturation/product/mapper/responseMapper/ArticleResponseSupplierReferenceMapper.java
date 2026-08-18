package com.mns.cda.saas_facturation.product.mapper.responseMapper;

import com.mns.cda.saas_facturation.product.DTO.responseDTO.ArticleResponseSupplierReferenceDTO;
import com.mns.cda.saas_facturation.product.DTO.responseDTO.CategoryResponseDTO;
import com.mns.cda.saas_facturation.product.model.Article;
import com.mns.cda.saas_facturation.product.service.StockService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ArticleResponseSupplierReferenceMapper {

    private final CategoryResponseMapper categoryMapper;
    private final TvaResponseMapper tvaResponseMapper;
    private final MakerReferenceResponseArticleMapper makerReferenceResponseArticleMapper;
    private final StockService stockService;

    public ArticleResponseSupplierReferenceDTO toResponseDTO(Article article) {
        List<CategoryResponseDTO> categoriesResponse = article.getCategories() != null
                ? article.getCategories()
                .stream()
                .map(categoryMapper::toResponseDTO)
                .toList()
                : List.of();

        return new ArticleResponseSupplierReferenceDTO(
                article.getArtId(),
                article.getArtReference(),
                article.getArtName(),
                article.getArtDescription(),
                article.getArtPriceExcludeTaxes(),
                stockService.getAvailableStockByArticle(article.getArtId()),
                tvaResponseMapper.toResponseDto(article.getTva()),
                categoriesResponse,
                article.getMakerReferences()
                        .stream()
                        .map(makerReferenceResponseArticleMapper::toResponseArticleDto)
                        .toList()
        );
    }
}
