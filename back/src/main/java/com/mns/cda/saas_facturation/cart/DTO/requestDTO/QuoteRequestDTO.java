package com.mns.cda.saas_facturation.cart.DTO.requestDTO;

import com.mns.cda.saas_facturation.enumeration.QuoteStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record QuoteRequestDTO(
        @NotBlank String qotNumber,
        @NotNull LocalDate qotExpirationDate,
        Long qotParentId,
        @NotNull Long cartId
) {
}
