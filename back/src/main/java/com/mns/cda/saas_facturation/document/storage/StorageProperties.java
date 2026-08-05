package com.mns.cda.saas_facturation.document.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration du stockage des documents générés (devis, commandes, factures au format PDF).
 *
 * <p>Regroupe en un seul endroit typé ce que {@code @Value} éparpillerait dans chaque classe qui
 * en a besoin. Chargée automatiquement par Spring Boot depuis les propriétés préfixées
 * {@code app.storage.*} (voir {@code application.properties}) grâce à
 * {@code @ConfigurationPropertiesScan}, posée sur {@code SaasFacturationApplication}.</p>
 *
 * <p>Immuable par construction (record) : une fois l'application démarrée, cette configuration
 * ne doit plus changer pendant l'exécution.</p>
 *
 * @param type      implémentation de stockage active — {@code "local"} en développement,
 *                  {@code "s3"} en production. Pilote quelle implémentation de
 *                  {@code DocumentStorageService} Spring instancie (voir {@code @ConditionalOnProperty}
 *                  sur les implémentations correspondantes).
 * @param localPath répertoire racine du stockage local, relatif au répertoire d'exécution de
 *                  l'application. N'est utilisé que lorsque {@code type = "local"}.
 */
@ConfigurationProperties(prefix = "app.storage")
public record StorageProperties(
        String type,
        String localPath
) {
}