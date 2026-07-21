package com.mns.cda.saas_facturation.DTO;

import com.mns.cda.saas_facturation.DTO.responseDTO.ArticleResponseMakerReferenceDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.MakerResponseDTO;

import java.math.BigDecimal;

public record MakerReferenceDTO(
        ArticleResponseMakerReferenceDTO article,
        MakerResponseDTO maker,
        String reference,
        int artMkrStock,
        BigDecimal artMkrSellPrice
) {
}
