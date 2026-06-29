package com.mns.cda.saas_facturation.controller;

import com.mns.cda.saas_facturation.Iservice.IArticleSupplierService;
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
    public List<ArticleSupplier> findAll() {
        return articleSupplierService.findAll();
    }

    @GetMapping("/{articleId}/{supplierId}")
    public ResponseEntity<ArticleSupplier> findById(
            @PathVariable Long articleId,
            @PathVariable Long supplierId) {

        ArticleSupplier.ArticleSupplierId articleSupplierId = new ArticleSupplier.ArticleSupplierId(articleId, supplierId);
        Optional<ArticleSupplier> articleSupplier = articleSupplierService.findById(articleSupplierId);

        if (articleSupplier.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(articleSupplier.get(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ArticleSupplier> create(
            @Valid @RequestBody ArticleSupplier articleSupplier) {

        articleSupplierService.create(articleSupplier);

        return new ResponseEntity<>(articleSupplier, HttpStatus.CREATED);
    }

    @DeleteMapping("/{articleId}/{supplierId}")
    public ResponseEntity<ArticleSupplier> delete(
            @PathVariable Long articleId,
            @PathVariable Long supplierId) {

        ArticleSupplier.ArticleSupplierId  articleSupplierId = new ArticleSupplier.ArticleSupplierId(articleId, supplierId);
        Optional<ArticleSupplier> articleSupplier = articleSupplierService.findById(articleSupplierId);

        if (articleSupplier.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        articleSupplierService.deleteById(articleSupplierId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{articleId}/{supplierd}")
    public ResponseEntity<ArticleSupplier> update(
            @PathVariable Long articleId,
            @PathVariable Long supplierd,
            @Valid @RequestBody ArticleSupplier articleSupplier
    ) {
        ArticleSupplier.ArticleSupplierId articleSupplierId = new ArticleSupplier.ArticleSupplierId(articleId, supplierd);
        try {
            articleSupplierService.update(articleSupplier);
            return new ResponseEntity<>(articleSupplier, HttpStatus.OK);
        } catch (IArticleSupplierService.ArticleSupplierNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

