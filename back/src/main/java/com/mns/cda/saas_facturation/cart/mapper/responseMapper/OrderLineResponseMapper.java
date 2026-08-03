package com.mns.cda.saas_facturation.cart.mapper.responseMapper;

import com.mns.cda.saas_facturation.cart.DTO.responseDTO.OrderLineResponseDTO;
import com.mns.cda.saas_facturation.cart.model.OrderLine;
import org.springframework.stereotype.Service;

@Service
public class OrderLineResponseMapper {

    public OrderLineResponseDTO toResponseDTO(OrderLine orderLine) {
        return new OrderLineResponseDTO(
                orderLine.getOrdLnId(),
                orderLine.getOrdLnQuantity()
        );
    }
}
