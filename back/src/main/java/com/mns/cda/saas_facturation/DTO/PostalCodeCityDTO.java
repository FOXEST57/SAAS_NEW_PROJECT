package com.mns.cda.saas_facturation.DTO;

public record PostalCodeCityDTO(
        PostalCodeDTO postalCode,
        CityDTO city
) {
}