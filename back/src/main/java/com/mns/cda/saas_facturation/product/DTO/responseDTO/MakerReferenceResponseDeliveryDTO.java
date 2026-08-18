package com.mns.cda.saas_facturation.product.DTO.responseDTO;

import com.mns.cda.saas_facturation.product.model.MakerReference;

import java.util.List;

public record MakerReferenceResponseDeliveryDTO(
        MakerReference.MakerReferenceId makerReferenceId,
        ArticleResponseMakerReferenceDTO article,
        MakerResponseDTO maker,
        String artMakerReference
) {
}
