package com.mns.cda.saas_facturation.mapper.responseMapper;

import com.mns.cda.saas_facturation.DTO.responseDTO.CartResponseDTO;
import com.mns.cda.saas_facturation.mapper.CustomerMapper;
import com.mns.cda.saas_facturation.model.Cart;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CartResponseMapper {

    protected final CustomerMapper customerMapper;

    public CartResponseDTO toResponseDTO(Cart cart) {

        return new CartResponseDTO(
                cart.getCrtId(),
                cart.getCrtRef(),
                cart.getCrtCreateDate(),
                cart.getCrtLastModifieDate(),
                cart.getCrtStatus(),
                customerMapper.toDTO(cart.getCustomer())
        );
    }

}
