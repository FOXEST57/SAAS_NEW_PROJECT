package com.mns.cda.saas_facturation.cart.DTO.requestDTO;

import com.mns.cda.saas_facturation.cart.model.QuoteLine;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CartRequestRevisitedDTO(
        @NotBlank @Column(unique = true,nullable = false) String crtRef,
        @NotNull Long creatorId,
        List<QuoteLine> quoteLines,
        @NotBlank @Email String receiverEmail,
        Long parentQuoteId

){


}
