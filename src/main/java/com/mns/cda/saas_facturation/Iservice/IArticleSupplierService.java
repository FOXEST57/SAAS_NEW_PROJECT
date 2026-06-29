package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.model.ArticleSupplier;

import java.util.List;
import java.util.Optional;

public interface IArticleSupplierService {

    public static class ArticleSupplierNotFoundException extends Exception {}

    List<ArticleSupplier> findAll();

    Optional<ArticleSupplier> findById(ArticleSupplier.ArticleSupplierId id);

    ArticleSupplier create(ArticleSupplier articleSupplier);

    ArticleSupplier update(ArticleSupplier articleSupplier) throws ArticleSupplierNotFoundException;

    void deleteById(ArticleSupplier.ArticleSupplierId id);
}
