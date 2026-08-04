package com.mns.cda.saas_facturation.cart.DTO;

import com.mns.cda.saas_facturation.enumeration.CommandStatus;

import java.time.LocalDateTime;
import java.util.List;

public record CommandDTO(
        Long cmfId,
        LocalDateTime cmdCreatedDate,
        LocalDateTime cmdModifiedDate,
        CommandStatus cmdStatus,
        String quoteNumber,
        List<QuoteLineDTO> quoteLines
) {
}
