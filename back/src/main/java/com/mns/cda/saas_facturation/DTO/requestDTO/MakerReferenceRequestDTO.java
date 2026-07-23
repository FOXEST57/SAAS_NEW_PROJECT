package com.mns.cda.saas_facturation.DTO.requestDTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record MakerReferenceRequestDTO(
        @NotNull Long artId,
        @NotNull Long mkrId,
        @NotBlank @Column(unique = true) String artMkrReference,
        int artMkrStock,
        @NotNull @DecimalMin(value = "0.00") BigDecimal artMkrSellPrice

        ) {
}
