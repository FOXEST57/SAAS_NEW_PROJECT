package com.mns.cda.saas_facturation.exception;

/**
 * <p>Levée quand la génération d'un PDF échoue — template introuvable, erreur de
 * mise en page, HTML mal formé, etc.</p>
 */
public class PdfGenerationException extends RuntimeException {

    public PdfGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public PdfGenerationException(String message) {
        super(message);
    }
}