package com.mns.cda.saas_facturation.DTO;

import java.math.BigDecimal;

public record TvaResponseDTO (
        Long tvaId,
        String tvaName,
        BigDecimal tvaTaux
) {

}
