package com.mns.cda.saas_facturation.validation;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validateur personnalisé pour les numéros de téléphone au format international E.164.
 *
 * <p>Utilisé en combinaison avec l'annotation {@link ValidPhoneNumber} via Bean Validation (JSR 380).
 * Un numéro est considéré valide s'il respecte les deux conditions suivantes :
 * <ul>
 *   <li>Il est reconnu comme valide par la librairie Google libphonenumber</li>
 *   <li>Il est déjà écrit exactement au format E.164 (ex: +33612345678), sans zéro superflu ni séparateur</li>
 * </ul>
 *
 * <p>Exemples :
 * <pre>
 *   +33612345678   → valide
 *   +330612345678  → invalide (double zéro après le préfixe)
 *   0612345678     → invalide (pas de préfixe international)
 *   +33612345abc   → invalide (caractères non numériques)
 * </pre>
 */
public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

    /**
     * Instance singleton de l'utilitaire Google libphonenumber.
     * Gère le parsing, la validation et le formatage des numéros internationaux.
     */
    private final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    /**
     * Vérifie si le numéro de téléphone fourni est valide au format E.164 strict.
     *
     * @param value   le numéro de téléphone à valider (peut être null)
     * @param context le contexte Bean Validation (non utilisé ici)
     * @return {@code true} si le numéro est valide, {@code false} sinon
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        // Un champ null ou vide est considéré valide ici :
        // la contrainte @NotBlank est responsable de rejeter les valeurs absentes.
        if (value == null || value.isBlank()) return true;

        try {
            // Parse le numéro sans région par défaut (null) :
            // cela oblige la présence d'un préfixe international (+33, +352, etc.)
            // Un numéro local comme "0612345678" lèvera une NumberParseException.
            Phonenumber.PhoneNumber number = phoneUtil.parse(value, null);

            // Vérifie que le numéro est valide selon les règles du pays détecté
            // (longueur correcte, préfixe opérateur existant, etc.)
            if (!phoneUtil.isValidNumber(number)) return false;

            // Reformate le numéro en E.164 canonique et compare avec la saisie originale.
            // Cela rejette les numéros avec un zéro superflu (+330612... au lieu de +33612...)
            // ou tout autre format non strictement E.164.
            String normalized = phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164);
            return normalized.equals(value);

        } catch (NumberParseException e) {
            // Le numéro n'a pas pu être parsé (format illisible, préfixe inconnu, etc.)
            // On retourne false sans propager l'exception : c'est une entrée invalide, pas une erreur serveur.
            return false;
        }
    }
}
