package com.mns.cda.saas_facturation.cart.service;

import com.mns.cda.saas_facturation.cart.model.Invoice;
import com.mns.cda.saas_facturation.cart.model.InvoiceLine;
import com.mns.cda.saas_facturation.cart.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class InvoiceCalculationService {

    private final PaymentRepository paymentRepository;

    /**
     * Total TTC de la facture.
     *
     * <p>L'arrondi se fait <strong>ligne par ligne</strong>, et non sur la somme :
     * c'est cette valeur qui borne les règlements, elle doit donc être celle
     * que le client voit affichée.</p>
     */
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

    /** Cumul des règlements déjà imputés sur la facture. */
    public BigDecimal calculateTotalPaid(Invoice invoice) {
        BigDecimal paid = paymentRepository.sumPayAmountByInvoiceId(invoice.getInvoiceId());
        return paid != null ? paid : BigDecimal.ZERO;
    }

    /**
     * Montant restant dû : total TTC moins les règlements enregistrés.
     *
     * <p>Attention à ne pas retomber dans le défaut précédent, où {@code totalPaid}
     * était initialisé avec {@code calculateTotalTTC(invoice)}. La soustraction
     * rendait alors systématiquement zéro, ce qui avait deux conséquences :
     * le restant dû exposé par {@code InvoiceDTO} était toujours nul, et
     * {@code PaymentService} comparait chaque versement à ce zéro — donc
     * refusait tout paiement.</p>
     */
    public BigDecimal calculateRemainingAmount(Invoice invoice) {
        return calculateTotalTTC(invoice)
                .subtract(calculateTotalPaid(invoice))
                .max(BigDecimal.ZERO);
    }
}
