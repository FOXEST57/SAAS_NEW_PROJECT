package com.mns.cda.saas_facturation.repository;

import com.mns.cda.saas_facturation.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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
 *   <li>{@link Long} — le type de la clé primaire ({@code artId})</li>
 * </ul>
 *
 * @see Article
 * @see JpaRepository
 */
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    /**
     * Récupère la liste de tous les articles associés à un fournisseur spécifique.
     *
     * <p>Cette méthode utilise la convention de nommage Spring Data JPA :
     * {@code findBy} + {@code Supplier} (champ de {@link Article}) + {@code SplId}
     * (champ de {@link com.mns.cda.saas_facturation.model.Supplier}).
     * Spring génère automatiquement la requête SQL correspondante à l'exécution.</p>
     *
     * <p>Requête SQL équivalente :</p>
     * <pre>{@code
     * SELECT * FROM article WHERE supplier_id = :id
     * }</pre>
     *
     * @param id l'identifiant unique du fournisseur dont on veut récupérer les articles
     * @return une {@link List} d'articles associés à ce fournisseur
     *         (vide si le fournisseur n'a aucun article)
     */
    List<Article> findBySupplierSplId(Long id);
}