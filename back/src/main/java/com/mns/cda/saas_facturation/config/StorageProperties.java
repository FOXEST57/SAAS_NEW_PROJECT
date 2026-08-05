package com.mns.cda.saas_facturation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>Contient les réglages de stockage des PDF (devis, commandes, factures).</p>
 *
 * <p>Spring remplit automatiquement cette classe avec les valeurs écrites dans
 * {@code application.properties} (les lignes qui commencent par {@code app.storage.}).</p>
 *
 * <ul>
 *   <li>{@code type} : où on stocke les fichiers — {@code "local"} (sur le disque)
 *       ou {@code "s3"} (dans le cloud)</li>
 *   <li>{@code localPath} : le dossier utilisé quand {@code type = "local"}</li>
 * </ul>
 */
@ConfigurationProperties(prefix = "app.storage")
public record StorageProperties(
        String type,
        String localPath
) {
}