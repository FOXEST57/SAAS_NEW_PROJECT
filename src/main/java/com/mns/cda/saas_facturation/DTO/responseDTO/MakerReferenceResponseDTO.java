package com.mns.cda.saas_facturation.DTO.responseDTO;

public record MakerReferenceResponseDTO(
        ArticleResponseMakerReferenceDTO article,
        MakerResponseDTO maker,
        String reference
) {
}
