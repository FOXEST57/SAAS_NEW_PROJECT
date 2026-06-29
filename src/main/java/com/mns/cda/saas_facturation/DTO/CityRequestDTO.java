package com.mns.cda.saas_facturation.DTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

public record CityRequestDTO(
    @NotBlank @Column(unique = true) String cityName,
    @NotBlank Long postalCodeId
) {
}
