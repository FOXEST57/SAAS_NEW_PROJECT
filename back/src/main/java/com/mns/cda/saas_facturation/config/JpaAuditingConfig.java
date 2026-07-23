package com.mns.cda.saas_facturation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuration dédiée à l'activation du JPA Auditing ({@code createdAt}, {@code updatedAt}...).
 *
 * <p>Volontairement séparée de la classe {@code @SpringBootApplication} : cette dernière
 * est automatiquement chargée par {@code @WebMvcTest} pour détecter la configuration
 * racine du contexte. Si {@code @EnableJpaAuditing} y était déclaré directement, chaque
 * test {@code @WebMvcTest} du projet échouerait avec {@code "JPA metamodel must not be empty"},
 * car aucune entité JPA n'est scannée dans ce type de test "web only".</p>
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
