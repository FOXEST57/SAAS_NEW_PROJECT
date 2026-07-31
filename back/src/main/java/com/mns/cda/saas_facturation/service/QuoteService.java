package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.QuoteDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.QuoteRequestDTO;
import com.mns.cda.saas_facturation.Iservice.IQuoteService;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.mapper.QuoteMapper;
import com.mns.cda.saas_facturation.model.Cart;
import com.mns.cda.saas_facturation.model.Quote;
import com.mns.cda.saas_facturation.repository.CartRepository;
import com.mns.cda.saas_facturation.repository.QuoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuoteService implements IQuoteService {

    private QuoteRepository quoteRepository;
    private QuoteMapper quoteMapper;
    private CartRepository cartRepository;

    @Override
    public List<QuoteDTO> findAll() {
        return quoteRepository.findAll()
                .stream()
                .map(quoteMapper::toDTO)
                .toList();
    }

    @Override
    public QuoteDTO findById(Long qotId) {
        Quote quote = quoteRepository.findById(qotId).orElseThrow(() -> new ResourceNotFoundException("Devis non existant"));

        return quoteMapper.toDTO(quote);
    }

    public QuoteDTO create(QuoteRequestDTO quoteRequestRequestDTO) {
        Cart cart = cartRepository.findById(quoteRequestRequestDTO.cartId()).orElseThrow(() -> new ResourceNotFoundException("Panier non existant"));
        return null;
    }

}
