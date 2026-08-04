package com.mns.cda.saas_facturation.cart.DTO.requestDTO;

import jakarta.validation.constraints.NotNull;

public record CommandRequestDTO(
        @NotNull Long qotId
) {
}
