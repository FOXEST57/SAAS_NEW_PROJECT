package com.mns.cda.saas_facturation.cart.DTO;

import com.mns.cda.saas_facturation.document.DTO.DocumentPdfLineDTO;
import com.mns.cda.saas_facturation.document.DTO.DocumentPdfTvaDTO;
import java.math.BigDecimal;

import java.util.List;

/**
 * <p>Tout ce qu'il faut pour dessiner une facture en PDF, et rien d'autre.</p>
 *
 * <p>Cet objet est construit une seule fois, au moment où la facture est
 * émise, à partir des entités en base. Ensuite, plus personne ne retouche aux
 * entités : le template travaille uniquement avec ce qui est ici.</p>
 *
 * <p>Les dates sont déjà écrites en texte (ex. « 05/08/2026 ») plutôt que
 * laissées en {@code LocalDateTime} : le formatage d'une date dépend de la
 * langue, et c'est le genre de logique qui n'a rien à faire dans un
 * template.</p>
 *
 * <p>Les informations de <b>ton</b> entreprise ne sont pas ici : elles sont
 * identiques sur tous les documents, donc fournies à part par
 * {@code ICompanyService}. Ici, on ne trouve que ce qui est propre à
 * <b>cette</b> facture.</p>
 *
 * <ul>
 *   <li>{@code invoiceNumber} : le numéro de la facture</li>
 *   <li>{@code issuedDate} : la date d'émission</li>
 *   <li>{@code dueDate} : la date limite de paiement (émission + 30 jours)</li>
 *   <li>{@code customerName} à {@code customerPhone} : le bloc « adressé à »,
 *       l'adresse étant déjà assemblée sur une seule ligne</li>
 *   <li>{@code lines} : le tableau des articles facturés</li>
 *   <li>{@code vatBreakdown} : le récapitulatif de TVA, un objet par taux</li>
 *   <li>{@code totalHT}, {@code totalTVA}, {@code totalTTC} : les totaux du
 *       pied de facture</li>
 * </ul>
 */
public record InvoicePdfDTO(
        String invoiceNumber,
        String issuedDate,
        String dueDate,

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