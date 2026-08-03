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

        BigDecimal totalHT,totalTVA,totalTTC;
        totalHT = BigDecimal.ZERO;
        totalTVA = BigDecimal.ZERO;

        //Total des prix HT et TVA calculés à partir des lignes de devis
        if (quote.getQotParent() != null) {
            totalHT = totalHT.add(quote.getQotParent().getQotLines()
                    .stream()
                    .map(quoteLine -> quoteLine.getQotLnPriceHT()
                            .multiply(BigDecimal.valueOf(quoteLine.getQotLnQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
        }
        totalHT = totalHT.add(quote.getQotLines()
                .stream()
                .map(quoteLine -> quoteLine.getQotLnPriceHT()
                        .multiply(BigDecimal.valueOf(quoteLine.getQotLnQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));


        //Total de la TVA calculés à partir des lignes de devis
        if (quote.getQotParent() != null) {
            totalTVA = totalTVA.add(quote.getQotParent().getQotLines()
                    .stream()
                    .map(quoteLine -> quoteLine.getQotLnPriceHT()
                            .multiply(quoteLine.getTvaRate())
                            .multiply(BigDecimal.valueOf(quoteLine.getQotLnQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
        }
        totalTVA = totalTVA.add(quote.getQotLines()
                .stream()
                .map(quoteLine -> quoteLine.getQotLnPriceHT()
                        .multiply(quoteLine.getTvaRate())
                        .multiply(BigDecimal.valueOf(quoteLine.getQotLnQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        //Total TTC
        totalTTC = BigDecimal.ZERO.add(totalHT.add(totalTVA));
        return new QuoteDTO(
                quote.getQotNumber(),
                quote.getQotCreatedDate(),
                quote.getQotExpirationDate(),
                quote.getQotStatus(),
                quote.getQotParent() != null ? this.toDTO(quote.getQotParent()) : null,
                quote.getCart().getCrtRef(),
                quote.getQotLines().stream().map(quoteLineMapper::toDTO).toList(),
                totalHT,
                totalTVA,
                totalTTC
                );
    }
}
