package com.mns.cda.saas_facturation.cart.service.pipeline;

import com.mns.cda.saas_facturation.cart.model.Quote;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuoteAcceptedEvent {

    private final Quote quote;

}
