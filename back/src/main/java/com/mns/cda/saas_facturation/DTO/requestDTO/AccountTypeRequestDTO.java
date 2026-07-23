package com.mns.cda.saas_facturation.DTO.requestDTO;

import jakarta.validation.constraints.NotBlank;

public record AccountTypeRequestDTO(
        @NotBlank String accTypeLibelle
) {
}
