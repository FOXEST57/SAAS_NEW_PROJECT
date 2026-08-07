package com.mns.cda.saas_facturation.user.DTO.updateDTO;

import com.mns.cda.saas_facturation.validation.ValidPhoneNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CustomerUpdateDTO(
        @NotBlank String ctmFirstName,
        @NotBlank String ctmLastName,
        @NotBlank @Email String ctmEmail,
        @NotBlank @ValidPhoneNumber String ctmPhone,
        @NotNull Long addId,
        @NotBlank String password
) {
}
