package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.ArticleSupplierDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.ArticleSupplierRequestDTO;
import com.mns.cda.saas_facturation.Iservice.IArticleService;
import com.mns.cda.saas_facturation.Iservice.IArticleSupplierService;
import com.mns.cda.saas_facturation.Iservice.ISupplierService;
import com.mns.cda.saas_facturation.model.Article;
import com.mns.cda.saas_facturation.model.ArticleSupplier;
import com.mns.cda.saas_facturation.model.Supplier;
import com.mns.cda.saas_facturation.repository.ArticleRepository;
import com.mns.cda.saas_facturation.repository.ArticleSupplierRepository;
import com.mns.cda.saas_facturation.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ArticleSupplierService implements IArticleSupplierService {

    protected final ArticleSupplierRepository articleSupplierRepository;
    protected final SupplierRepository supplierRepository;
    protected final ArticleRepository articleRepository;

    protected final SupplierService supplierService;
    protected final ArticleService articleService;

    //GetAll
    @Override
    public List<ArticleSupplierDTO> findAll() {
        return articleSupplierRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList(); }

    //Get By Id
    @Override
    public Optional<ArticleSupplierDTO> findById(ArticleSupplier.ArticleSupplierId id) {
        return articleSupplierRepository.findById(id)
                .map(this::toDTO);
    }


    //Post
    @Override
    public ArticleSupplierDTO create(ArticleSupplierRequestDTO dto)
            throws IArticleService.ArticleNotFoundException,
            ISupplierService.SupplierNotFoundException {

        Supplier artSplSupplier = supplierRepository.findById(dto.supplierId())
                .orElseThrow(ISupplierService.SupplierNotFoundException::new);

        Article artSplArticle = articleRepository.findById(dto.articleId())
                .orElseThrow(IArticleService.ArticleNotFoundException::new);

        ArticleSupplier artSpl = new ArticleSupplier(
                null,
                artSplArticle,
                artSplSupplier,
                dto.artSplReference(),
                dto.artSplStock()
        );

        return toDTO(articleSupplierRepository.save(artSpl));
    }



    //PUT
    @Override
    public ArticleSupplierDTO update(ArticleSupplier.ArticleSupplierId id, ArticleSupplierRequestDTO dto)
            throws ArticleSupplierNotFoundException,
            ISupplierService.SupplierNotFoundException,
            IArticleService.ArticleNotFoundException {

        ArticleSupplier artSpl = articleSupplierRepository.findById(id)
                .orElseThrow(ArticleSupplierNotFoundException::new);

        Supplier supplier = supplierRepository.findById(dto.supplierId())
                .orElseThrow(ISupplierService.SupplierNotFoundException::new);
        artSpl.setSupplier(supplier);

        Article article = articleRepository.findById(dto.articleId())
                .orElseThrow(IArticleService.ArticleNotFoundException::new);
        artSpl.setArticle(article);

        artSpl.setArtSplReference(dto.artSplReference());
        artSpl.setArtSplStock(dto.artSplStock());

        ArticleSupplier saved = articleSupplierRepository.save(artSpl);
        return toDTO(saved);
    }

    @Override
    public void deleteById(ArticleSupplier.ArticleSupplierId id) {
        articleSupplierRepository.deleteById(id);
    }

    @Override
    public ArticleSupplierDTO toDTO(ArticleSupplier articleSupplier) {
        return new ArticleSupplierDTO(
                articleSupplier.getArtSplId(),
                articleService.toSupplierResponseDTO(articleSupplier.getArticle()),
                supplierService.toResponseDTO(articleSupplier.getSupplier()),
                articleSupplier.getArtSplReference(),
                articleSupplier.getArtSplStock()
        );
    }
}
