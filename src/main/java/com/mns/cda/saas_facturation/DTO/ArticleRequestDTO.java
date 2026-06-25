package com.mns.cda.saas_facturation.DTO;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

//Création d'un DTO pour tout ce qui va être insertion ou modification en base de données
public record ArticleRequestDTO(
        @NotBlank String artReference,
        @NotBlank String artName,
        @NotBlank String artDescription,
        @DecimalMin("0.0") BigDecimal artPriceExcludeTaxes,
        @Min(0) int artStock,
        @NotNull @Min(1) Long tvaId,
        @Positive Long supplierId
) {}

