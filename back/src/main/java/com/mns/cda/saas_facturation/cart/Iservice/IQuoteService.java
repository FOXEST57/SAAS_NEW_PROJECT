package com.mns.cda.saas_facturation.cart.Iservice;

import com.mns.cda.saas_facturation.cart.DTO.QuoteDTO;
import com.mns.cda.saas_facturation.cart.DTO.requestDTO.QuoteRequestDTO;
import com.mns.cda.saas_facturation.cart.DTO.updateDTO.PatchQuoteLineQuantity;
import com.mns.cda.saas_facturation.enumeration.QuoteStatus;

import java.util.List;

public interface IQuoteService {
    List<QuoteDTO> findAll();

    QuoteDTO findById(Long qotId);

    QuoteDTO create(QuoteRequestDTO quoteRequestDTO);

    QuoteDTO updateQuantity(Long qotId, PatchQuoteLineQuantity quantity);

    void delete(Long qotId);

    QuoteDTO updateStatus(Long qotId, QuoteStatus qotStatus);
}
