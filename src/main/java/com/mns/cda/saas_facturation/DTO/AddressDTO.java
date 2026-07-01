package com.mns.cda.saas_facturation.DTO;

import jakarta.validation.constraints.NotNull;

public record AddressDTO(
        String addNumber,
        String addStreet,
        String addComplement,
        PostalCodeDTO postalCode,
        CityDTO city
) {
}
