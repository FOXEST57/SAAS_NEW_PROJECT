package com.mns.cda.saas_facturation.repository;

import com.mns.cda.saas_facturation.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    // Requête qui renvoi une liste d'article par id fournisseur
    List<Article> findBySupplierSplId(Long id);
}
