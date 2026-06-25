package com.mns.cda.saas_facturation.DTO;

import java.math.BigDecimal;

public record TvaRequestDTO(
        String tvaName,
        BigDecimal tvaTaux) {
}
