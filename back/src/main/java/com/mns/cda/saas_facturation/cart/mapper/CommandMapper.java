package com.mns.cda.saas_facturation.cart.mapper;

import com.mns.cda.saas_facturation.cart.DTO.CommandDTO;
import com.mns.cda.saas_facturation.cart.DTO.QuoteLineDTO;
import com.mns.cda.saas_facturation.cart.model.Command;
import com.mns.cda.saas_facturation.cart.model.QuoteLine;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CommandMapper {

    protected final QuoteLineMapper quoteLineMapper;

    public CommandDTO toDTO(Command command) {

        List<QuoteLineDTO> quoteLines = command.getQuote().getQotLines() != null ?
                command.getQuote().getQotLines()
                .stream()
                .map(quoteLineMapper::toDTO)
                .toList() :
                List.of();


        return new CommandDTO(
                command.getCmdId(),
                command.getCmdCreateDate(),
                command.getCmdModifiedDate(),
                command.getCmdStatus(),
                command.getQuote().getQotId(),
                command.getQuote().getQotNumber(),
                command.getCmdReference(),
                quoteLines,
                command.getCreatorId(),
                command.getReceiverEmail()
        );

    }
}
