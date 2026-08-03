package com.mns.cda.saas_facturation.cart.Iservice;

import com.mns.cda.saas_facturation.cart.DTO.CartDTO;
import com.mns.cda.saas_facturation.cart.DTO.OrderLineDTO;
import com.mns.cda.saas_facturation.cart.DTO.requestDTO.OrderLineRequestDTO;
import com.mns.cda.saas_facturation.cart.DTO.updateDTO.UpdateOrderLineDTO;

import java.util.List;

public interface IOrderLineService {


    OrderLineDTO findById(Long articleId, Long cartId);
    List<OrderLineDTO> findByCartId(Long cartId);
    List<CartDTO> findByArtId (Long articleId);
    OrderLineDTO create(OrderLineRequestDTO dto);
    OrderLineDTO update(Long artId, Long crtId, UpdateOrderLineDTO dto);
    void delete(Long articleId, Long cartId);

}
