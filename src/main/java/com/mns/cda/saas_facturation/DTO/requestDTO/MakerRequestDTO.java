package com.mns.cda.saas_facturation.DTO.requestDTO;

import com.mns.cda.saas_facturation.validation.ValidPhoneNumber;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MakerRequestDTO (
        @NotBlank @Column(unique = true) String mkrName,
        @NotBlank @Email String mkrEmail,
        @NotBlank @ValidPhoneNumber String mkrPhone,
        @NotNull Long addressId
        ) {
}
