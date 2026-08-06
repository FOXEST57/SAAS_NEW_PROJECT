package com.mns.cda.saas_facturation.user.DTO.requestDTO;

import com.mns.cda.saas_facturation.enumeration.AccountTypeEnum;
import jakarta.validation.constraints.NotBlank;

public record AccountTypeRequestDTO(
        @NotBlank AccountTypeEnum accTypeLibelle
) {
}
