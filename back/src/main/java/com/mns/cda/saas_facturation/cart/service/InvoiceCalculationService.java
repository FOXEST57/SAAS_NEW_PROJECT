package com.mns.cda.saas_facturation.cart.service;

import com.mns.cda.saas_facturation.cart.model.Invoice;
import com.mns.cda.saas_facturation.cart.model.InvoiceLine;
import com.mns.cda.saas_facturation.cart.model.Payment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class InvoiceCalculationService {

    public BigDecimal calculateTotalTTC(Invoice invoice) {
        return invoice.getInvoiceLines().stream()
                .map(this::calculateLineTTC)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateLineTTC(InvoiceLine line) {

        BigDecimal totalHT = line.getInvLnPriceHT()
                .multiply(BigDecimal.valueOf(line.getInvLnQuantity()));

        return totalHT
                .multiply(BigDecimal.ONE.add(line.getTvaRate()))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateRemainingAmount(
            Invoice invoice
    ) {
        BigDecimal totalPaid = this.calculateTotalTTC(invoice);
        return calculateTotalTTC(invoice)
                .subtract(totalPaid);
    }
}