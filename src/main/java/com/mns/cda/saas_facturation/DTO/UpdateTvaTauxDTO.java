package com.mns.cda.saas_facturation.DTO;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record UpdateTvaTauxDTO(
        @DecimalMin(value = "0.0") BigDecimal tvaTaux
) {
}
