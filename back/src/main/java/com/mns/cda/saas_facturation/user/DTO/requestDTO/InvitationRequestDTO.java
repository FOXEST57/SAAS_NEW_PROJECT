package com.mns.cda.saas_facturation.user.DTO.requestDTO;

import com.mns.cda.saas_facturation.enumeration.InvitationTypeEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record InvitationRequestDTO(

        @NotBlank
        @Email
        String email,

        InvitationTypeEnum type

) {}
