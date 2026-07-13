package com.mns.cda.saas_facturation.DTO.requestDTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record CityRequestDTO(
        @NotBlank @Column(unique = true) String cityName,
        @NotNull Long cntId
) {
}
