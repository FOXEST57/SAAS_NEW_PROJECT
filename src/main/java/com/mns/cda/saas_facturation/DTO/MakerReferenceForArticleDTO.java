package com.mns.cda.saas_facturation.DTO;

import com.mns.cda.saas_facturation.DTO.responseDTO.MakerResponseDTO;

public record MakerReferenceForArticleDTO(
        MakerResponseDTO maker,
        String reference
){
}
