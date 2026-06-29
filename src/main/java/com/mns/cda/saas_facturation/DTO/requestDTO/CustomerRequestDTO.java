package com.mns.cda.saas_facturation.DTO.requestDTO;

import com.mns.cda.saas_facturation.validation.ValidPhoneNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomerRequestDTO(
        @NotBlank String ctmFirstName,
        @NotBlank String ctmLastName,
        @NotBlank @Email String ctmEmail,
        @NotBlank @ValidPhoneNumber String ctmPhone,
        @NotBlank String ctmAddress,
        @NotBlank Long cityId
) {
}
