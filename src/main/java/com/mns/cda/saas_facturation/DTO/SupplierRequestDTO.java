package com.mns.cda.saas_facturation.DTO;

import com.mns.cda.saas_facturation.validation.ValidPhoneNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SupplierRequestDTO (
    @NotBlank String name,
    @NotBlank @Email String email,
    @NotBlank @ValidPhoneNumber String phoneNumber,
    @NotBlank String address
    )
{}
