package com.mns.cda.saas_facturation.DTO.responseDTO;

import java.math.BigDecimal;

public record TvaResponseDTO (
        Long tvaId,
        String tvaName,
        BigDecimal tvaTaux
) {

}
