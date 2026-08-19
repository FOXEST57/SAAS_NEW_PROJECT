package com.mns.cda.saas_facturation.product.DTO.requestDTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record MakerReferenceRequestDTO(
        @NotNull Long artId,
        @NotNull Long mkrId,
        @NotBlank @Column(unique = true) String artMkrReference,
        @NotNull List<Long> deliveryIds
        ) {
}
