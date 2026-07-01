package com.mns.cda.saas_facturation.DTO.requestDTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CityRequestDTO(
        @NotBlank @Column(unique = true) String cityName,
        @NotNull Long cntId
) {
}
