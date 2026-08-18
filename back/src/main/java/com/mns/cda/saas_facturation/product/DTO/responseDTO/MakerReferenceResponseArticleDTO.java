package com.mns.cda.saas_facturation.product.DTO.responseDTO;

import com.mns.cda.saas_facturation.product.model.MakerReference;

import java.util.List;

public record MakerReferenceResponseArticleDTO(
        MakerReference.MakerReferenceId makerReferenceId,
        MakerResponseDTO maker,
        String artMakerReference,
        List<DeliveryResponseDTO> deliveries
) {
}
