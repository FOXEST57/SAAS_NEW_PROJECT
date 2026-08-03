package com.mns.cda.saas_facturation.location.DTO.requestDTO;

import jakarta.validation.constraints.NotNull;

public record AddressRequestDTO(
        String addNumber,
        String addStreet,
        String addComplement,
        @NotNull Long pCodeId,
        @NotNull Long cityId
) {
}
