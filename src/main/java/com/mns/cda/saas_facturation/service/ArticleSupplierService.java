package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.Iservice.IArticleSupplierService;
import com.mns.cda.saas_facturation.model.ArticleSupplier;
import com.mns.cda.saas_facturation.repository.ArticleSupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ArticleSupplierService implements IArticleSupplierService {

    protected final ArticleSupplierRepository  articleSupplierRepository;

    @Override
    public List<ArticleSupplier> findAll() {
        return articleSupplierRepository.findAll(); }

    @Override
    public Optional<ArticleSupplier> findById(ArticleSupplier.ArticleSupplierId id) {
        return articleSupplierRepository.findById(id);
    }

    @Override
    public ArticleSupplier create(ArticleSupplier articleSupplier) {
        return articleSupplierRepository.save(articleSupplier);
    }

    @Override
    public ArticleSupplier update(ArticleSupplier articleSupplier) throws ArticleSupplierNotFoundException {
        return articleSupplierRepository.save(articleSupplier);
    }

    @Override
    public void deleteById(ArticleSupplier.ArticleSupplierId id) {
        articleSupplierRepository.deleteById(id);
    }
}
