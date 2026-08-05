package com.mns.cda.saas_facturation.cart.DTO;

import java.math.BigDecimal;

/**
 * <p>Une ligne du tableau de la facture, prête à être affichée.</p>
 *
 * <p>Tout est déjà calculé et mis en forme : le template n'a plus qu'à
 * recopier ces valeurs dans les cases du tableau.</p>
 *
 * <ul>
 *   <li>{@code articleRef} et {@code articleName} : la référence et la
 *       désignation, telles qu'elles étaient au moment de la facture</li>
 *   <li>{@code quantity} : la quantité facturée</li>
 *   <li>{@code unitPriceHT} : le prix unitaire hors taxes</li>
 *   <li>{@code tvaLabel} : le taux à afficher, déjà écrit en toutes lettres
 *       (ex. « 20 % ») — le template n'a aucun calcul à faire</li>
 *   <li>{@code totalHT} : prix unitaire × quantité</li>
 * </ul>
 */
public record InvoicePdfLineDTO(
        String articleRef,
        String articleName,
        int quantity,
        BigDecimal unitPriceHT,
        String tvaLabel,
        BigDecimal totalHT
) {
}