package com.mns.cda.saas_facturation.cart.DTO;

import com.mns.cda.saas_facturation.enumeration.QuoteStatus;
import com.mns.cda.saas_facturation.user.DTO.CustomerDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record QuoteDTO(
        Long quoteId,
        String qotNumber,
        LocalDateTime qotCreatedDate,
        LocalDate expirationDate,
        QuoteStatus qotStatus,
        QuoteDTO qotParent,
        Long cartId,
        List<QuoteLineDTO> qotLines,
        Long creatorId,
        String customerEmail
) {
}
