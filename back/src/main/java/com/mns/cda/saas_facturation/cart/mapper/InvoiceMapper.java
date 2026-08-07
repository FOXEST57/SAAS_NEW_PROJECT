package com.mns.cda.saas_facturation.cart.mapper;

import com.mns.cda.saas_facturation.cart.DTO.InvoiceDTO;
import com.mns.cda.saas_facturation.cart.DTO.InvoiceLineDTO;
import com.mns.cda.saas_facturation.cart.model.Invoice;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class InvoiceMapper {

    protected final InvoiceLineMapper invoiceLineMapper;

    public InvoiceDTO toDTO(Invoice invoice) {

        List<InvoiceLineDTO> invoiceLines = invoice.getInvoiceLines() != null ?
                invoice.getInvoiceLines().stream()
                        .map(invoiceLineMapper::toDTO)
                        .toList() :
                List.of();

        return new InvoiceDTO(
                invoice.getInvoiceId(),
                invoice.getInvoiceNumber(),
                invoice.getInvoiceCreatedDate(),
                invoice.getInvoicePathPDF(),
                invoice.getInvoiceStatus(),
                invoiceLines,
                invoice.getCreatorId(),
                invoice.getReceiverEmail()
        );
    }
}
