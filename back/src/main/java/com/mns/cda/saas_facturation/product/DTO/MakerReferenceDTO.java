package com.mns.cda.saas_facturation.product.DTO;

import com.mns.cda.saas_facturation.product.DTO.responseDTO.ArticleResponseMakerReferenceDTO;
import com.mns.cda.saas_facturation.product.DTO.responseDTO.MakerResponseDTO;

import java.math.BigDecimal;

public record MakerReferenceDTO(
        ArticleResponseMakerReferenceDTO article,
        MakerResponseDTO maker,
        String reference,
        int artMkrStock,
        BigDecimal artMkrSellPrice
) {
}
