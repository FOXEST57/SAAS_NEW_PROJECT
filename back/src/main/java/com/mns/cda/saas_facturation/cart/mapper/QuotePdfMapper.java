package com.mns.cda.saas_facturation.cart.mapper;

import com.mns.cda.saas_facturation.cart.DTO.QuotePdfDTO;
import com.mns.cda.saas_facturation.cart.model.Quote;
import com.mns.cda.saas_facturation.cart.model.QuoteLine;
import com.mns.cda.saas_facturation.document.DTO.DocumentPdfLineDTO;
import com.mns.cda.saas_facturation.document.DTO.DocumentPdfTvaDTO;
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
 * <p>Transforme un devis enregistré en base en un objet prêt à imprimer
 * ({@link QuotePdfDTO}).</p>
 *
 * <p>Fonctionne exactement comme {@link InvoicePdfMapper}, avec lequel il
 * partage toute la mise en forme ({@link DocumentFormat}). Ne reste ici que ce
 * qui est propre au devis : retrouver le client, lire la date de validité, et
 * signaler le cas échéant quel devis celui-ci remplace.</p>
 *
 * <p><b>Important :</b> cette classe doit être appelée pendant que la
 * transaction est encore ouverte. Elle parcourt les liens entre entités
 * (devis → panier → client) et JPA ne sait le faire que dans ce cadre.</p>
 */
@Service
public class QuotePdfMapper {

    /**
     * Format de date français. Le même objet sert pour les deux dates du
     * devis, bien qu'elles n'aient pas le même type : la date de création est
     * un {@code LocalDateTime}, celle d'expiration un {@code LocalDate}.
     */
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public QuotePdfDTO toDTO(Quote quote) {
        Customer customer = customerOf(quote);
        List<QuoteLine> sourceLines = quote.getQotLines() != null
                ? quote.getQotLines()
                : List.of();

        List<DocumentPdfLineDTO> lines = new ArrayList<>();

        // Base hors taxes cumulée pour chaque taux de TVA rencontré.
        // TreeMap : il compare les taux par leur valeur (0.20 et 0.2 sont donc
        // bien le même taux) et les garde triés du plus petit au plus grand.
        Map<BigDecimal, BigDecimal> baseByRate = new TreeMap<>();

        BigDecimal totalHT = BigDecimal.ZERO;

        for (QuoteLine line : sourceLines) {
            BigDecimal lineHT = DocumentFormat.money(
                    line.getQotLnPriceHT().multiply(BigDecimal.valueOf(line.getQotLnQuantity()))
            );
            BigDecimal rate = line.getTvaRate();

            lines.add(new DocumentPdfLineDTO(
                    DocumentFormat.upperCase(line.getArticleRef()),
                    DocumentFormat.capitalize(line.getArticleName()),
                    line.getQotLnQuantity(),
                    line.getQotLnPriceHT(),
                    DocumentFormat.tvaLabel(rate),
                    lineHT
            ));

            // On additionne des montants déjà arrondis : ce que le client
            // additionnera à la main sur le papier tombera juste.
            totalHT = totalHT.add(lineHT);
            baseByRate.merge(rate, lineHT, BigDecimal::add);
        }

        List<DocumentPdfTvaDTO> tvaBreakdown = new ArrayList<>();
        BigDecimal totalTVA = BigDecimal.ZERO;

        for (Map.Entry<BigDecimal, BigDecimal> entry : baseByRate.entrySet()) {
            BigDecimal base = entry.getValue();
            BigDecimal amount = DocumentFormat.money(base.multiply(entry.getKey()));

            tvaBreakdown.add(new DocumentPdfTvaDTO(
                    DocumentFormat.tvaLabel(entry.getKey()), base, amount));
            totalTVA = totalTVA.add(amount);
        }

        return new QuotePdfDTO(
                quote.getQotNumber(),
                quote.getQotCreatedDate().format(DATE_FORMAT),
                quote.getQotExpirationDate().format(DATE_FORMAT),
                parentNumberOf(quote),

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
     * <p>Retrouve le client destinataire du devis.</p>
     *
     * <p>Le devis ne connaît pas directement son client : il faut passer par
     * le panier dont il est issu. Si le lien est rompu, on s'arrête net —
     * envoyer un devis sans destinataire identifié n'aurait aucun sens.</p>
     */
    private Customer customerOf(Quote quote) {
        Customer customer = quote.getCart() != null ? quote.getCart().getCreator() : null;

        if (customer == null) {
            throw new PdfGenerationException(
                    "Impossible de générer le devis " + quote.getQotNumber()
                            + " : aucun client n'est rattaché à ce document."
            );
        }
        return customer;
    }

    /**
     * <p>Numéro du devis que celui-ci remplace, ou {@code null} s'il s'agit
     * d'un premier devis.</p>
     *
     * <p>Quand le client demande une modification, on ne retouche pas le devis
     * déjà transmis : on en émet une nouvelle version, qui pointe vers la
     * précédente ({@code qotParent}). L'afficher sur le PDF évite au client de
     * se demander lequel des deux documents fait foi.</p>
     */
    private String parentNumberOf(Quote quote) {
        return quote.getQotParent() != null ? quote.getQotParent().getQotNumber() : null;
    }
}