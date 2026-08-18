package com.mns.cda.saas_facturation.cart.Iservice;

import com.mns.cda.saas_facturation.cart.DTO.PaymentDTO;
import com.mns.cda.saas_facturation.cart.DTO.requestDTO.PaymentRequestDTO;

import java.util.List;

public interface IPaymentService {

    List<PaymentDTO> findAll();

    PaymentDTO findById(Long payId);

    PaymentDTO create(PaymentRequestDTO paymentRequestDTO);

    void delete(Long payId);
}