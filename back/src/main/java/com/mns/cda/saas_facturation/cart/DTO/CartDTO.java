package com.mns.cda.saas_facturation.cart.DTO;

import com.mns.cda.saas_facturation.cart.DTO.responseDTO.OrderLineResponseDTO;
import com.mns.cda.saas_facturation.enumeration.CartStatus;
import com.mns.cda.saas_facturation.user.DTO.CustomerDTO;

import java.time.LocalDateTime;
import java.util.List;

public record CartDTO (
        Long crtId,
        String crtRef,
        LocalDateTime crtCreateDate,
        LocalDateTime crtLastModifieDate,
        CartStatus crtStatus,
        CustomerDTO customer,
        List<OrderLineResponseDTO> orderLines
){
}
