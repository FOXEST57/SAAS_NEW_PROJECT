package com.mns.cda.saas_facturation.cart.service;

import com.mns.cda.saas_facturation.cart.DTO.InvoiceLineDTO;
import com.mns.cda.saas_facturation.cart.Iservice.IInvoiceLineService;
import com.mns.cda.saas_facturation.cart.mapper.InvoiceLineMapper;
import com.mns.cda.saas_facturation.cart.model.InvoiceLine;
import com.mns.cda.saas_facturation.cart.model.QuoteLine;
import com.mns.cda.saas_facturation.cart.repository.InvoiceLineRepository;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class InvoiceLineService implements IInvoiceLineService {

    private final InvoiceLineRepository invoiceLineRepository;
    private final InvoiceLineMapper invoiceLineMapper;

    @Override
    public List<InvoiceLineDTO> findAll() {
        return invoiceLineRepository.findAll()
                .stream()
                .map(invoiceLineMapper::toDTO)
                .toList();
    }

    @Override
    public Optional<InvoiceLineDTO> findById(Long invLnId) {
        return invoiceLineRepository.findById(invLnId).map(invoiceLineMapper::toDTO);
    }

//    @Override
//    public InvoiceLine create(QuoteLine quoteLine) {
//
//        InvoiceLine invoiceLine = new InvoiceLine(
//                null,
//                quoteLine.getQotLnQuantity(),
//                quoteLine.getQotLnPriceHT(),
//                quoteLine.getArticleName(),
//                quoteLine.getArticleRef(),
//                quoteLine.getTvaRate(),
//                null
//
//        );
//        return invoiceLineRepository.save(invoiceLine);
//    }

    @Override
    public InvoiceLine build(QuoteLine quoteLine) {
        InvoiceLine line = new InvoiceLine();
        line.setInvLnQuantity(quoteLine.getQotLnQuantity());
        line.setInvLnPriceHT(quoteLine.getQotLnPriceHT());
        line.setArticleName(quoteLine.getArticleName());
        line.setArticleRef(quoteLine.getArticleRef());
        line.setTvaRate(quoteLine.getTvaRate());
        // `invoice` est renseigné par l'appelant : c'est lui qui connaît le devis.
        return line;
    }

    @Override
    public void delete(Long invoiceLineId) throws ResourceNotFoundException {
        InvoiceLine invoiceLine = invoiceLineRepository.findById(invoiceLineId).orElseThrow(() -> new ResourceNotFoundException("Ligne de Devis non existant"));

        invoiceLineRepository.delete(invoiceLine);
    }

}
