package com.mns.cda.saas_facturation.user.DTO.requestDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LogInDTO(
        @NotBlank
        @Email
        String ctmEmail,
        @NotBlank
        String password
) {
}
