package com.mns.cda.saas_facturation.mapper.responseMapper;

import com.mns.cda.saas_facturation.DTO.responseDTO.CartResponseDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.OrderLineResponseDTO;
import com.mns.cda.saas_facturation.mapper.CustomerMapper;
import com.mns.cda.saas_facturation.model.Cart;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CartResponseMapper {

    protected final OrderLineResponseMapper orderLineResponseMapper;

    public CartResponseDTO toResponseDTO(Cart cart) {

        List<OrderLineResponseDTO> orderLines = cart.getOrderLines() != null ?
                cart.getOrderLines()
                        .stream()
                        .map(orderLineResponseMapper::toResponseDTO)
                        .toList() :
                List.of();

        return new CartResponseDTO(
                cart.getCrtId(),
                cart.getCrtRef(),
                cart.getCrtStatus(),
                orderLines

        );
    }

}
