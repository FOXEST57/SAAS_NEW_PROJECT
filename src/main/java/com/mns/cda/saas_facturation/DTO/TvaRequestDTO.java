package com.mns.cda.saas_facturation.DTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record TvaRequestDTO(
        @NotBlank String tvaName,
        @DecimalMin(value = "0.0") BigDecimal tvaTaux) {
}
