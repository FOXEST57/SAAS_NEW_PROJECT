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
        QuoteDTO quoteParent = quote.getQotParent() != null ? this.toDTO(quote.getQotParent()) : null;

        BigDecimal totalHT,totalTVA,totalTTC;
        totalHT = BigDecimal.ZERO;
        totalTVA = BigDecimal.ZERO;


        // Récupère les totaux du parent direct et les initialise aux totaux.
        if (quote.getQotParent() != null) {
            totalHT = totalHT.add(quoteParent.totalHT());
            totalTVA = totalTVA.add(quoteParent.totalTva());
        }

        //Total des prix HT et TVA calculés à partir des lignes de devis
        totalHT = totalHT.add(quote.getQotLines()
                .stream()
                .map(quoteLine -> quoteLine.getQotLnPriceHT()
                        .multiply(BigDecimal.valueOf(quoteLine.getQotLnQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));


        //Total de la TVA calculés à partir des lignes de devis
        totalTVA = totalTVA.add(quote.getQotLines()
                .stream()
                .map(quoteLine -> quoteLine.getQotLnPriceHT()
                        .multiply(quoteLine.getTvaRate())
                        .multiply(BigDecimal.valueOf(quoteLine.getQotLnQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        //Total TTC
        totalTTC = BigDecimal.ZERO.add(totalHT.add(totalTVA));
        return new QuoteDTO(
                quote.getQotId(),
                quote.getQotNumber(),
                quote.getQotCreatedDate(),
                quote.getQotExpirationDate(),
                quote.getQotStatus(),
                quote.getQotParent() != null ? this.toDTO(quote.getQotParent()) : null,
                quote.getCart().getCrtId(),
                quote.getQotLines().stream().map(quoteLineMapper::toDTO).toList(),
                totalHT,
                totalTVA,
                totalTTC
                );
    }
}
