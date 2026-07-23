package com.mns.cda.saas_facturation.DTO;

public record CityDTO(
        Long cityId,
        String cityName,
        CountryDTO country
) {
}
