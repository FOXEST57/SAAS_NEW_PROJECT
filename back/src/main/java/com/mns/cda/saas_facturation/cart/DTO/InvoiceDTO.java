package com.mns.cda.saas_facturation.cart.DTO;

import com.mns.cda.saas_facturation.enumeration.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Facture émise.
 *
 * <p>{@code commandId} rétablit le chaînage du document. Depuis que
 * {@code ReferenceCounterService} attribue à chaque type sa propre séquence,
 * le numéro de facture ne coïncide plus avec celui du devis ni avec la
 * référence du panier : le rapprochement par la référence, qui fonctionnait
 * tant que le pipeline recopiait la même chaîne, n'est plus possible. Sans ce
 * champ, une facture est un document isolé — impossible de remonter à sa
 * commande, à son devis, ni au panier d'origine.</p>
 *
 * <p>{@code remaining} est le montant encore dû, règlements déduits. Il évite
 * au client de recomposer lui-même le total TTC et la somme des paiements, et
 * garantit surtout qu'il affiche la même borne que celle appliquée par
 * {@code PaymentService} lorsqu'il refuse un versement excédentaire.</p>
 */
public record InvoiceDTO (
    Long invoiceId,
    String invoiceNumber,
    LocalDateTime invoiceCreatedDate,
    String invoicePathPDF,
    InvoiceStatus invoiceStatus,
    List<InvoiceLineDTO> invoiceLines,
    Long creatorId,
    String customerEmail,
    Long commandId,
    BigDecimal totalTTC,
    BigDecimal totalPaid,
    BigDecimal remaining
){}
