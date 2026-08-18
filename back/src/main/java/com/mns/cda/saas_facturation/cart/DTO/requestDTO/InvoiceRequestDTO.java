package com.mns.cda.saas_facturation.cart.DTO.requestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InvoiceRequestDTO(
        @NotNull Long commandId
) {
}
