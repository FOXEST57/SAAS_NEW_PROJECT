package com.mns.cda.saas_facturation.repository;

import com.mns.cda.saas_facturation.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository JPA gérant les accès en base de données pour l'entité {@link Supplier}.
 *
 * <p>Étend {@link JpaRepository} qui fournit automatiquement les opérations CRUD
 * de base ({@code findAll}, {@code findById}, {@code save}, {@code deleteById}, etc.)
 * sans nécessiter d'implémentation manuelle.</p>
 *
 * <p>Les paramètres génériques précisent :</p>
 * <ul>
 *   <li>{@link Supplier} — le type de l'entité gérée</li>
 *   <li>{@link Long} — le type de la clé primaire ({@code splId})</li>
 * </ul>
 *
 * <p>Aucune requête custom n'est définie ici : la gestion des doublons de nom
 * est assurée directement par la contrainte {@code unique = true} sur le champ
 * {@code splName} de l'entité {@link Supplier}, qui déclenche une
 * {@code DataIntegrityViolationException} interceptée par le
 * {@code GlobalExceptionInterceptor} en réponse HTTP 409.</p>
 *
 * @see Supplier
 * @see JpaRepository
 */
@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}