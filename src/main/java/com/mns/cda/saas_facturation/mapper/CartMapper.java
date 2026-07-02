package com.mns.cda.saas_facturation.mapper;

import com.mns.cda.saas_facturation.DTO.CartDTO;
import com.mns.cda.saas_facturation.model.Cart;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CartMapper {

    public CartDTO toDTO(Cart cart) {

       return new CartDTO(
        cart.getCrtId(),
        cart.getCrtRef(),
        cart.getCrtStatus()
       );

    }
}
