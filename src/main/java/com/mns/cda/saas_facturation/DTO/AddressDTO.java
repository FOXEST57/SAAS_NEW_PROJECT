package com.mns.cda.saas_facturation.DTO;

public record AddressDTO(
        Long addId,
        String addNumber,
        String addStreet,
        String addComplement,
        PostalCodeDTO postalCode,
        CityDTO city
) {
}
