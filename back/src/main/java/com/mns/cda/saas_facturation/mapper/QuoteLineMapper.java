package com.mns.cda.saas_facturation.mapper;

import com.mns.cda.saas_facturation.DTO.QuoteLineDTO;
import com.mns.cda.saas_facturation.model.QuoteLine;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class QuoteLineMapper {


    public QuoteLineDTO toDTO(QuoteLine quoteLine) {

        BigDecimal totalHT = quoteLine.getQotLnPriceHT()
                .multiply(BigDecimal.valueOf(quoteLine.getQotLnQuantity()));

        BigDecimal totalTVA = quoteLine.getQotLnPriceHT()
                .multiply(quoteLine.getTvaRate())
                .multiply(BigDecimal.valueOf(quoteLine.getQotLnQuantity()));

        BigDecimal totalTTC = totalHT.add(totalTVA);

        return new QuoteLineDTO(
                quoteLine.getQotLnId(),
                quoteLine.getQotLnQuantity(),
                quoteLine.getQotLnPriceHT(),
                quoteLine.getArticleName(),
                quoteLine.getArticleRef(),
                quoteLine.getTvaRate(),
                totalHT,
                totalTVA,
                totalTTC
        );
    }

}
