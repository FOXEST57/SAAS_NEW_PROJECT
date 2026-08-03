package com.mns.cda.saas_facturation.cart.DTO.responseDTO;

import com.mns.cda.saas_facturation.cart.model.OrderLine;

public record OrderLineResponseDTO (
        OrderLine.OrderLineId id,
        Integer quantity
) {
}
