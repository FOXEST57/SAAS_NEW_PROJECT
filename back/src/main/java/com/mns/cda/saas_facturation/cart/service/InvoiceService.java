package com.mns.cda.saas_facturation.cart.service;

import com.mns.cda.saas_facturation.cart.DTO.InvoiceDTO;
import com.mns.cda.saas_facturation.cart.DTO.requestDTO.InvoiceRequestDTO;
import com.mns.cda.saas_facturation.cart.DTO.requestDTO.PatchInvoiceStatus;
import com.mns.cda.saas_facturation.cart.Iservice.IInvoiceService;
import com.mns.cda.saas_facturation.cart.mapper.InvoiceMapper;
import com.mns.cda.saas_facturation.cart.model.Command;
import com.mns.cda.saas_facturation.cart.model.Invoice;
import com.mns.cda.saas_facturation.cart.model.InvoiceLine;
import com.mns.cda.saas_facturation.cart.repository.CommandRepository;
import com.mns.cda.saas_facturation.cart.repository.InvoiceRepository;
import com.mns.cda.saas_facturation.enumeration.InvoiceStatus;
import com.mns.cda.saas_facturation.exception.ResourceAlreadyExistException;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService implements IInvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;
    private final CommandRepository commandRepository;
    private final InvoiceLineService invoiceLineService;

    @Override
    public List<InvoiceDTO> findAll() {
        return invoiceRepository.findAll()
                .stream()
                .map(invoiceMapper::toDTO)
                .toList();
    }

    @Override
    public InvoiceDTO findById(Long qotId) {
        Invoice invoice = invoiceRepository.findById(qotId).orElseThrow(() -> new ResourceNotFoundException("Devis non existant"));

        return invoiceMapper.toDTO(invoice);
    }

    @Override
    @Transactional
    public InvoiceDTO create(InvoiceRequestDTO invoiceRequestDTO) {
        Command command = commandRepository.findById(invoiceRequestDTO.commandId()).orElseThrow(() -> new ResourceNotFoundException("Commande non existante"));

        if (invoiceRepository.existsByCommand_CmdId(command.getCmdId())) {
            throw new ResourceAlreadyExistException("Une facture existe déjà pour cette commande.");
        }

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(invoiceRequestDTO.invoiceNumber());
        invoice.setInvoiceStatus(InvoiceStatus.CREATED);
        invoice.setCommand(command);
        invoice.setInvoicePathPDF("ceci est une URL");

        command.getQuote().getQotLines().forEach(ql -> {
            InvoiceLine line = invoiceLineService.build(ql);   // sans save()
            line.setInvoice(invoice);
            invoice.getInvoiceLines().add(line);
        });

        return invoiceMapper.toDTO(invoiceRepository.save(invoice));
    }

    @Transactional
    @Override
    public void delete(Long qotId) {
        Invoice invoice = invoiceRepository.findById(qotId).orElseThrow(() -> new ResourceNotFoundException("Facture non existante"));

        invoiceRepository.delete(invoice);
    }


    @Override
    public InvoiceDTO updateStatus(PatchInvoiceStatus dto) {
        Invoice invoice = invoiceRepository.findById(dto.invoiceId()).orElseThrow(() -> new ResourceNotFoundException("Facture non existante"));
        invoice.setInvoiceStatus(dto.invoiceStatus());
        return invoiceMapper.toDTO(invoiceRepository.save(invoice));
    }
}
