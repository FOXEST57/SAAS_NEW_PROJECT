package com.mns.cda.saas_facturation.repository;

import com.mns.cda.saas_facturation.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository JPA gérant les accès en base de données pour l'entité {@link Category}.
 *
 * <p>Étend {@link JpaRepository} qui fournit automatiquement les opérations CRUD
 * de base ({@code findAll}, {@code findById}, {@code save}, {@code deleteById}, etc.)
 * sans nécessiter d'implémentation manuelle.</p>
 *
 * <p>Les paramètres génériques précisent :</p>
 * <ul>
 *   <li>{@link Category} — le type de l'entité gérée</li>
 *   <li>{@link Long} — le type de la clé primaire ({@code catId})</li>
 * </ul>
 *
 * <p>Aucune requête custom n'est définie ici : les opérations héritées de
 * {@link JpaRepository} sont suffisantes pour les besoins actuels du service.</p>
 *
 * @see Category
 * @see JpaRepository
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}