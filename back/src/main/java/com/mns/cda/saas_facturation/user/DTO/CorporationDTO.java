package com.mns.cda.saas_facturation.user.DTO;

import com.mns.cda.saas_facturation.location.DTO.AddressDTO;

import java.time.LocalDateTime;

public record CorporationDTO(
        Long corpId,
        String corpName,
        String corpSiret,
        LocalDateTime corpCreationDate,
        String corpPreRefQuote,
        String corpPreRefInvoice,
        String corpTva,
        String corpIban,
        String corpTag,
        AddressDTO address
) {
}
