package com.mns.cda.saas_facturation.cart.Iservice;

import com.mns.cda.saas_facturation.cart.DTO.QuoteLineDTO;
import com.mns.cda.saas_facturation.cart.DTO.updateDTO.PatchQuoteLineQuantity;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.cart.model.OrderLine;
import com.mns.cda.saas_facturation.cart.model.QuoteLine;

import java.util.List;
import java.util.Optional;

public interface IQuoteLineService {
    List<QuoteLineDTO> findAll();

    Optional<QuoteLineDTO> findById(Long cntId);

//    QuoteLine create(OrderLine orderLine);

    // Construit la ligne figée sans la persister : le devis s'en charge par cascade.
    QuoteLine build(OrderLine orderLine);

    QuoteLine patchQuantity (Long quoteLineId, PatchQuoteLineQuantity dto) throws ResourceNotFoundException;

    void delete(Long quotLineId) throws ResourceNotFoundException;
}
