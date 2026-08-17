package com.mns.cda.saas_facturation.cart.service.pipeline;

import com.mns.cda.saas_facturation.cart.DTO.requestDTO.CommandRequestDTO;
import com.mns.cda.saas_facturation.cart.DTO.requestDTO.InvoiceRequestDTO;
import com.mns.cda.saas_facturation.cart.DTO.requestDTO.QuoteRequestDTO;
import com.mns.cda.saas_facturation.cart.mapper.CartPipelineMapper;
import com.mns.cda.saas_facturation.cart.service.CartService;
import com.mns.cda.saas_facturation.cart.service.CommandService;
import com.mns.cda.saas_facturation.cart.service.InvoiceService;
import com.mns.cda.saas_facturation.cart.service.QuoteService;
import com.mns.cda.saas_facturation.security.AppUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Transactional
public class PipelineOrchestrator {

    private final CartPipelineMapper mapper;
    private final QuoteService quoteService;
    private final CommandService commandService;
    private final CartService cartService;
    private final InvoiceService invoiceService;

    @EventListener
    public void onCartValidated(CartValidatedEvent event) {
        QuoteRequestDTO dto = mapper.cartToQuote(event.getCart());
        quoteService.create(null, dto);
    }

    @EventListener
    public void onQuoteAccepted(QuoteAcceptedEvent event) {
        CommandRequestDTO dto = mapper.quoteToCommand(event.getQuote());
        commandService.create(dto);
    }

    @EventListener
    public void onQuoteRevisited(QuoteRevisitedEvent event) {
        cartService.quoteToRevisitedCart(event.getQuote().getQotId());
    }

    @EventListener
    public void onCommandAccepted(CommandAcceptedEvent event) {
        InvoiceRequestDTO dto = mapper.commandToInvoice(event.getCommand());
        invoiceService.create(dto);
    }
}
