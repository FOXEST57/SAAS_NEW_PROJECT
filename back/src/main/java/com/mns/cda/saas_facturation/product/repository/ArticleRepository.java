package com.mns.cda.saas_facturation.product.repository;

import com.mns.cda.saas_facturation.product.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository JPA gérant les accès en base de données pour l'entité {@link Article}.
 *
 * <p>Étend {@link JpaRepository} qui fournit automatiquement les opérations CRUD
 * de base ({@code findAll}, {@code findById}, {@code save}, {@code deleteById}, etc.)
 * sans nécessiter d'implémentation manuelle.</p>
 *
 * <p>Les paramètres génériques précisent :</p>
 * <ul>
 *   <li>{@link Article} — le type de l'entité gérée</li>
 *   <li>{@link Long} — le type de la clé primaire ({@code articleId})</li>
 * </ul>
 *
 * @see Article
 * @see JpaRepository
 */
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    Optional<Article> findArticleByArtReference(String artReference);
}