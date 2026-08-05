package com.mns.cda.saas_facturation.cart.service;

import com.mns.cda.saas_facturation.cart.DTO.QuoteDTO;
import com.mns.cda.saas_facturation.cart.DTO.requestDTO.CommandRequestDTO;
import com.mns.cda.saas_facturation.cart.DTO.requestDTO.QuoteRequestDTO;
import com.mns.cda.saas_facturation.cart.DTO.updateDTO.PatchQuoteLineQuantity;
import com.mns.cda.saas_facturation.cart.Iservice.IQuoteService;
import com.mns.cda.saas_facturation.cart.mapper.CartPipelineMapper;
import com.mns.cda.saas_facturation.cart.service.pipeline.CartValidatedEvent;
import com.mns.cda.saas_facturation.cart.service.pipeline.QuoteAcceptedEvent;
import com.mns.cda.saas_facturation.cart.service.pipeline.QuoteRevisitedEvent;
import com.mns.cda.saas_facturation.enumeration.QuoteStatus;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.cart.mapper.QuoteMapper;
import com.mns.cda.saas_facturation.cart.model.Cart;
import com.mns.cda.saas_facturation.cart.model.Quote;
import com.mns.cda.saas_facturation.cart.model.QuoteLine;
import com.mns.cda.saas_facturation.cart.repository.CartRepository;
import com.mns.cda.saas_facturation.cart.repository.QuoteLineRepository;
import com.mns.cda.saas_facturation.cart.repository.QuoteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuoteService implements IQuoteService {

    private final QuoteRepository quoteRepository;
    private final QuoteMapper quoteMapper;
    private final CartRepository cartRepository;
    private final QuoteLineService quoteLineService;

    private final ApplicationEventPublisher publisher;


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
    @Transactional
    public QuoteDTO create(QuoteRequestDTO quoteRequestDTO) {
        Quote qotParent = quoteRequestDTO.qotParentId() != null
                ? quoteRepository.findById(quoteRequestDTO.qotParentId())
                        .orElseThrow(() -> new ResourceNotFoundException("Devis non existant"))
                : null;
        Cart cart = cartRepository.findById(quoteRequestDTO.cartId()).orElseThrow(() -> new ResourceNotFoundException("Panier non existant"));

        Quote quote = new Quote();
        quote.setQotNumber(quoteRequestDTO.qotNumber());
        quote.setQotExpirationDate(quoteRequestDTO.qotExpirationDate());
        quote.setQotStatus(QuoteStatus.CREATED);
        quote.setQotParent(qotParent);
        quote.setCart(cart);
        // Création des QuoteLine à partir de cart
        cart.getOrderLines().forEach(ol -> {
            QuoteLine line = quoteLineService.build(ol);   // sans save()
            line.setQuote(quote);
            quote.getQotLines().add(line);
        });

        return quoteMapper.toDTO(quoteRepository.save(quote));
    }

    @Override
    @Transactional
    public QuoteDTO updateQuantity(Long qotId, PatchQuoteLineQuantity quantity) {
        Quote quote = quoteRepository.findById(qotId).orElseThrow(() -> new ResourceNotFoundException("Devis non existant"));

        EnumSet<QuoteStatus> modifiableStatuses = EnumSet.of(
                QuoteStatus.PENDING,
                QuoteStatus.CREATED,
                QuoteStatus.REJECTED
        );

        if (!modifiableStatuses.contains(quote.getQotStatus())) {
            throw new IllegalStateException("Le devis n'est pas dans un état modifiable");
        }

        QuoteLine quoteLine = quote.getQotLines().stream()
                .filter(line -> line.getArticleRef().equalsIgnoreCase(quantity.artRef()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ligne de devis non existante pour la référence " + quantity.artRef()));

        quoteLineService.patchQuantity(quoteLine.getQotLnId(), quantity);
        return quoteMapper.toDTO(quoteRepository.findById(qotId).orElseThrow());
    }

    @Transactional
    @Override
    public void delete(Long qotId) {
        Quote quote = quoteRepository.findById(qotId).orElseThrow(() -> new ResourceNotFoundException("Devis non existant"));

        quoteRepository.delete(quote);
    }

    @Override
    @Transactional
    public QuoteDTO updateStatus(Long qotId, QuoteStatus qotStatus) {
        Quote quote = quoteRepository.findById(qotId).orElseThrow(() -> new ResourceNotFoundException("Devis non existant"));
        quote.setQotStatus(qotStatus);
        if(quote.getQotStatus() == QuoteStatus.REVISITED) {
            publisher.publishEvent(new QuoteRevisitedEvent(quote));

        } else if (quote.getQotStatus() == QuoteStatus.ACCEPTED) {
            publisher.publishEvent(new QuoteAcceptedEvent(quote));

        }
        return quoteMapper.toDTO(quoteRepository.save(quote));
    }
}
