package com.mns.cda.saas_facturation.cart.service.pipeline;

import com.mns.cda.saas_facturation.cart.model.Cart;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartValidatedEvent {
    private final Cart cart;
}
