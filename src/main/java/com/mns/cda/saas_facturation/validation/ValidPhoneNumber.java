package com.mns.cda.saas_facturation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation de validation personnalisée pour les numéros de téléphone internationaux.
 *
 * <p>Vérifie que le numéro annoté respecte le format E.164 strict (ex: +33612345678).
 * La logique de validation est déléguée à {@link PhoneNumberValidator}.
 *
 * <p>Utilisation sur un champ :
 * <pre>
 *   {@code @ValidPhoneNumber}
 *   private String splPhone;
 * </pre>
 *
 * Utilisation sur un paramètre de méthode :
 * <pre>
 *   public void create({@code @ValidPhoneNumber} String phone) { ... }
 * </pre>
 */
@Constraint(validatedBy = PhoneNumberValidator.class) // Lie cette annotation à son validateur
@Target({ ElementType.FIELD, ElementType.PARAMETER })  // Applicable sur un champ ou un paramètre
@Retention(RetentionPolicy.RUNTIME)                    // L'annotation est conservée à l'exécution (nécessaire pour Bean Validation)
public @interface ValidPhoneNumber {

    /**
     * Message d'erreur retourné lorsque la validation échoue.
     * Peut être surchargé à l'usage : {@code @ValidPhoneNumber(message = "Numéro invalide")}.
     */
    String message() default "Numéro de téléphone invalide (format international requis : +33...)";

    /**
     * Permet de regrouper des contraintes pour les déclencher sélectivement.
     * Requis par la spécification Bean Validation (JSR 380) — laisser vide par défaut.
     */
    Class<?>[] groups() default {};

    /**
     * Permet d'associer des métadonnées à la contrainte (ex: niveau de sévérité).
     * Requis par la spécification Bean Validation (JSR 380) — laisser vide par défaut.
     */
    Class<? extends Payload>[] payload() default {};
}
