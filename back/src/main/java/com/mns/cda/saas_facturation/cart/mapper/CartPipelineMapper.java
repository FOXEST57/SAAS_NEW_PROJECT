package com.mns.cda.saas_facturation.cart.mapper;

import com.mns.cda.saas_facturation.cart.DTO.requestDTO.CartRequestDTO;
import com.mns.cda.saas_facturation.cart.DTO.requestDTO.CommandRequestDTO;
import com.mns.cda.saas_facturation.cart.DTO.requestDTO.InvoiceRequestDTO;
import com.mns.cda.saas_facturation.cart.DTO.requestDTO.QuoteRequestDTO;
import com.mns.cda.saas_facturation.cart.model.Cart;
import com.mns.cda.saas_facturation.cart.model.Command;
import com.mns.cda.saas_facturation.cart.model.Quote;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


@Service
@AllArgsConstructor
public class CartPipelineMapper {

    public QuoteRequestDTO cartToQuote(Cart cart) {
        LocalDate expirationDate = cart.getCrtCreateDate().plusMonths(1).toLocalDate();
        return new QuoteRequestDTO(
                cart.getCrtRef(),// quoteNumber simuler depuis cartRéférence en attendant d'avoir un générateur automatique.
                expirationDate,
                cart.getParentQuoteId(),
                cart.getCrtId()
        );
    }

    public CartRequestDTO quoteToCartRevision(Quote quote) {
        return new CartRequestDTO(
                quote.getQotNumber() + "-R", // cartRef simuler depuis quoteNumber en attendant d'avoir un générateur automatique.
                quote.getCart().getCreator().getCtmId(),
                null,
                quote.getReceiverEmail(),
                quote.getQotId()
        );
    }

    public CommandRequestDTO quoteToCommand(Quote quote) {
        return new CommandRequestDTO(
                quote.getQotId()
        );
    }

    public InvoiceRequestDTO commandToInvoice(Command command) {
        return new InvoiceRequestDTO(
                command.getQuote().getQotNumber(),
                command.getCmdId()
        );
    }

}

