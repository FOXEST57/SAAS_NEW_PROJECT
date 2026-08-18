package com.mns.cda.saas_facturation.cart.mapper;

import com.mns.cda.saas_facturation.cart.DTO.requestDTO.*;
import com.mns.cda.saas_facturation.cart.model.Cart;
import com.mns.cda.saas_facturation.cart.model.Command;
import com.mns.cda.saas_facturation.cart.model.Quote;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;


@Service
@AllArgsConstructor
public class CartPipelineMapper {

    public QuoteRequestDTO cartToQuote(Cart cart) {
        LocalDate expirationDate = cart.getCrtCreateDate().plusMonths(1).toLocalDate();
        return new QuoteRequestDTO(
                expirationDate,
                cart.getParentQuoteId(),
                cart.getCrtId()
        );
    }

    public CartRequestDTO quoteToCartRevision(Quote quote) {

        return new CartRequestDTO(
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
                command.getCmdId()
        );
    }

}

