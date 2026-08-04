package com.mns.cda.saas_facturation.cart.DTO.requestDTO;

import com.mns.cda.saas_facturation.enumeration.InvoiceStatus;
import jakarta.validation.constraints.NotNull;

public record PatchInvoiceStatus(
        @NotNull Long invoiceId,
        @NotNull InvoiceStatus invoiceStatus
) {
}
