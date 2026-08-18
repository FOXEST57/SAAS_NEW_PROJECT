package com.mns.cda.saas_facturation.cart.DTO.requestDTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CartRequestDTO (
        List<OrderLineRequestDTO> orderLines,
        @NotBlank @Email String receiverEmail,
        Long parentQuoteId

){


}
