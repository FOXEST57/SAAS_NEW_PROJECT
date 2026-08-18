package com.mns.cda.saas_facturation.product.mapper;

import com.mns.cda.saas_facturation.product.DTO.ArticleDTO;
import com.mns.cda.saas_facturation.product.DTO.ArticleLightDTO;
import com.mns.cda.saas_facturation.product.mapper.responseMapper.MakerReferenceResponseArticleMapper;
import com.mns.cda.saas_facturation.product.mapper.responseMapper.SupplierReferenceResponseArticleMapper;
import com.mns.cda.saas_facturation.product.mapper.responseMapper.CategoryResponseMapper;
import com.mns.cda.saas_facturation.product.mapper.responseMapper.TvaResponseMapper;
import com.mns.cda.saas_facturation.product.DTO.responseDTO.*;
import com.mns.cda.saas_facturation.product.model.Article;
import com.mns.cda.saas_facturation.product.model.MakerReference;
import com.mns.cda.saas_facturation.product.model.SupplierReference;
import com.mns.cda.saas_facturation.product.repository.ArticleRepository;
import com.mns.cda.saas_facturation.product.service.StockService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class ArticleMapper {

    private final ArticleRepository articleRepository;
    private final CategoryResponseMapper categoryMapper;
    private final TvaResponseMapper tvaResponseMapper;
    private final SupplierReferenceResponseArticleMapper supplierReferenceResponseArticleMapper;
    private final MakerReferenceResponseArticleMapper makerReferenceResponseArticleMapper;
    private final StockService stockService;

    public ArticleDTO toDTO(Article article) {

        // Calcul du prix TTC : prixHT × (1 + tauxTVA)
        // BigDecimal est utilisé à la place de double pour éviter les erreurs d'arrondi monétaires
        BigDecimal priceTTC = article.getArtPriceExcludeTaxes()
                .multiply(BigDecimal.ONE.add(article.getTva().getTvaTaux()));


        List<CategoryResponseDTO> categoriesResponse = article.getCategories() != null
                ? article.getCategories()
                .stream()
                .map(categoryMapper::toResponseDTO)
                .toList()
                :List.of();

        List<SupplierReferenceResponseArticleDTO> suppliersLinks = article.getSupplierReferences() != null
                ? article.getSupplierReferences()
                .stream()
                .map(supplierReferenceResponseArticleMapper::toResponseArticleDTO)
                .toList()
                :List.of();

        List<MakerReferenceResponseArticleDTO> makerLinks = article.getMakerReferences()
                .stream()
                .map(makerReferenceResponseArticleMapper::toResponseArticleDto)
                .toList();

        // Construction du DTO de réponse avec toutes les données calculées et mappées
        return new ArticleDTO(
                article.getArtId(),
                article.getArtReference(),
                article.getArtName(),
                article.getArtDescription(),
                article.getArtPriceExcludeTaxes(), // Prix HT
                stockService.getAvailableStockByArticle(article.getArtId()), // Stock disponible calculé dynamiquement
                article.isActive(),
                tvaResponseMapper.toResponseDto(article.getTva()),
                priceTTC, // Prix TTC calculé dynamiquement
                article.getArtCreatedDate(),
                article.getArtUpdatedDate(),
                categoriesResponse,
                suppliersLinks,
                makerLinks
        );
    }

    public ArticleLightDTO toLightDTO(Article article) {

        BigDecimal priceTTC = article.getArtPriceExcludeTaxes()
                .multiply(BigDecimal.ONE.add(article.getTva().getTvaTaux()));

        return new ArticleLightDTO(
                article.getArtId(),
                article.getArtReference(),
                article.getArtName(),
                article.getArtDescription(),
                article.isActive(),
                stockService.getAvailableStockByArticle(article.getArtId()),
                priceTTC
        );
    }


    public ArticleResponseMakerReferenceDTO makerReferenceToDTO(MakerReference makerReference) {
        Article article = makerReference.getArticle();

        List<SupplierReferenceResponseArticleDTO> suppliers = articleRepository.findById(article.getArtId())
                .map(art -> art.getSupplierReferences()
                        .stream()
                        .map(supplierReferenceResponseArticleMapper::toResponseArticleDTO)
                        .toList())
                .orElse(List.of());

        return new ArticleResponseMakerReferenceDTO(
                article.getArtId(),
                article.getArtName(),
                article.getArtReference(),
                suppliers

        );
    }

    public ArticleResponseSupplierReferenceDTO supplierReferenceToDTO(SupplierReference supplierReference) {
        Article article = supplierReference.getArticle();

        List<CategoryResponseDTO> categories = article.getCategories() != null
                ? article.getCategories()
                .stream()
                .map(categoryMapper::toResponseDTO)
                .toList()
                :List.of();

        return new ArticleResponseSupplierReferenceDTO(
                article.getArtId(),
                article.getArtReference(),
                article.getArtName(),
                article.getArtDescription(),
                article.getArtPriceExcludeTaxes(),
                stockService.getAvailableStockByArticle(article.getArtId()),
                tvaResponseMapper.toResponseDto(article.getTva()),
                categories,
                article.getMakerReferences()
                        .stream()
                        .map(makerReferenceResponseArticleMapper::toResponseArticleDto)
                        .toList()
        );
    }

    public ArticleResponseInventoryDTO inventoryToDto(Article article) {
        return new ArticleResponseInventoryDTO(
                article.getArtId(),
                article.getArtReference(),
                article.getArtName()
        );
    }
}
