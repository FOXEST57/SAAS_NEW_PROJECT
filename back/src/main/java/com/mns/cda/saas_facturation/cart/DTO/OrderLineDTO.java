package com.mns.cda.saas_facturation.cart.DTO;

import com.mns.cda.saas_facturation.cart.DTO.responseDTO.CartResponseDTO;
import com.mns.cda.saas_facturation.product.DTO.ArticleLightDTO;
import com.mns.cda.saas_facturation.cart.model.OrderLine;

public record OrderLineDTO(
        OrderLine.OrderLineId id,
        Integer quantity,
        ArticleLightDTO article,
        CartResponseDTO cart
) {
}
