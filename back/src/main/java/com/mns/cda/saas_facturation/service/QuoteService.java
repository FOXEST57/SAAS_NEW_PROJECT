package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.QuoteDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.QuoteRequestDTO;
import com.mns.cda.saas_facturation.DTO.updateDTO.PatchQuoteLineQuantity;
import com.mns.cda.saas_facturation.Iservice.IQuoteService;
import com.mns.cda.saas_facturation.enumeration.QuoteStatus;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.mapper.QuoteMapper;
import com.mns.cda.saas_facturation.model.Cart;
import com.mns.cda.saas_facturation.model.Quote;
import com.mns.cda.saas_facturation.model.QuoteLine;
import com.mns.cda.saas_facturation.repository.CartRepository;
import com.mns.cda.saas_facturation.repository.QuoteLineRepository;
import com.mns.cda.saas_facturation.repository.QuoteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuoteService implements IQuoteService {

    private final QuoteRepository quoteRepository;
    private final QuoteMapper quoteMapper;
    private final CartRepository cartRepository;
    private final QuoteLineService quoteLineService;
    private final QuoteLineRepository quoteLineRepository;

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

    @Override
    public QuoteDTO create(QuoteRequestDTO quoteRequestDTO) {
        Quote qotParent = quoteRequestDTO.qotParentId() != null
                ? quoteRepository.findById(quoteRequestDTO.qotParentId())
                        .orElseThrow(() -> new ResourceNotFoundException("Devis non existant"))
                : null;
        Cart cart = cartRepository.findById(quoteRequestDTO.cartId()).orElseThrow(() -> new ResourceNotFoundException("Panier non existant"));

        // Création des QuoteLine à partir de cart
        List<QuoteLine> qotLines = cart.getOrderLines()
                .stream()
                .map(quoteLineService::create)
                .toList();

        Quote quote = new Quote();
        quote.setQotNumber(quoteRequestDTO.qotNumber());
        quote.setQotExpirationDate(quoteRequestDTO.qotExpirationDate());
        quote.setQotStatus(quoteRequestDTO.qotStatus());
        quote.setQotParent(qotParent);
        quote.setCart(cart);
        quote.setQotLines(qotLines);

        return quoteMapper.toDTO(quoteRepository.save(quote));
    }

    @Override
    public QuoteDTO updateQuantity(Long qotId, PatchQuoteLineQuantity quantity) {
        Quote quote = quoteRepository.findById(qotId).orElseThrow(() -> new ResourceNotFoundException("Devis non existant"));
        QuoteLine quoteLine = quoteLineRepository.findByArticleRef(quantity.artRef());
        if (quoteLine != null) {
            quoteLineService.patchQuantity(quoteLine.getQotLnId(), quantity);
            return quoteMapper.toDTO(quote);
        }
        throw new ResourceNotFoundException("Ligne de devis non existante");
    }

    @Transactional
    @Override
    public void delete(Long qotId) {
        Quote quote = quoteRepository.findById(qotId).orElseThrow(() -> new ResourceNotFoundException("Devis non existant"));

        quote.getQotLines().forEach(quoteLine -> quoteLineService.delete(quoteLine.getQotLnId()));

        quoteRepository.delete(quote);
    }

    @Override
    public QuoteDTO updateStatus(Long qotId, QuoteStatus qotStatus) {
        Quote quote = quoteRepository.findById(qotId).orElseThrow(() -> new ResourceNotFoundException("Devis non existant"));
        quote.setQotStatus(qotStatus);
        return quoteMapper.toDTO(quoteRepository.save(quote));
    }
}
