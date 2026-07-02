package com.mns.cda.saas_facturation.DTO.responseDTO;

import com.mns.cda.saas_facturation.model.OrderLine;

public record OrderLineResponseDTO (
        OrderLine.OrderLineId id,
        Integer quantity
) {
}
