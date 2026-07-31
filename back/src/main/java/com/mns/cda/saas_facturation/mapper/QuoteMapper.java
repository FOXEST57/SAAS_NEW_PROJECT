package com.mns.cda.saas_facturation.mapper;

import com.mns.cda.saas_facturation.DTO.QuoteDTO;
import com.mns.cda.saas_facturation.model.Quote;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class QuoteMapper {

    private final CartMapper cartMapper;
    private final QuoteLineMapper quoteLineMapper;

    public QuoteDTO toDTO(Quote quote) {
        return new QuoteDTO(
                quote.getQotNumber(),
                quote.getQotCreatedDate(),
                quote.getQotExpirationDate(),
                quote.getQotStatus(),
                quote.getQotParent() != null ? this.toDTO(quote.getQotParent()) : null,
                cartMapper.toDTO(quote.getCart()),
                quote.getQotLines().stream().map(quoteLineMapper::toDTO).toList()
        );
    }
}
