package com.mns.cda.saas_facturation.DTO;

import java.util.List;

public record CityDTO(
        String cityName,
        List<PostalCodeDTO> postalCodes
) {
}
