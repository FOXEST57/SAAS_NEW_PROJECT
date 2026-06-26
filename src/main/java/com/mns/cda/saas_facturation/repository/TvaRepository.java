package com.mns.cda.saas_facturation.repository;

import com.mns.cda.saas_facturation.model.Tva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository JPA gérant les accès en base de données pour l'entité {@link Tva}.
 *
 * <p>Étend {@link JpaRepository} qui fournit automatiquement les opérations CRUD
 * de base ({@code findAll}, {@code findById}, {@code save}, {@code deleteById}, etc.)
 * sans nécessiter d'implémentation manuelle.</p>
 *
 * <p>Les paramètres génériques précisent :</p>
 * <ul>
 *   <li>{@link Tva} — le type de l'entité gérée</li>
 *   <li>{@link Long} — le type de la clé primaire ({@code tvaId})</li>
 * </ul>
 *
 * <p>Aucune requête custom n'est définie ici : les opérations héritées de
 * {@link JpaRepository} sont suffisantes pour les besoins actuels du service.</p>
 *
 * @see Tva
 * @see JpaRepository
 */
@Repository
public interface TvaRepository extends JpaRepository<Tva, Long> {
}