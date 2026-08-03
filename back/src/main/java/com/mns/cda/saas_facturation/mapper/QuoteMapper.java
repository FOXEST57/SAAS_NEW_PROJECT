package com.mns.cda.saas_facturation.mapper;

import com.mns.cda.saas_facturation.DTO.QuoteDTO;
import com.mns.cda.saas_facturation.model.Quote;
import com.mns.cda.saas_facturation.model.QuoteLine;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class QuoteMapper {

    private final CartMapper cartMapper;
    private final QuoteLineMapper quoteLineMapper;

    public QuoteDTO toDTO(Quote quote) {
        //Total des prix HT et TVA calculés à partir des lignes de devis
        BigDecimal totalHT = quote.getQotLines()
                        .stream()
                        .map(quoteLine -> quoteLine.getQotLnPriceHT()
                                .multiply(BigDecimal.valueOf(quoteLine.getQotLnQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        //Total de la TVA calculés à partir des lignes de devis
        BigDecimal totalTva = quote.getQotLines()
                .stream()
                .map(quoteLine -> quoteLine.getQotLnPriceHT()
                        .multiply(quoteLine.getTvaRate())
                        .multiply(BigDecimal.valueOf(quoteLine.getQotLnQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //Total TTC
        BigDecimal totalTTC = totalHT.add(totalTva);
        return new QuoteDTO(
                quote.getQotNumber(),
                quote.getQotCreatedDate(),
                quote.getQotExpirationDate(),
                quote.getQotStatus(),
                quote.getQotParent() != null ? this.toDTO(quote.getQotParent()) : null,
                quote.getCart().getCrtRef(),
                quote.getQotLines().stream().map(quoteLineMapper::toDTO).toList(),
                totalHT,
                totalTva,
                totalTTC
                );
    }
}
