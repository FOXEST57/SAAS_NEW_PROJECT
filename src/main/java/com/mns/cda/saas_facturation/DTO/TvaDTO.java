package com.mns.cda.saas_facturation.DTO;

import java.math.BigDecimal;

//Création d'un DTO de réponse pour les routes qui retourne en body une tva avec champ id
public record TvaDTO(
        Long tvaId,
        String tvaName,
        BigDecimal tvaTaux
) {
}
