package com.mns.cda.saas_facturation.document.DTO;

import java.math.BigDecimal;

/**
 * <p>Une ligne du récapitulatif de TVA, en bas d'un document imprimé.</p>
 *
 * <p>La loi impose de détailler la TVA <b>taux par taux</b>, et non en un seul
 * montant global. C'est important ici : en chauffage et climatisation, un même
 * document mélange couramment plusieurs taux (5,5 % sur la rénovation
 * énergétique, 10 % sur certains travaux, 20 % sur le reste).</p>
 *
 * <p>On regroupe donc les lignes par taux, et on obtient un objet de ce type
 * par taux présent sur le document.</p>
 *
 * <ul>
 *   <li>{@code tvaLabel} : le taux, ex. « 5,5 % »</li>
 *   <li>{@code baseHT} : le total hors taxes concerné par ce taux</li>
 *   <li>{@code amount} : le montant de TVA correspondant</li>
 * </ul>
 */
public record DocumentPdfTvaDTO(
        String tvaLabel,
        BigDecimal baseHT,
        BigDecimal amount
) {
}