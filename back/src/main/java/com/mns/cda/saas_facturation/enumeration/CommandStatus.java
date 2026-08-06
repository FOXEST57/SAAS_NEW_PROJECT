package com.mns.cda.saas_facturation.enumeration;

public enum CommandStatus {

    CREATED, // est créé automatiquement à la création
    PENDING,
    CANCELED,
    ACCEPTED, // Crée automatiquement la facture associée
    DELIVERED

}
