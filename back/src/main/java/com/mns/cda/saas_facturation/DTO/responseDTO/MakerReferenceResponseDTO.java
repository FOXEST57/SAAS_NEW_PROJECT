package com.mns.cda.saas_facturation.DTO.responseDTO;

import com.mns.cda.saas_facturation.model.MakerReference;

import java.math.BigDecimal;

public record MakerReferenceResponseDTO(
        MakerReference.MakerReferenceId makerReferenceId,
        MakerResponseDTO maker,
        String reference,
        int artMkrStock,
        BigDecimal artMkrSellPrice
) {
}
