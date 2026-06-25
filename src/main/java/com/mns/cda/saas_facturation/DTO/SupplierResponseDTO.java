package com.mns.cda.saas_facturation.DTO;

//Création d'un DTO de réponse pour les routes qui retourne un body afin d'éviter les Json Infini
public record SupplierResponseDTO (
        Long splId,
        String splName
) {
}

