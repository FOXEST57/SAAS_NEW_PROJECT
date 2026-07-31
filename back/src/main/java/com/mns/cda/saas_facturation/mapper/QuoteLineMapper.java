package com.mns.cda.saas_facturation.mapper;

import com.mns.cda.saas_facturation.DTO.QuoteLineDTO;
import com.mns.cda.saas_facturation.model.QuoteLine;

import java.math.BigDecimal;

public class QuoteLineMapper {


    public QuoteLineDTO toDTO(QuoteLine quoteLine) {

        BigDecimal totalHT = quoteLine.getQotLnPriceHT()
                .multiply(BigDecimal.valueOf(quoteLine.getQotLnQuantity()));
    }

}
