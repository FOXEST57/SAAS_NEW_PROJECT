package com.mns.cda.saas_facturation.DTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

//Création d'un DTO pour tout ce qui va être insertion ou modification en base de données
public record ArticleRequestDTO(
        @NotBlank @Column(unique = true) String artReference,
        @NotBlank String artName,
        @NotBlank String artDescription,
        @DecimalMin(value = "0.0", inclusive = false) BigDecimal artPriceExcludeTaxes,
        @Min(0) int artStock,
        @NotNull @Min(1) Long tvaId,
        Long categoryId,
        @Positive Long supplierId
) {}

