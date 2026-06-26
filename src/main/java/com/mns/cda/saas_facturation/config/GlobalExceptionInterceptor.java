package com.mns.cda.saas_facturation.config;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Intercepteur global des exceptions HTTP pour l'application SaaS Facturation.
 *
 * <p>Cette classe centralise la gestion des erreurs levées par les contrôleurs REST.
 * Au lieu de laisser chaque contrôleur gérer ses propres exceptions, toutes les erreurs
 * remontent ici et sont transformées en réponses JSON claires et cohérentes pour le client.</p>
 *
 * <p>Elle est annotée avec {@link RestControllerAdvice}, ce qui en fait un composant
 * Spring appliqué automatiquement à tous les {@code @RestController} de l'application.</p>
 *
 * <p>Exceptions actuellement gérées :</p>
 * <ul>
 *   <li>{@link MethodArgumentNotValidException} → 400 Bad Request (erreurs de validation Bean)</li>
 *   <li>{@link DataIntegrityViolationException} → 409 Conflict (violations de contraintes SQL)</li>
 * </ul>
 *
 * @see RestControllerAdvice
 */
@RestControllerAdvice
public class GlobalExceptionInterceptor {

    /**
     * Gère les erreurs de validation des données reçues dans le corps des requêtes HTTP.
     *
     * <p>Cette méthode est déclenchée automatiquement lorsqu'un DTO annoté avec
     * {@code @Valid} ou {@code @Validated} ne passe pas les contraintes de validation
     * Bean (ex : {@code @NotNull}, {@code @Size}, {@code @Email}, etc.).</p>
     *
     * <p>Elle parcourt l'ensemble des erreurs de champs détectées et les regroupe
     * dans une {@code Map} dont la clé est le nom du champ en erreur et la valeur
     * est le message de contrainte associé.</p>
     *
     * <p>Exemple de réponse JSON retournée :</p>
     * <pre>{@code
     * {
     *   "email": "doit être une adresse email valide",
     *   "nom": "ne doit pas être vide"
     * }
     * }</pre>
     *
     * @param ex l'exception levée par Spring lors de l'échec de la validation,
     *           contenant le détail de chaque champ invalide
     * @return une {@code Map<String, String>} associant chaque champ invalide
     *         à son message d'erreur, retournée avec le statut HTTP 400 Bad Request
     * @see MethodArgumentNotValidException
     * @see FieldError
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> constraintViolationInterceptor(MethodArgumentNotValidException ex) {

        // Map qui va accueillir les paires { nomDuChamp : messageErreur }
        Map<String, String> errors = new HashMap<>();

        // On itère sur chaque erreur de champ détectée par le moteur de validation
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            // Clé   = nom du champ du DTO (ex : "email", "telephone")
            // Valeur = message défini dans l'annotation de contrainte (ex : "doit être une adresse valide")
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return errors;
    }

    /**
     * Gère les violations de contraintes détectées au niveau de la base de données.
     *
     * <p>Cette méthode est déclenchée lorsque JPA/Hibernate tente de persister ou
     * mettre à jour une entité qui viole une contrainte d'intégrité SQL, par exemple :</p>
     * <ul>
     *   <li>une valeur dupliquée sur une colonne {@code UNIQUE}</li>
     *   <li>une violation de clé étrangère ({@code FOREIGN KEY})</li>
     *   <li>une valeur {@code NOT NULL} manquante non interceptée en amont</li>
     * </ul>
     *
     * <p>Le message retourné est volontairement générique afin de ne pas exposer
     * les détails internes du schéma de base de données au client.</p>
     *
     * <p>Exemple de réponse JSON retournée :</p>
     * <pre>{@code
     * {
     *   "Erreur": "Erreur de contrainte"
     * }
     * }</pre>
     *
     * @param ex l'exception levée par Spring Data lors d'une violation d'intégrité SQL
     * @return une {@code Map<String, String>} avec un message d'erreur générique,
     *         retournée avec le statut HTTP 409 Conflict
     * @see DataIntegrityViolationException
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> getConstraintViolationDataBase(DataIntegrityViolationException ex) {
        return Map.of("Erreur", "Erreur de contrainte");
    }
}