package com.mns.cda.saas_facturation.cart.Iservice;

import com.mns.cda.saas_facturation.cart.DTO.InvoiceLineDTO;
import com.mns.cda.saas_facturation.cart.model.InvoiceLine;
import com.mns.cda.saas_facturation.cart.model.QuoteLine;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

public interface IInvoiceLineService {

    List<InvoiceLineDTO> findAll();

    Optional<InvoiceLineDTO> findById(Long invLnId);

//    InvoiceLine create(QuoteLine quoteLine);

    // Construit la ligne figée sans la persister : le devis s'en charge par cascade.
    InvoiceLine build(QuoteLine quoteLine);

    void delete(Long quotLineId) throws ResourceNotFoundException;

}
