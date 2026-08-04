package com.mns.cda.saas_facturation.cart.service;

import com.mns.cda.saas_facturation.cart.DTO.QuoteLineDTO;
import com.mns.cda.saas_facturation.cart.DTO.updateDTO.PatchQuoteLineQuantity;
import com.mns.cda.saas_facturation.cart.Iservice.IQuoteLineService;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.cart.mapper.QuoteLineMapper;
import com.mns.cda.saas_facturation.cart.model.OrderLine;
import com.mns.cda.saas_facturation.cart.model.QuoteLine;
import com.mns.cda.saas_facturation.product.model.Article;
import com.mns.cda.saas_facturation.cart.repository.QuoteLineRepository;
import com.mns.cda.saas_facturation.cart.repository.QuoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QuoteLineService implements IQuoteLineService {

    private final QuoteLineRepository quoteLineRepository;
    private final QuoteLineMapper quoteLineMapper;
    private final QuoteRepository quoteRepository;

    @Override
    public List<QuoteLineDTO> findAll() {
        return quoteLineRepository.findAll()
                .stream()
                .map(quoteLineMapper::toDTO)
                .toList();
    }

    @Override
    public Optional<QuoteLineDTO> findById(Long cntId) {
        return quoteLineRepository.findById(cntId).map(quoteLineMapper::toDTO);
    }

//    @Override
//    public QuoteLine create(OrderLine orderLine) {
//
//        QuoteLine quoteLine = new QuoteLine(
//                null,
//                orderLine.getOrdLnQuantity(),
//                orderLine.getArticle().getArtPriceExcludeTaxes(),
//                orderLine.getArticle().getArtName(),
//                orderLine.getArticle().getArtReference(),
//                orderLine.getArticle().getTva().getTvaTaux(),
//                null
//
//        );
//        return quoteLineRepository.save(quoteLine);
//    }

    @Override
    public QuoteLine build(OrderLine orderLine) {
        Article article = orderLine.getArticle();

        QuoteLine line = new QuoteLine();
        line.setQotLnQuantity(orderLine.getOrdLnQuantity());
        line.setQotLnPriceHT(article.getArtPriceExcludeTaxes());
        line.setArticleName(article.getArtName());
        line.setArticleRef(article.getArtReference());
        line.setTvaRate(article.getTva().getTvaTaux());
        // `quote` est renseigné par l'appelant : c'est lui qui connaît le devis.
        return line;
    }

    @Override
    public QuoteLine patchQuantity (Long quoteLineId, PatchQuoteLineQuantity dto) throws ResourceNotFoundException {
        QuoteLine quoteLine = quoteLineRepository.findById(quoteLineId).orElseThrow(() -> new ResourceNotFoundException("Ligne de Devis non existant"));

        quoteLine.setQotLnQuantity(dto.qotLineQuantity());

        return quoteLineRepository.save(quoteLine);
    }

    @Override
    public void delete(Long quoteLineId) throws ResourceNotFoundException {
        QuoteLine quoteLine = quoteLineRepository.findById(quoteLineId).orElseThrow(() -> new ResourceNotFoundException("Ligne de Devis non existant"));

        quoteLineRepository.delete(quoteLine);
    }

}
