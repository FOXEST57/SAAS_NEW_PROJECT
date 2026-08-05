package com.mns.cda.saas_facturation.exception;

/**
 * <p>Levée quand le stockage d'un fichier échoue — disque plein, dossier
 * inaccessible, problème réseau vers le cloud, etc.</p>
 */
public class DocumentStorageException extends RuntimeException {

    public DocumentStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public DocumentStorageException(String message) {
        super(message);
    }
}