package com.mns.cda.saas_facturation.location.DTO;

public record PostalCodeCityDTO(
        PostalCodeDTO postalCode,
        CityDTO city
) {
}