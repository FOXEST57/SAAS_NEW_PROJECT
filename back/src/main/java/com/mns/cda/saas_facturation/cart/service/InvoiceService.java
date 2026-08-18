package com.mns.cda.saas_facturation.cart.service;

import com.mns.cda.saas_facturation.cart.DTO.InvoiceDTO;
import com.mns.cda.saas_facturation.cart.DTO.requestDTO.InvoiceRequestDTO;
import com.mns.cda.saas_facturation.cart.Iservice.IInvoicePdfService;
import com.mns.cda.saas_facturation.cart.DTO.patchDTO.PatchInvoiceStatus;
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
import com.mns.cda.saas_facturation.referencement.ReferenceCounterService;
import com.mns.cda.saas_facturation.referencement.ReferenceType;
import com.mns.cda.saas_facturation.user.model.Corporation;
import com.mns.cda.saas_facturation.user.model.Customer;
import com.mns.cda.saas_facturation.user.repository.CorporationRepository;
import com.mns.cda.saas_facturation.user.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService implements IInvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;
    private final CommandRepository commandRepository;
    private final InvoiceLineService invoiceLineService;
    private final IInvoicePdfService invoicePdfService;
    private final CorporationRepository corporationRepository;

    private final ReferenceCounterService referenceCounterService;

    /**
     * Valeur temporaire du chemin du PDF. Le vrai chemin dépend de la date
     * d'émission, qui n'est connue qu'une fois la facture enregistrée — mais
     * la colonne refuse d'être vide, il faut donc y mettre quelque chose.
     */
    private static final String PDF_PENDING = "en cours de génération";
    private final CustomerRepository customerRepository;

    @Override
    public List<InvoiceDTO> findAll() {
        return invoiceRepository.findAll()
                .stream()
                .map(invoiceMapper::toDTO)
                .toList();
    }

    @Override
    public InvoiceDTO findById(Long invId) {
        Invoice invoice = invoiceRepository.findById(invId).orElseThrow(() -> new ResourceNotFoundException("Devis non existant"));

        return invoiceMapper.toDTO(invoice);
    }

    @Override
    @Transactional
    public InvoiceDTO create(InvoiceRequestDTO invoiceRequestDTO) {
        Command command = commandRepository.findById(invoiceRequestDTO.commandId())
                .orElseThrow(() -> new ResourceNotFoundException("Commande non existante"));

        if (invoiceRepository.existsByCommand_CmdId(command.getCmdId())) {
            throw new ResourceAlreadyExistException("Une facture existe déjà pour cette commande.");
        }

        Invoice invoice = new Invoice();
        invoice.setInvoiceStatus(InvoiceStatus.CREATED);
        invoice.setCommand(command);
        invoice.setInvoicePathPDF(PDF_PENDING);

        invoice.setCreatorId(command.getCreatorId());
        invoice.setReceiverEmail(command.getReceiverEmail());

        Customer creator = customerRepository.findById(invoice.getCreatorId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer non existante"));
        invoice.setInvoiceNumber(referenceCounterService.generateReference(creator.getCorporation(), ReferenceType.INVOICE));

        command.getQuote().getQotLines().forEach(ql -> {
            InvoiceLine line = invoiceLineService.build(ql);   // sans save()
            line.setInvoice(invoice);
            invoice.getInvoiceLines().add(line);
        });

        // Premier enregistrement : c'est lui qui déclenche l'audit et remplit
        // invoiceCreatedDate, dont le PDF a besoin.
        Invoice saved = invoiceRepository.save(invoice);

        return invoiceMapper.toDTO(invoiceRepository.save(saved));
    }

    @Transactional
    @Override
    public void delete(Long invId) {
        Invoice invoice = invoiceRepository.findById(invId).orElseThrow(() -> new ResourceNotFoundException("Facture non existante"));

        invoiceRepository.delete(invoice);
    }


    @Override
    @Transactional
    public InvoiceDTO updateStatus(PatchInvoiceStatus dto) {

        Invoice invoice = invoiceRepository.findById(dto.invoiceId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Facture non existante"));

        InvoiceStatus currentStatus = invoice.getInvoiceStatus();
        InvoiceStatus newStatus = dto.invoiceStatus();

        if (!isValidTransition(currentStatus, newStatus)) {
            throw new IllegalStateException(
                    "Transition de statut impossible : "
                            + currentStatus + " → " + newStatus
            );
        }

        if (currentStatus == InvoiceStatus.CREATED
                && newStatus == InvoiceStatus.ISSUED) {

            invoice.setInvoiceStatus(InvoiceStatus.ISSUED);

            invoice.setInvoicePathPDF(
                    invoicePdfService.generate(invoice)
            );

        } else {
            invoice.setInvoiceStatus(newStatus);
        }

        return invoiceMapper.toDTO(invoiceRepository.save(invoice));
    }

    private boolean isValidTransition(
            InvoiceStatus currentStatus,
            InvoiceStatus newStatus
    ) {
        return switch (currentStatus) {

            case CREATED ->
                    newStatus == InvoiceStatus.ISSUED
                            || newStatus == InvoiceStatus.CANCELLED;

            case ISSUED ->
                    newStatus == InvoiceStatus.SENT
                            || newStatus == InvoiceStatus.CANCELLED;

            case SENT ->
                    newStatus == InvoiceStatus.PARTIALLY_PAID
                            || newStatus == InvoiceStatus.PAID
                            || newStatus == InvoiceStatus.OVERDUE
                            || newStatus == InvoiceStatus.CANCELLED;

            case PARTIALLY_PAID ->
                    newStatus == InvoiceStatus.PAID
                            || newStatus == InvoiceStatus.OVERDUE
                            || newStatus == InvoiceStatus.CANCELLED;

            case OVERDUE ->
                    newStatus == InvoiceStatus.PAID
                            || newStatus == InvoiceStatus.CANCELLED;

            case PAID, CANCELLED ->
                    false;
        };
    }

}
