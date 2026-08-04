package com.mns.cda.saas_facturation.cart.mapper;

import com.mns.cda.saas_facturation.cart.DTO.InvoiceLineDTO;
import com.mns.cda.saas_facturation.cart.model.InvoiceLine;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class InvoiceLineMapper {

    public InvoiceLineDTO toDTO(InvoiceLine invoiceLine) {

        BigDecimal totalHT = invoiceLine.getInvLnPriceHT()
                .multiply(BigDecimal.valueOf(invoiceLine.getInvLnQuantity()));

        BigDecimal totalTVA = invoiceLine.getInvLnPriceHT()
                .multiply(invoiceLine.getTvaRate())
                .multiply(BigDecimal.valueOf(invoiceLine.getInvLnQuantity()));

        BigDecimal totalTTC = totalHT.add(totalTVA);

        return new InvoiceLineDTO(
                invoiceLine.getInvLnId(),
                invoiceLine.getInvLnQuantity(),
                invoiceLine.getInvLnPriceHT(),
                invoiceLine.getArticleName(),
                invoiceLine.getArticleRef(),
                invoiceLine.getTvaRate(),
                totalHT,
                totalTVA,
                totalTTC
        );
    }
    
}
