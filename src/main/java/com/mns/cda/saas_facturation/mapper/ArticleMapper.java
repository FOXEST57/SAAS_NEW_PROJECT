package com.mns.cda.saas_facturation.mapper;

import com.mns.cda.saas_facturation.DTO.ArticleDTO;
import com.mns.cda.saas_facturation.DTO.MakerReferenceForArticleDTO;
import com.mns.cda.saas_facturation.DTO.SupplierDTO;
import com.mns.cda.saas_facturation.DTO.SupplierReferenceDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.*;

import com.mns.cda.saas_facturation.mapper.responseMapper.SupplierReferenceResponseMapper;
import com.mns.cda.saas_facturation.mapper.responseMapper.CategoryResponseMapper;
import com.mns.cda.saas_facturation.mapper.responseMapper.TvaResponseMapper;
import com.mns.cda.saas_facturation.model.Article;
import com.mns.cda.saas_facturation.model.MakerReference;
import com.mns.cda.saas_facturation.model.SupplierReference;
import com.mns.cda.saas_facturation.repository.ArticleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class ArticleMapper {

    private final CategoryResponseMapper categoryMapper;
    private final TvaResponseMapper tvaResponseMapper;
    private final SupplierReferenceResponseMapper supplierReferenceMapper;
    private final ArticleRepository articleRepository;
    private final MakerReferenceMapper makerReferenceMapper;

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

        List<SupplierReferenceResponseDTO> suppliersLinks = article.getSuppliers() != null
                ? article.getSuppliers()
                .stream()
                .map(supplierReferenceMapper::toResponseDTO)
                .toList()
                :List.of();

        List<MakerReferenceForArticleDTO> makerLinks = article.getMakerReferences()
                .stream()
                .map(makerReferenceMapper::referenceToDto)
                .toList();

        // Construction du DTO de réponse avec toutes les données calculées et mappées
        return new ArticleDTO(
                article.getArtId(),
                article.getArtReference(),
                article.getArtName(),
                article.getArtDescription(),
                article.getArtPriceExcludeTaxes(), // Prix HT
                article.getArtStock(),
                tvaResponseMapper.toResponseDto(article.getTva()),
                priceTTC,
                article.getArtCreateDate(),
                article.getArtUpdateDate(),// Prix TTC calculé dynamiquement
                categoriesResponse,
                suppliersLinks,
                makerLinks
        );
    }

    public ArticleResponseMakerReferenceDTO MakerReferenceToDTO(MakerReference makerReference) {
        Article article = makerReference.getArticle();

        List<SupplierReferenceResponseDTO> suppliers = articleRepository.findById(article.getArtId())
                .map(art -> art.getSuppliers()
                        .stream()
                        .map(supplierReferenceMapper::toResponseDTO)
                        .toList())
                .orElse(List.of());

        return new ArticleResponseMakerReferenceDTO(
                article.getArtId(),
                article.getArtReference(),
                article.getArtName(),
                suppliers

        );
    }

    public ArticleResponseSupplierDTO supplierReferenceToDTO(SupplierReference supplierReference) {
        Article article = supplierReference.getArticle();

        List<CategoryResponseDTO> categories = article.getCategories() != null
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
                categories
        );
    }

}
