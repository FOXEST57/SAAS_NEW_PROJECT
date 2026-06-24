package com.mns.cda.saas_facturation.repository;

import com.mns.cda.saas_facturation.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
}
