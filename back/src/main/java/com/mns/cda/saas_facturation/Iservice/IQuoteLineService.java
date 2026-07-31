package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.QuoteLineDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.QuoteLineRequestDTO;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

public interface IQuoteLineService {
    List<QuoteLineDTO> findAll();

    Optional<QuoteLineDTO> findById(Long cntId);

    QuoteLineDTO create(QuoteLineRequestDTO dto);

    QuoteLineDTO update(Long cntId, QuoteLineRequestDTO dto) throws ResourceNotFoundException;

    void delete(Long cntId) throws ResourceNotFoundException;
}
