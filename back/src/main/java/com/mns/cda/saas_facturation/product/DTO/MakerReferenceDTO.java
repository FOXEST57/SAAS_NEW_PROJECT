package com.mns.cda.saas_facturation.product.DTO;

import com.mns.cda.saas_facturation.product.DTO.responseDTO.ArticleResponseMakerReferenceDTO;
import com.mns.cda.saas_facturation.product.DTO.responseDTO.DeliveryResponseDTO;
import com.mns.cda.saas_facturation.product.DTO.responseDTO.MakerResponseDTO;

import java.util.List;

public record MakerReferenceDTO(
        ArticleResponseMakerReferenceDTO article,
        MakerResponseDTO maker,
        String artMakerReference,
        List<DeliveryResponseDTO> deliveries
) {
}
