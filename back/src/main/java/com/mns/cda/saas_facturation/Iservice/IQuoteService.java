package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.QuoteDTO;

import java.util.List;

public interface IQuoteService {
    List<QuoteDTO> findAll();

    QuoteDTO findById(Long qotId);
}
