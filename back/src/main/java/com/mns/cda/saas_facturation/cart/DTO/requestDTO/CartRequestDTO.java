package com.mns.cda.saas_facturation.cart.DTO.requestDTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CartRequestDTO (
        @NotBlank @Column(unique = true,nullable = false) String crtRef,
        @NotBlank @Column(nullable = false) String crtStatus,
        @NotNull Long ctmId,
        List<OrderLineRequestDTO> orderLines

){


}
