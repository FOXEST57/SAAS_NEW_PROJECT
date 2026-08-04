package com.mns.cda.saas_facturation.enumeration;

public enum InvoiceStatus {
    CREATED,        // Brouillon
    ISSUED,         // Validée / émise
    SENT,           // Envoyée au client
    OVERDUE,        // Échue et non totalement réglée
    PARTIALLY_PAID, // Partiellement payée
    PAID,           // Totalement réglée
    CANCELLED       // Annulée
}
