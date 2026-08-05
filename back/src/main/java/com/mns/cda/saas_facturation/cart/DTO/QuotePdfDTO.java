package com.mns.cda.saas_facturation.cart.DTO;

import com.mns.cda.saas_facturation.document.DTO.DocumentPdfLineDTO;
import com.mns.cda.saas_facturation.document.DTO.DocumentPdfTvaDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>Tout ce qu'il faut pour dessiner un devis en PDF, et rien d'autre.</p>
 *
 * <p>Construit une seule fois, au moment où le devis est transmis au client.
 * Ensuite, plus personne ne retouche aux entités : le template travaille
 * uniquement avec ce qui est ici.</p>
 *
 * <p>Deux différences avec la facture, qui viennent du métier et non de la
 * technique :</p>
 *
 * <ul>
 *   <li>la date limite est une vraie donnée du devis
 *       ({@code qotExpirationDate}, saisie à la création), et non une échéance
 *       calculée comme sur une facture ;</li>
 *   <li>un devis peut en remplacer un autre. Quand c'est le cas,
 *       {@code replacesQuoteNumber} porte le numéro de la version précédente,
 *       pour que le client comprenne d'où vient ce nouveau document. Il vaut
 *       {@code null} pour un premier devis.</li>
 * </ul>
 *
 * <p>Les informations de <b>ton</b> entreprise ne sont pas ici : elles sont
 * identiques sur tous les documents, donc fournies à part par
 * {@code ICompanyService}.</p>
 */
public record QuotePdfDTO(
        String quoteNumber,
        String issuedDate,
        String expirationDate,
        /** Numéro du devis remplacé par celui-ci, {@code null} si c'est le premier. */
        String replacesQuoteNumber,

        String customerName,
        String customerAddress,
        String customerEmail,
        String customerPhone,

        List<DocumentPdfLineDTO> lines,
        List<DocumentPdfTvaDTO> tvaBreakdown,

        BigDecimal totalHT,
        BigDecimal totalTVA,
        BigDecimal totalTTC
) {
}