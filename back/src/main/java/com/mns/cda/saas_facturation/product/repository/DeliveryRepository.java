package com.mns.cda.saas_facturation.product.repository;

import com.mns.cda.saas_facturation.product.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository JPA gérant les accès en base de données pour l'entité {@link Delivery}.
 *
 * <p>Étend {@link JpaRepository} qui fournit automatiquement les opérations CRUD
 * de base ({@code findAll}, {@code findById}, {@code save}, {@code deleteById}, etc.)
 * sans nécessiter d'implémentation manuelle.</p>
 *
 * <p>Les paramètres génériques précisent :</p>
 * <ul>
 *   <li>{@link Delivery} — le type de l'entité gérée</li>
 *   <li>{@link Long} — le type de la clé primaire ({@code deliveryId})</li>
 * </ul>
 *
 * @see Delivery
 * @see JpaRepository
 */
@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}