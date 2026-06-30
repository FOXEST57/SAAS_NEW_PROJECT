package com.mns.cda.saas_facturation.DTO.responseDTO;

public record MakerReferenceResponseDTO(
        ArticleResponseMakerReferenceDTO articleResponseMakerReferenceDTO,
        MakerResponseDTO makerResponseDTO,
        String mkrRefReference,
        Long mkrRefStock

) {
}
