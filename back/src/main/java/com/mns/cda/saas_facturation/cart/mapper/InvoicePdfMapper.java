package com.mns.cda.saas_facturation.cart.mapper;

import com.mns.cda.saas_facturation.cart.DTO.InvoicePdfDTO;
import com.mns.cda.saas_facturation.cart.DTO.InvoicePdfLineDTO;
import com.mns.cda.saas_facturation.cart.DTO.InvoicePdfTvaDTO;
import com.mns.cda.saas_facturation.cart.model.Invoice;
import com.mns.cda.saas_facturation.cart.model.InvoiceLine;
import com.mns.cda.saas_facturation.exception.PdfGenerationException;
import com.mns.cda.saas_facturation.location.model.Address;
import com.mns.cda.saas_facturation.user.model.Customer;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * <p>Transforme une facture enregistrée en base en un objet prêt à imprimer
 * ({@link InvoicePdfDTO}).</p>
 *
 * <p>C'est ici que se fait tout le travail de préparation : aller chercher le
 * client, mettre les dates en forme, calculer les totaux et regrouper la TVA
 * par taux. Le template, lui, ne fera plus qu'afficher.</p>
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

    /** Les montants s'affichent toujours avec deux décimales : ce sont des euros. */
    private static final int MONEY_SCALE = 2;

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
            BigDecimal lineHT = money(
                    line.getInvLnPriceHT().multiply(BigDecimal.valueOf(line.getInvLnQuantity()))
            );
            BigDecimal rate = line.getTvaRate();

            lines.add(new InvoicePdfLineDTO(
                    upperCase(line.getArticleRef()),
                    capitalize(line.getArticleName()),
                    line.getInvLnQuantity(),
                    line.getInvLnPriceHT(),
                    tvaLabel(rate),
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
            BigDecimal amount = money(base.multiply(entry.getKey()));

            tvaBreakdown.add(new InvoicePdfTvaDTO(tvaLabel(entry.getKey()), base, amount));
            totalTVA = totalTVA.add(amount);
        }

        return new InvoicePdfDTO(
                invoice.getInvoiceNumber(),
                invoice.getInvoiceCreatedDate().format(DATE_FORMAT),
                invoice.getInvoiceCreatedDate().plusDays(PAYMENT_DAYS).format(DATE_FORMAT),

                customer.getCtmFirstName() + " " + customer.getCtmLastName(),
                formatAddress(customer.getAddress()),
                customer.getCtmEmail(),
                customer.getCtmPhone(),

                lines,
                tvaBreakdown,

                money(totalHT),
                money(totalTVA),
                money(totalHT.add(totalTVA))
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

    /**
     * <p>Assemble l'adresse du client sur une seule ligne, ex.
     * « 12 rue de la Paix, 69001 Lyon ».</p>
     *
     * <p>Chaque morceau est facultatif en base : on ne garde que ceux qui sont
     * renseignés, pour éviter les virgules perdues et les doubles espaces.</p>
     */
    private String formatAddress(Address address) {
        if (address == null) {
            return "";
        }

        StringBuilder street = new StringBuilder();
        appendIfPresent(street, address.getAddNumber(), " ");
        appendIfPresent(street, address.getAddStreet(), " ");
        appendIfPresent(street, address.getAddComplement(), " ");

        StringBuilder city = new StringBuilder();
        if (address.getPostalCode() != null) {
            appendIfPresent(city, address.getPostalCode().getPCodeName(), " ");
        }
        // La ville est stockée en minuscules (LowercaseConverter) : on la
        // remet en majuscules, c'est la convention postale — « 69001 LYON ».
        if (address.getCity() != null && address.getCity().getCityName() != null) {
            appendIfPresent(city, address.getCity().getCityName().toUpperCase(Locale.FRENCH), " ");
        }

        if (street.isEmpty()) {
            return city.toString();
        }
        if (city.isEmpty()) {
            return street.toString();
        }
        return street + ", " + city;
    }

    /**
     * <p>Met une majuscule au premier caractère, pour la désignation des
     * articles : « casque audio » devient « Casque audio ».</p>
     *
     * <p>Les articles sont enregistrés en minuscules
     * ({@code LowercaseConverter}), ce qui est pratique pour les recherches
     * mais fait négligé sur un document envoyé au client. On ne corrige donc
     * qu'à l'affichage : la donnée en base n'est pas touchée.</p>
     *
     * <p>Seule la première lettre est modifiée — le reste est laissé tel quel,
     * car on ne peut pas deviner ce qui mérite une majuscule au milieu d'un
     * nom de produit.</p>
     */
    private String capitalize(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        String trimmed = value.trim();
        return trimmed.substring(0, 1).toUpperCase(Locale.FRENCH) + trimmed.substring(1);
    }

    /**
     * <p>Met une référence entièrement en majuscules : « ref-004 » devient
     * « REF-004 ».</p>
     *
     * <p>C'est l'usage pour un code article : plus lisible, et impossible à
     * confondre avec du texte ordinaire quand le client recopie la référence
     * pour te la citer.</p>
     */
    private String upperCase(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.trim().toUpperCase(Locale.FRENCH);
    }

    /** <p>Ajoute un morceau de texte s'il existe vraiment, avec son séparateur.</p> */
    private void appendIfPresent(StringBuilder target, String value, String separator) {
        if (value == null || value.isBlank()) {
            return;
        }
        if (!target.isEmpty()) {
            target.append(separator);
        }
        target.append(value.trim());
    }

    /**
     * <p>Écrit un taux de TVA tel qu'il doit apparaître sur la facture :
     * {@code 0.055} devient « 5,5 % », {@code 0.20} devient « 20 % ».</p>
     *
     * <p>{@code stripTrailingZeros()} supprime les zéros inutiles (20,00
     * devient 20) et la virgule remplace le point, comme il se doit en
     * français.</p>
     */
    private String tvaLabel(BigDecimal rate) {
        if (rate == null) {
            return "—";
        }
        String value = rate.multiply(BigDecimal.valueOf(100))
                .stripTrailingZeros()
                .toPlainString()
                .replace('.', ',');
        return value + " %";
    }

    /**
     * <p>Arrondit un montant à deux décimales, à l'euro près du dessus quand
     * on est pile au milieu ({@code HALF_UP}) — la règle habituelle en
     * comptabilité.</p>
     */
    private BigDecimal money(BigDecimal value) {
        return value.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }
}