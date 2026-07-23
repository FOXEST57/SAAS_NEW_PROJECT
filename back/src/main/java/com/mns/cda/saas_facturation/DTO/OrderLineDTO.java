package com.mns.cda.saas_facturation.DTO;

import com.mns.cda.saas_facturation.DTO.responseDTO.CartResponseDTO;
import com.mns.cda.saas_facturation.model.OrderLine;

public record OrderLineDTO(
        OrderLine.OrderLineId id,
        Integer quantity,
        ArticleLightDTO article,
        CartResponseDTO cart
) {
}
