package com.mns.cda.saas_facturation.cart.mapper;

import com.mns.cda.saas_facturation.cart.DTO.InvoicePdfDTO;
import com.mns.cda.saas_facturation.cart.DTO.InvoicePdfLineDTO;
import com.mns.cda.saas_facturation.cart.DTO.InvoicePdfTvaDTO;
import com.mns.cda.saas_facturation.cart.model.Invoice;
import com.mns.cda.saas_facturation.cart.model.InvoiceLine;
import com.mns.cda.saas_facturation.document.DocumentFormat;
import com.mns.cda.saas_facturation.exception.PdfGenerationException;
import com.mns.cda.saas_facturation.user.model.Customer;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * <p>Transforme une facture enregistrée en base en un objet prêt à imprimer
 * ({@link InvoicePdfDTO}).</p>
 *
 * <p>Tout ce qui relève de la mise en forme pure (adresses, majuscules, taux,
 * arrondis) est délégué à {@link DocumentFormat}, partagé avec le devis. Ne
 * reste ici que ce qui est propre à la facture : retrouver le client au bout
 * de la chaîne, et assembler le document.</p>
 *
 * <p><b>Important :</b> cette classe doit être appelée pendant que la
 * transaction est encore ouverte. Elle parcourt les liens entre entités
 * (facture → commande → devis → panier → client) et JPA ne sait le faire que
 * dans ce cadre.</p>
 */
@Service
public class InvoicePdfMapper {

    /** Format de date français, celui qu'attend un client francophone. */
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /** Délai de paiement accordé au client, en jours. */
    private static final int PAYMENT_DAYS = 30;

    public InvoicePdfDTO toDTO(Invoice invoice) {
        Customer customer = customerOf(invoice);
        List<InvoiceLine> sourceLines = invoice.getInvoiceLines() != null
                ? invoice.getInvoiceLines()
                : List.of();

        List<InvoicePdfLineDTO> lines = new ArrayList<>();

        // Base hors taxes cumulée pour chaque taux de TVA rencontré.
        // TreeMap : il compare les taux par leur valeur (0.20 et 0.2 sont donc
        // bien le même taux) et les garde triés du plus petit au plus grand.
        Map<BigDecimal, BigDecimal> baseByRate = new TreeMap<>();

        BigDecimal totalHT = BigDecimal.ZERO;

        for (InvoiceLine line : sourceLines) {
            BigDecimal lineHT = DocumentFormat.money(
                    line.getInvLnPriceHT().multiply(BigDecimal.valueOf(line.getInvLnQuantity()))
            );
            BigDecimal rate = line.getTvaRate();

            lines.add(new InvoicePdfLineDTO(
                    DocumentFormat.upperCase(line.getArticleRef()),
                    DocumentFormat.capitalize(line.getArticleName()),
                    line.getInvLnQuantity(),
                    line.getInvLnPriceHT(),
                    DocumentFormat.tvaLabel(rate),
                    lineHT
            ));

            // On additionne des montants déjà arrondis : ce que le client
            // additionnera à la main sur le papier tombera juste.
            totalHT = totalHT.add(lineHT);
            baseByRate.merge(rate, lineHT, BigDecimal::add);
        }

        List<InvoicePdfTvaDTO> tvaBreakdown = new ArrayList<>();
        BigDecimal totalTVA = BigDecimal.ZERO;

        for (Map.Entry<BigDecimal, BigDecimal> entry : baseByRate.entrySet()) {
            BigDecimal base = entry.getValue();
            BigDecimal amount = DocumentFormat.money(base.multiply(entry.getKey()));

            tvaBreakdown.add(new InvoicePdfTvaDTO(
                    DocumentFormat.tvaLabel(entry.getKey()), base, amount));
            totalTVA = totalTVA.add(amount);
        }

        return new InvoicePdfDTO(
                invoice.getInvoiceNumber(),
                invoice.getInvoiceCreatedDate().format(DATE_FORMAT),
                invoice.getInvoiceCreatedDate().plusDays(PAYMENT_DAYS).format(DATE_FORMAT),

                customer.getCtmFirstName() + " " + customer.getCtmLastName(),
                DocumentFormat.formatAddress(customer.getAddress()),
                customer.getCtmEmail(),
                customer.getCtmPhone(),

                lines,
                tvaBreakdown,

                DocumentFormat.money(totalHT),
                DocumentFormat.money(totalTVA),
                DocumentFormat.money(totalHT.add(totalTVA))
        );
    }

    /**
     * <p>Retrouve le client à facturer.</p>
     *
     * <p>La facture ne connaît pas directement son client : il faut remonter
     * la chaîne facture → commande → devis → panier → client. Si un maillon
     * manque, on s'arrête net : une facture doit obligatoirement identifier
     * son destinataire, mieux vaut un message clair qu'un PDF incomplet.</p>
     */
    private Customer customerOf(Invoice invoice) {
        Customer customer = null;

        if (invoice.getCommand() != null
                && invoice.getCommand().getQuote() != null
                && invoice.getCommand().getQuote().getCart() != null) {
            customer = invoice.getCommand().getQuote().getCart().getCustomer();
        }

        if (customer == null) {
            throw new PdfGenerationException(
                    "Impossible de générer la facture " + invoice.getInvoiceNumber()
                            + " : aucun client n'est rattaché à ce document."
            );
        }
        return customer;
    }
}