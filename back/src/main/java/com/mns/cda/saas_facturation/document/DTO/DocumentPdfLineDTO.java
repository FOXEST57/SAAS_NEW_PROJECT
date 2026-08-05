package com.mns.cda.saas_facturation.document.DTO;

import java.math.BigDecimal;

/**
 * <p>Une ligne du tableau d'un document imprimé, prête à être affichée.</p>
 *
 * <p>Utilisée aussi bien pour le devis que pour la facture : dans les deux
 * cas, on affiche les mêmes colonnes à partir de lignes déjà figées.</p>
 *
 * <p>Tout est calculé et mis en forme en amont : le template n'a plus qu'à
 * recopier ces valeurs dans les cases du tableau.</p>
 *
 * <ul>
 *   <li>{@code articleRef} et {@code articleName} : la référence et la
 *       désignation, telles qu'elles étaient au moment où le document a été
 *       établi</li>
 *   <li>{@code quantity} : la quantité</li>
 *   <li>{@code unitPriceHT} : le prix unitaire hors taxes</li>
 *   <li>{@code tvaLabel} : le taux à afficher, déjà écrit en toutes lettres
 *       (ex. « 20 % ») — le template n'a aucun calcul à faire</li>
 *   <li>{@code totalHT} : prix unitaire × quantité</li>
 * </ul>
 */
public record DocumentPdfLineDTO(
        String articleRef,
        String articleName,
        int quantity,
        BigDecimal unitPriceHT,
        String tvaLabel,
        BigDecimal totalHT
) {
}