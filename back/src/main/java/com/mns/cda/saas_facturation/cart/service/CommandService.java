package com.mns.cda.saas_facturation.cart.service;

import com.mns.cda.saas_facturation.cart.DTO.CommandDTO;
import com.mns.cda.saas_facturation.cart.DTO.requestDTO.CommandRequestDTO;
import com.mns.cda.saas_facturation.cart.DTO.requestDTO.PatchCommandStatus;
import com.mns.cda.saas_facturation.cart.mapper.CommandMapper;
import com.mns.cda.saas_facturation.cart.model.Command;
import com.mns.cda.saas_facturation.cart.model.Quote;
import com.mns.cda.saas_facturation.cart.repository.CommandRepository;
import com.mns.cda.saas_facturation.cart.repository.QuoteRepository;
import com.mns.cda.saas_facturation.enumeration.CommandStatus;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommandService implements com.mns.cda.saas_facturation.cart.Iservice.ICommandService {

    private final CommandRepository commandRepository;
    private final CommandMapper commandMapper;
    private final QuoteRepository quoteRepository;

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
    public CommandDTO create(CommandRequestDTO dto) {

        Quote quote = quoteRepository.findById(dto.qotId())
                .orElseThrow(() -> new ResourceNotFoundException("Quote not found with id: " + dto.qotId()));

        Command command = new Command();
        command.setQuote(quote);
        command.setCmdStatus(CommandStatus.CREATED);

        return commandMapper.toDTO(commandRepository.save(command));
    }

    @Override
    public CommandDTO patchStatus(PatchCommandStatus dto) {

        Command command = commandRepository.findById(dto.cmdId())
                .orElseThrow(() -> new ResourceNotFoundException("Command not found with id: " + dto.cmdId()));

        command.setCmdStatus(dto.cmdStatus());
        return commandMapper.toDTO(commandRepository.save(command));
    }

    @Override
    public CommandDTO delete(Long id) {
        Command command = commandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Command not found with id: " + id));

        commandRepository.delete(command);
        return commandMapper.toDTO(command);
    }
}
