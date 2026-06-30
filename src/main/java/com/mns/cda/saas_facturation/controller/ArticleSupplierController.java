package com.mns.cda.saas_facturation.controller;

import com.mns.cda.saas_facturation.DTO.ArticleSupplierDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.ArticleSupplierRequestDTO;
import com.mns.cda.saas_facturation.Iservice.IArticleService;
import com.mns.cda.saas_facturation.Iservice.IArticleSupplierService;
import com.mns.cda.saas_facturation.Iservice.ISupplierService;
import com.mns.cda.saas_facturation.model.ArticleSupplier;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/article-supplier")
@Tag(name = "Article Supplier", description = "API de gestion des différents votes.")
@CrossOrigin
public class ArticleSupplierController {

    private final IArticleSupplierService articleSupplierService;

    @GetMapping("/list")
    public List<ArticleSupplierDTO> findAll() {
        return articleSupplierService.findAll();
    }

    @GetMapping("/{articleId}/{supplierId}")
    public ResponseEntity<ArticleSupplierDTO> findById(
            @PathVariable Long articleId,
            @PathVariable Long supplierId) {

        ArticleSupplier.ArticleSupplierId articleSupplierId = new ArticleSupplier.ArticleSupplierId(articleId, supplierId);
        Optional<ArticleSupplierDTO> articleSupplier = articleSupplierService.findById(articleSupplierId);

        if (articleSupplier.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(articleSupplier.get(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ArticleSupplierDTO> create(
            @Valid @RequestBody ArticleSupplierRequestDTO articleSupplier)
            throws IArticleService.ArticleNotFoundException,
            ISupplierService.SupplierNotFoundException {

        ArticleSupplierDTO response = articleSupplierService.create(articleSupplier);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{articleId}/{supplierId}")
    public ResponseEntity<ArticleSupplierDTO> delete(
            @PathVariable Long articleId,
            @PathVariable Long supplierId) {

        ArticleSupplier.ArticleSupplierId  articleSupplierId = new ArticleSupplier.ArticleSupplierId(articleId, supplierId);
        Optional<ArticleSupplierDTO> articleSupplier = articleSupplierService.findById(articleSupplierId);

        if (articleSupplier.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        articleSupplierService.deleteById(articleSupplierId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{articleId}/{supplierId}")
    public ResponseEntity<ArticleSupplierDTO> update(
            @PathVariable Long articleId,
            @PathVariable Long supplierId,
            @Valid @RequestBody ArticleSupplierRequestDTO dto
    ) throws ISupplierService.SupplierNotFoundException,
            IArticleService.ArticleNotFoundException,
            IArticleSupplierService.ArticleSupplierNotFoundException {

        ArticleSupplier.ArticleSupplierId artSplId = new ArticleSupplier.ArticleSupplierId(articleId, supplierId);

        ArticleSupplierDTO updated = articleSupplierService.update(artSplId, dto);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
}

