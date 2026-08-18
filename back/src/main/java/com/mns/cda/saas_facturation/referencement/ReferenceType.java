package com.mns.cda.saas_facturation.referencement;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ReferenceType {
    ARTICLE("ART"),
    CART("CRT"),
    QUOTE("QOT"),
    COMMAND("CMD"),
    INVOICE("INV");

    private final String code;
}
