package com.mns.cda.saas_facturation.cart.repository;

import com.mns.cda.saas_facturation.cart.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("""
        SELECT COALESCE(SUM(p.payAmount), 0)
        FROM Payment p
        WHERE p.invoice.invoiceId = :invoiceId
    """)
    BigDecimal sumPayAmountByInvoiceId(@Param("invoiceId") Long invoiceId);
}