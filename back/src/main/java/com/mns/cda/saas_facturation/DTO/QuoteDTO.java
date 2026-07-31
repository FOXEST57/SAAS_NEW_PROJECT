package com.mns.cda.saas_facturation.DTO;

import com.mns.cda.saas_facturation.model.QuoteLine;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record QuoteDTO(
        String qotNumber,
        LocalDateTime qotCreatedDate,
        LocalDate expirationDate,
        String qotStatus,
        QuoteDTO qotParent,
        CartDTO cart,
        List<QuoteLine> qotLines
) {
}
