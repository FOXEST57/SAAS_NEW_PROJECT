package com.mns.cda.saas_facturation.cart.DTO.responseDTO;

import com.mns.cda.saas_facturation.enumeration.CartStatus;

import java.util.List;

public record CartResponseDTO(
        Long crtId,
        String crtRef,
        CartStatus crtStatus,
        List<OrderLineResponseDTO> orderLines

){
}
