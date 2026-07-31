package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.QuoteLineDTO;
import com.mns.cda.saas_facturation.DTO.updateDTO.PatchQuoteLineQuantity;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.model.OrderLine;
import com.mns.cda.saas_facturation.model.QuoteLine;

import java.util.List;
import java.util.Optional;

public interface IQuoteLineService {
    List<QuoteLineDTO> findAll();

    Optional<QuoteLineDTO> findById(Long cntId);

    QuoteLine create(OrderLine orderLine);

    QuoteLine patchQuantity (Long quoteLineId, PatchQuoteLineQuantity dto) throws ResourceNotFoundException;

    void delete(Long quotLineId) throws ResourceNotFoundException;
}
