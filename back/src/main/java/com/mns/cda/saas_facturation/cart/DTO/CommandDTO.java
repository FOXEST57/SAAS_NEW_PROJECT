package com.mns.cda.saas_facturation.cart.DTO;

import com.mns.cda.saas_facturation.enumeration.CommandStatus;

import java.time.LocalDateTime;
import java.util.List;

public record CommandDTO(
        Long cmdId,
        LocalDateTime cmdCreatedDate,
        LocalDateTime cmdModifiedDate,
        CommandStatus cmdStatus,
        Long quoteId,
        String quoteNumber,
        String cmdReference,
        List<QuoteLineDTO> quoteLines,
        Long creatorId,
        String customerEmail
) {
}
