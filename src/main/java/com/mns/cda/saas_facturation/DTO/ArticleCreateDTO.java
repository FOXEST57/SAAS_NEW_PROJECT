package com.mns.cda.saas_facturation.DTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ArticleCreateDTO(
        @NotBlank String artReference,
        @NotBlank String artName,
        @NotBlank String artDescription,
        @DecimalMin("0.0") Double artPriceExcludeTaxes,
        @Min(0) int artStock,
        @NotNull @Min(1) Long tvaId
) {}

