package com.mns.cda.saas_facturation.DTO.requestDTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CityRequestDTO(
        @NotBlank @Column(unique = true) String cityName,
        @NotBlank List<Long> postalCodeIds
) {
}
