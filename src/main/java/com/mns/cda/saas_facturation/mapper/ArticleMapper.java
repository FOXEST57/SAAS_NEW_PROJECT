package com.mns.cda.saas_facturation.mapper;

import com.mns.cda.saas_facturation.DTO.ArticleDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.ArticleSupplierResponseDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.CategoryResponseDTO;

import com.mns.cda.saas_facturation.mapper.responseMapper.ArticleSupplierResponseMapper;
import com.mns.cda.saas_facturation.mapper.responseMapper.CategoryResponseMapper;
import com.mns.cda.saas_facturation.mapper.responseMapper.TvaResponseMapper;
import com.mns.cda.saas_facturation.model.Article;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class ArticleMapper {

    private final CategoryResponseMapper categoryMapper;
    private final TvaResponseMapper tvaResponseMapper;
    private final ArticleSupplierResponseMapper articleSupplierMapper;

    public ArticleDTO toDTO(Article article) {

        // Calcul du prix TTC : prixHT × (1 + tauxTVA)
        // BigDecimal est utilisé à la place de double pour éviter les erreurs d'arrondi monétaires
        BigDecimal priceTTC = article.getArtPriceExcludeTaxes()
                .multiply(BigDecimal.ONE.add(article.getTva().getTvaTaux()));


        // Mapping conditionnel de la catégorie : null si l'article n'a pas de catégorie associée
        CategoryResponseDTO categoryResponse = article.getCategory() != null
                ? categoryMapper.toResponseDTO(article.getCategory())
                : null;

        List<ArticleSupplierResponseDTO> suppliersLinks = article.getSuppliers() != null
                ? article.getSuppliers()
                .stream()
                .map(articleSupplierMapper::toResponseDTO)
                .toList()
                :List.of();

        // Construction du DTO de réponse avec toutes les données calculées et mappées
        return new ArticleDTO(
                article.getArtId(),
                article.getArtReference(),
                article.getArtName(),
                article.getArtDescription(),
                article.getArtPriceExcludeTaxes(), // Prix HT
                article.getArtStock(),
                tvaResponseMapper.toResponseDto(article.getTva()),
                priceTTC,                          // Prix TTC calculé dynamiquement
                categoryResponse,
                suppliersLinks
        );
    }

}
