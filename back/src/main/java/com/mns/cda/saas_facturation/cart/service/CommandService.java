package com.mns.cda.saas_facturation.cart.service;

import com.mns.cda.saas_facturation.cart.DTO.CommandDTO;
import com.mns.cda.saas_facturation.cart.DTO.requestDTO.CommandRequestDTO;
import com.mns.cda.saas_facturation.cart.DTO.patchDTO.PatchCommandStatus;
import com.mns.cda.saas_facturation.cart.DTO.requestDTO.InvoiceRequestDTO;
import com.mns.cda.saas_facturation.cart.mapper.CartPipelineMapper;
import com.mns.cda.saas_facturation.cart.mapper.CommandMapper;
import com.mns.cda.saas_facturation.cart.model.Command;
import com.mns.cda.saas_facturation.cart.model.Quote;
import com.mns.cda.saas_facturation.cart.repository.CommandRepository;
import com.mns.cda.saas_facturation.cart.repository.QuoteRepository;
import com.mns.cda.saas_facturation.cart.service.pipeline.CommandAcceptedEvent;
import com.mns.cda.saas_facturation.enumeration.CommandStatus;
import com.mns.cda.saas_facturation.exception.ResourceAlreadyExistException;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.referencement.ReferenceCounterService;
import com.mns.cda.saas_facturation.referencement.ReferenceType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommandService implements com.mns.cda.saas_facturation.cart.Iservice.ICommandService {

    private final CommandRepository commandRepository;
    private final CommandMapper commandMapper;
    private final QuoteRepository quoteRepository;

    private final ReferenceCounterService referenceCounterService;
    private final ApplicationEventPublisher publisher;

    @Override
    public List<CommandDTO> findAll() {
        return commandRepository.findAll()
                .stream()
                .map(commandMapper::toDTO)
                .toList();
    }

    @Override
    public CommandDTO findById(Long id) {
        return commandRepository.findById(id)
                .map(commandMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Command not found with id: " + id));
    }

    @Override
    @Transactional
    public CommandDTO create(CommandRequestDTO dto) throws ResourceAlreadyExistException {

        Quote quote = quoteRepository.findById(dto.qotId())
                .orElseThrow(() -> new ResourceNotFoundException("Quote not found with id: " + dto.qotId()));
        if (commandRepository.existsByQuote_QotId(quote.getQotId())) {
            throw new ResourceAlreadyExistException("Une Commande existe déjà pour ce Devis.");
        }

        Command command = new Command();
        command.setQuote(quote);
        command.setCmdStatus(CommandStatus.CREATED);
        command.setCmdReference(referenceCounterService.generateReference(null, ReferenceType.COMMAND));
        command.setCreatorId(quote.getCreatorId());
        command.setReceiverEmail(quote.getReceiverEmail());

        return commandMapper.toDTO(commandRepository.save(command));
    }

    @Override
    @Transactional
    public CommandDTO patchStatus(PatchCommandStatus dto) {

        Command command = commandRepository.findById(dto.cmdId())
                .orElseThrow(() -> new ResourceNotFoundException("Command not found with id: " + dto.cmdId()));

        command.setCmdStatus(dto.cmdStatus());
        if (dto.cmdStatus() == CommandStatus.ACCEPTED) {
         publisher.publishEvent(new CommandAcceptedEvent(command));
        }
        return commandMapper.toDTO(commandRepository.save(command));
    }

    @Override
    @Transactional
    public CommandDTO delete(Long id) {
        Command command = commandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Command not found with id: " + id));

        commandRepository.delete(command);
        return commandMapper.toDTO(command);
    }
}
