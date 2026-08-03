package com.mns.cda.saas_facturation.product.DTO.updateDTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record ArticleUpdateDTO(
        @NotBlank @Column(unique = true) String artReference,
        @NotBlank String artName,
        @NotBlank String artDescription,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal artPriceExcludeTaxes,
        @NotNull @Min(1) Long tvaId,
        List<Long> categoryIds
) {
}
