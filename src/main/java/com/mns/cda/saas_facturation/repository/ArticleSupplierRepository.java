package com.mns.cda.saas_facturation.repository;

import com.mns.cda.saas_facturation.model.ArticleSupplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticleSupplierRepository extends JpaRepository<ArticleSupplier, ArticleSupplier.ArticleSupplierId> {

}
