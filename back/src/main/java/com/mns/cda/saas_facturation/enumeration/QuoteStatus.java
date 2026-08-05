package com.mns.cda.saas_facturation.enumeration;

public enum QuoteStatus {
    CREATED, //automatique a la création
    ACCEPTED, // crée la commande
    PENDING,
    REJECTED,
    EXPIRED,
    CLOSED,
    REVISITED // Crée un Panier Revisited
}
