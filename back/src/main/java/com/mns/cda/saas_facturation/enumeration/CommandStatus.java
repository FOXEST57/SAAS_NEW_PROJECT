package com.mns.cda.saas_facturation.enumeration;

public enum CommandStatus {

    CREATED, // est crée automatiquement a la création
    PENDING,
    CANCELED,
    ACCEPTED, // Crée automatiquement la facture associée
    DELIVERED

}
