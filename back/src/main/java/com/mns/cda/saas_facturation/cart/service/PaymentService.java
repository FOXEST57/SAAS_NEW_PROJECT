package com.mns.cda.saas_facturation.cart.service;

import com.mns.cda.saas_facturation.cart.DTO.PaymentDTO;
import com.mns.cda.saas_facturation.cart.DTO.requestDTO.PaymentRequestDTO;
import com.mns.cda.saas_facturation.cart.Iservice.IPaymentService;
import com.mns.cda.saas_facturation.cart.mapper.PaymentMapper;
import com.mns.cda.saas_facturation.cart.model.Invoice;
import com.mns.cda.saas_facturation.cart.model.Payment;
import com.mns.cda.saas_facturation.cart.repository.InvoiceRepository;
import com.mns.cda.saas_facturation.cart.repository.PaymentRepository;
import com.mns.cda.saas_facturation.enumeration.InvoiceStatus;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService implements IPaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceCalculationService invoiceCalculationService;

    @Override
    public List<PaymentDTO> findAll() {
        return paymentRepository.findAll()
                .stream()
                .map(paymentMapper::toDTO)
                .toList();
    }

    @Override
    public PaymentDTO findById(Long payId) {
        Payment payment = paymentRepository.findById(payId)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement non existant"));

        return paymentMapper.toDTO(payment);
    }

    @Override
    @Transactional
    public PaymentDTO create(PaymentRequestDTO paymentRequestDTO) {

        Invoice invoice = invoiceRepository.findById(paymentRequestDTO.invoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Facture non existante"));

        if (invoice.getInvoiceStatus() == InvoiceStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Impossible d'effectuer un paiement sur une facture annulée."
            );
        }

        if (invoice.getInvoiceStatus() == InvoiceStatus.PAID) {
            throw new IllegalStateException(
                    "Cette facture est déjà totalement payée."
            );
        }

        // Total des paiements déjà enregistrés
        BigDecimal totalPaid =
                paymentRepository.sumPayAmountByInvoiceId(invoice.getInvoiceId());

        // Montant restant avant le nouveau paiement
        BigDecimal remainingAmount =
                invoiceCalculationService.calculateRemainingAmount(
                        invoice,
                        totalPaid
                );

        // Vérification du dépassement
        if (paymentRequestDTO.payAmount().compareTo(remainingAmount) > 0) {
            throw new IllegalArgumentException(
                    "Le paiement dépasse le montant restant dû."
            );
        }

        // Création du paiement
        Payment payment = new Payment();

        payment.setPayType(paymentRequestDTO.payType());
        payment.setPayAccount(paymentRequestDTO.payAccount());
        payment.setPayAmount(paymentRequestDTO.payAmount());
        payment.setInvoice(invoice);

        Payment savedPayment = paymentRepository.save(payment);

        // Nouveau total payé après le nouveau paiement
        BigDecimal newTotalPaid = totalPaid.add(savedPayment.getPayAmount());

        // Total TTC de la facture
        BigDecimal totalTTC =
                invoiceCalculationService.calculateTotalTTC(invoice);

        // Mise à jour du statut
        if (newTotalPaid.compareTo(totalTTC) >= 0) {
            invoice.setInvoiceStatus(InvoiceStatus.PAID);
        } else {
            invoice.setInvoiceStatus(InvoiceStatus.PARTIALLY_PAID);
        }

        invoiceRepository.save(invoice);

        return paymentMapper.toDTO(savedPayment);
    }

    @Override
    public void delete(Long payId) {

        // à corriger en fonction des règles métiers car un paiement n'est pas supprimé mais annulé via un remboursement par exemple
        Payment payment = paymentRepository.findById(payId)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement non existant"));

        paymentRepository.delete(payment);
    }
}