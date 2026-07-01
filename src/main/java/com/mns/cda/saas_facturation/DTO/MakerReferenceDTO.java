package com.mns.cda.saas_facturation.DTO;

import com.mns.cda.saas_facturation.DTO.responseDTO.ArticleResponseMakerReferenceDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.MakerResponseDTO;

public record MakerReferenceDTO(
        ArticleResponseMakerReferenceDTO articleResponseMakerReferenceDTO,
        MakerResponseDTO makerResponseDTO,
        String mkrRefReference
) {
}
