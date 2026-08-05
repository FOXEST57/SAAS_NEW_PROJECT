package com.mns.cda.saas_facturation.document;

import com.mns.cda.saas_facturation.location.model.Address;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

/**
 * <p>Petites transformations de texte et de nombres utilisées pour préparer un
 * document à imprimer (devis, facture).</p>
 *
 * <p>Ces méthodes ne concernent aucun document en particulier : mettre une
 * ville en majuscules ou arrondir un montant se fait de la même façon sur un
 * devis et sur une facture. Elles sont donc regroupées ici plutôt que copiées
 * dans chaque mapper — sans quoi les deux versions finiraient par diverger.</p>
 *
 * <p>Ce sont des méthodes {@code static} : elles ne dépendent de rien et ne
 * retiennent rien entre deux appels. Le même texte en entrée donne toujours le
 * même texte en sortie. En faire un composant Spring à injecter n'apporterait
 * que de la complication.</p>
 */
public final class DocumentFormat {

    /** Les montants s'affichent toujours avec deux décimales : ce sont des euros. */
    private static final int MONEY_SCALE = 2;

    /** Classe utilitaire : on ne l'instancie pas. */
    private DocumentFormat() {
    }

    /**
     * <p>Assemble une adresse sur une seule ligne, ex.
     * « 12 rue de la Paix, 69001 LYON ».</p>
     *
     * <p>Chaque morceau est facultatif en base : on ne garde que ceux qui sont
     * renseignés, pour éviter les virgules perdues et les doubles espaces.</p>
     */
    public static String formatAddress(Address address) {
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
    public static String capitalize(String value) {
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
    public static String upperCase(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.trim().toUpperCase(Locale.FRENCH);
    }

    /**
     * <p>Écrit un taux de TVA tel qu'il doit apparaître sur le document :
     * {@code 0.055} devient « 5,5 % », {@code 0.20} devient « 20 % ».</p>
     *
     * <p>{@code stripTrailingZeros()} supprime les zéros inutiles (20,00
     * devient 20) et la virgule remplace le point, comme il se doit en
     * français.</p>
     */
    public static String tvaLabel(BigDecimal rate) {
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
    public static BigDecimal money(BigDecimal value) {
        return value.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    /** <p>Ajoute un morceau de texte s'il existe vraiment, avec son séparateur.</p> */
    private static void appendIfPresent(StringBuilder target, String value, String separator) {
        if (value == null || value.isBlank()) {
            return;
        }
        if (!target.isEmpty()) {
            target.append(separator);
        }
        target.append(value.trim());
    }
}