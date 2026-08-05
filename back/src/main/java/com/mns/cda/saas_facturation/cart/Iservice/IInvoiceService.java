package com.mns.cda.saas_facturation.cart.Iservice;

import com.mns.cda.saas_facturation.cart.DTO.InvoiceDTO;
import com.mns.cda.saas_facturation.cart.DTO.requestDTO.InvoiceRequestDTO;
import com.mns.cda.saas_facturation.cart.DTO.patchDTO.PatchInvoiceStatus;

import java.util.List;

public interface IInvoiceService {

    List<InvoiceDTO> findAll();

    InvoiceDTO findById(Long id);

    InvoiceDTO create (InvoiceRequestDTO dto);

    InvoiceDTO updateStatus(PatchInvoiceStatus dto);

    void delete (Long id);


}
