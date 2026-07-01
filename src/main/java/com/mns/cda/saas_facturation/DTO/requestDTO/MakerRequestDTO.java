package com.mns.cda.saas_facturation.DTO.requestDTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

public record MakerRequestDTO (
        @NotBlank @Column(unique = true) String mkrName
        ) {
}
