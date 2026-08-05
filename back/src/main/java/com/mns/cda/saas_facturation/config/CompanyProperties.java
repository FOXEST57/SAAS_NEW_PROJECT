package com.mns.cda.saas_facturation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>Contient les informations de ton entreprise, celles qui doivent
 * apparaître sur les documents que tu envoies au client (devis, bon de
 * commande, facture).</p>
 *
 * <p>Spring remplit automatiquement cette classe avec les valeurs écrites dans
 * {@code application.properties} (les lignes qui commencent par
 * {@code app.company.}).</p>
 *
 * <ul>
 *   <li>{@code name} : la raison sociale, ex. « Klimafact SARL »</li>
 *   <li>{@code tagline} : la phrase d'accroche affichée sous le nom</li>
 *   <li>{@code address} : l'adresse du siège, sur une seule ligne</li>
 *   <li>{@code siret} : le numéro SIRET — obligatoire sur une facture</li>
 *   <li>{@code tvaNumber} : le numéro de TVA intracommunautaire</li>
 *   <li>{@code email} et {@code phone} : les coordonnées de contact</li>
 *   <li>{@code iban} : le compte sur lequel le client règle la facture</li>
 * </ul>
 *
 * <p>Note sur les noms : dans le fichier {@code .properties} on écrit
 * {@code app.company.tva-number} (avec un tiret), et Spring le relie tout seul
 * au champ {@code tvaNumber} (en majuscule au milieu).</p>
 */
@ConfigurationProperties(prefix = "app.company")
public record CompanyProperties(
        String name,
        String tagline,
        String address,
        String siret,
        String tvaNumber,
        String email,
        String phone,
        String iban
) {
}