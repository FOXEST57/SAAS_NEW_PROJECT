package com.mns.cda.saas_facturation.enumeration;

public enum InvoiceStatus {
    CREATED,        // Mis automatiquement à la création
    ISSUED,         // Validée / émise
    SENT,           // Envoyée au client
    OVERDUE,        // Échue et non totalement réglée
    PARTIALLY_PAID, // Partiellement payée
    PAID,           // Totalement réglée
    CANCELLED       // Annulée
}
