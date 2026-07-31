package com.mns.cda.saas_facturation.DTO.requestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record QuoteRequestDTO(
        @NotBlank String qotNumber,
        @NotNull LocalDate expirationDate,
        @NotBlank String qotStatus,
        Long qotParentId,
        @NotNull Long cartId
) {
}
