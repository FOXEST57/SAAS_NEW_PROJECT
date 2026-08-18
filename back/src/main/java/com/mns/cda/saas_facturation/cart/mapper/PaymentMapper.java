package com.mns.cda.saas_facturation.cart.mapper;

import com.mns.cda.saas_facturation.cart.DTO.PaymentDTO;
import com.mns.cda.saas_facturation.cart.model.Payment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PaymentMapper {

    public PaymentDTO toDTO(Payment payment) {
        return new PaymentDTO(
                payment.getPayId(),
                payment.getPayCreatedDate(),
                payment.getPayType(),
                payment.getPayAccount(),
                payment.getPayAmount(),
                payment.getInvoice().getInvoiceId()
        );
    }
}