package com.mns.cda.saas_facturation.DTO.requestDTO;

import com.mns.cda.saas_facturation.validation.ValidPhoneNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CustomerRequestDTO(
        @NotBlank String ctmFirstName,
        @NotBlank String ctmLastName,
        @NotBlank @Email String ctmEmail,
        @NotBlank @ValidPhoneNumber String ctmPhone,
        @NotNull Long addId,
        @NotNull Long accTypeId,
        String password,
        List<Long> customerIds
) {
}
