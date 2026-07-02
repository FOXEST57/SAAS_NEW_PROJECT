package com.mns.cda.saas_facturation.mapper;

import com.mns.cda.saas_facturation.DTO.OrderLineDTO;
import com.mns.cda.saas_facturation.model.OrderLine;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OrderLineMapper {

    private final ArticleMapper articleMapper;

    public OrderLineDTO toDTO(OrderLine orderLine) {
        return new OrderLineDTO(
                orderLine.getOrdLnId(),
                orderLine.getOrdLnQuantity(),
                articleMapper.toLightDTO(orderLine.getArticle())
        );
    }
}
