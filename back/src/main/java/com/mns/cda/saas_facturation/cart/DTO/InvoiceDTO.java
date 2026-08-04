package com.mns.cda.saas_facturation.cart.DTO;

import com.mns.cda.saas_facturation.enumeration.InvoiceStatus;

import java.time.LocalDateTime;
import java.util.List;

public record InvoiceDTO (
    Long invoiceId,
    String invoiceNumber,
    LocalDateTime invoiceCreatedDate,
    String invoicePathPDF,
    InvoiceStatus invoiceStatus,
    List<InvoiceLineDTO> invoiceLines
){}
