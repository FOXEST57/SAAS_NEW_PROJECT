package com.mns.cda.saas_facturation.DTO;

import com.mns.cda.saas_facturation.enumeration.QuoteStatus;

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
        BigDecimal totalHT,
        BigDecimal totalTva,
        BigDecimal totalTTC
) {
}
