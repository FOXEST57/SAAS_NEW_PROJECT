package com.mns.cda.saas_facturation.DTO.updateDTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ArticleUpdateDTO(
        @NotBlank @Column(unique = true) String artReference,
        @NotBlank String artName,
        @NotBlank String artDescription,
        @DecimalMin(value = "0.0", inclusive = false) BigDecimal artPriceExcludeTaxes,
        @Min(0) int artStock,
        @NotNull @Min(1) Long tvaId,
        @Positive Long categoryId
) {
}
