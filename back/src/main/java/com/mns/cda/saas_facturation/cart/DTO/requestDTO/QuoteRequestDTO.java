package com.mns.cda.saas_facturation.cart.DTO.requestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;


public record QuoteRequestDTO(
        @NotNull LocalDate qotExpirationDate,
        Long qotParentId,
        @NotNull Long cartId
) {
}
