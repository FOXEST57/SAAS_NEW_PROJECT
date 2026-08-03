package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.QuoteDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.QuoteRequestDTO;
import com.mns.cda.saas_facturation.DTO.updateDTO.PatchQuoteLineQuantity;
import com.mns.cda.saas_facturation.enumeration.QuoteStatus;

import java.util.List;

public interface IQuoteService {
    List<QuoteDTO> findAll();

    QuoteDTO findById(Long qotId);

    QuoteDTO create(QuoteRequestDTO quoteRequestDTO);

    QuoteDTO updateQuantity(Long qotId, PatchQuoteLineQuantity quantity, String artRef);

    void delete(Long qotId);

    QuoteDTO updateStatus(Long qotId, QuoteStatus qotStatus);
}
