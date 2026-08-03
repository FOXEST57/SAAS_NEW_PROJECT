package com.mns.cda.saas_facturation.location.DTO;

public record CityDTO(
        Long cityId,
        String cityName,
        CountryDTO country
) {
}
