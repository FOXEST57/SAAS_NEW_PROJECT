package com.mns.cda.saas_facturation.mapper.responseMapper;

import com.mns.cda.saas_facturation.DTO.responseDTO.ArticleResponseSupplierDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.CategoryResponseDTO;
import com.mns.cda.saas_facturation.model.Article;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ArticleResponseSupplierMapper {

    private final CategoryResponseMapper categoryMapper;
    private final TvaResponseMapper tvaResponseMapper;
    private final MakerReferenceResponseMapper makerReferenceResponseMapper;

    public ArticleResponseSupplierDTO toResponseDTO(Article article) {

        List<CategoryResponseDTO> categoriesResponse = article.getCategories() != null
                ? article.getCategories()
                .stream()
                .map(categoryMapper::toResponseDTO)
                .toList()
                :List.of();

        return new ArticleResponseSupplierDTO(
                article.getArtId(),
                article.getArtReference(),
                article.getArtName(),
                article.getArtDescription(),
                article.getArtPriceExcludeTaxes(),
                article.getArtStock(),
                tvaResponseMapper.toResponseDto(article.getTva()),
                categoriesResponse,
                article.getMakerReferences()
                        .stream()
                        .map(makerReferenceResponseMapper::toResponseDto)
                        .toList()
        );
    }
}
