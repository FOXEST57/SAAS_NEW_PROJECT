package com.mns.cda.saas_facturation.cart.mapper;

import com.mns.cda.saas_facturation.cart.DTO.OrderLineDTO;
import com.mns.cda.saas_facturation.cart.mapper.responseMapper.CartResponseMapper;
import com.mns.cda.saas_facturation.cart.model.OrderLine;
import com.mns.cda.saas_facturation.product.mapper.ArticleMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OrderLineMapper {

    private final ArticleMapper articleMapper;
    private final CartResponseMapper cartMapper;

    public OrderLineDTO toDTO(OrderLine orderLine) {
        return new OrderLineDTO(
                orderLine.getOrdLnId(),
                orderLine.getOrdLnQuantity(),
                articleMapper.toLightDTO(orderLine.getArticle()),
                cartMapper.toResponseDTO(orderLine.getCart())

        );
    }
}
