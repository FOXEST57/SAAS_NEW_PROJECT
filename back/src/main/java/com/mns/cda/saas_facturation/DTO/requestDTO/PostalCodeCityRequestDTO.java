package com.mns.cda.saas_facturation.DTO.requestDTO;

import jakarta.validation.constraints.NotNull;

public record PostalCodeCityRequestDTO(
        @NotNull Long pCodeId,
        @NotNull Long cityId
) {
}
