package com.mns.cda.saas_facturation.cart.mapper;

import com.mns.cda.saas_facturation.cart.DTO.CartDTO;
import com.mns.cda.saas_facturation.cart.DTO.responseDTO.OrderLineResponseDTO;
import com.mns.cda.saas_facturation.cart.mapper.responseMapper.OrderLineResponseMapper;
import com.mns.cda.saas_facturation.cart.model.Cart;
import com.mns.cda.saas_facturation.user.mapper.CustomerMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CartMapper {

    protected final CustomerMapper customerMapper;
    protected final OrderLineResponseMapper orderLineResponseMapper;

    public CartDTO toDTO(Cart cart) {

        List<OrderLineResponseDTO> orderLines = cart.getOrderLines() != null ?
                cart.getOrderLines()
                .stream()
                .map(orderLineResponseMapper::toResponseDTO)
                .toList() :
                List.of();

       return new CartDTO(
               cart.getCrtId(),
               cart.getCrtRef(),
               cart.getCrtCreateDate(),
               cart.getCrtLastModifieDate(),
               cart.getCrtStatus(),
               customerMapper.toDTO(cart.getCreator()),
               cart.getReceiverEmail(),
               orderLines
       );

    }
}
