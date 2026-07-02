package com.mns.cda.saas_facturation.DTO.requestDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderLineRequestDTO(
        @NotNull Long crtId,
        @NotNull Long artId,
        @NotNull @Min(1) Long quantity
) {
}
