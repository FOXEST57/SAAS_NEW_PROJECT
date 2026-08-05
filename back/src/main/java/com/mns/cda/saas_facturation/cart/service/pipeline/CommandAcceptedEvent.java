package com.mns.cda.saas_facturation.cart.service.pipeline;

import com.mns.cda.saas_facturation.cart.model.Command;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommandAcceptedEvent {

    private final Command command;

}
