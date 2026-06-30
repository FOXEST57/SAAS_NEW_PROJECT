package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.ArticleSupplierDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.ArticleSupplierRequestDTO;
import com.mns.cda.saas_facturation.model.ArticleSupplier;

import java.util.List;
import java.util.Optional;

public interface IArticleSupplierService {


    public static class ArticleSupplierNotFoundException extends Exception {}

    List<ArticleSupplierDTO> findAll();

    Optional<ArticleSupplierDTO> findById(ArticleSupplier.ArticleSupplierId id);

    ArticleSupplierDTO create(ArticleSupplierRequestDTO dto)
            throws IArticleService.ArticleNotFoundException,
            ISupplierService.SupplierNotFoundException;

    ArticleSupplierDTO update(ArticleSupplier.ArticleSupplierId id, ArticleSupplierRequestDTO dto)
            throws ArticleSupplierNotFoundException,
            IArticleService.ArticleNotFoundException,
            ISupplierService.SupplierNotFoundException;

    void deleteById(ArticleSupplier.ArticleSupplierId id);
}
