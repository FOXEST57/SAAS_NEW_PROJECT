package com.mns.cda.saas_facturation.location.DTO.requestDTO;

import jakarta.validation.constraints.NotBlank;

public record PostalCodeRequestDTO(
        @NotBlank String pCodeName
) {
}
