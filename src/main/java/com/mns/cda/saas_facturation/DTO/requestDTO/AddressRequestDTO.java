package com.mns.cda.saas_facturation.DTO.requestDTO;

import jakarta.validation.constraints.NotNull;

public record AddressRequestDTO(
        String addNumber,
        String addStreet,
        String addComplement,
        @NotNull Long pcodeId,
        @NotNull Long cityId
) {
}
