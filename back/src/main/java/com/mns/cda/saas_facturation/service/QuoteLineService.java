package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.QuoteLineDTO;
import com.mns.cda.saas_facturation.DTO.updateDTO.PatchQuoteLineQuantity;
import com.mns.cda.saas_facturation.Iservice.IQuoteLineService;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.mapper.QuoteLineMapper;
import com.mns.cda.saas_facturation.model.OrderLine;
import com.mns.cda.saas_facturation.model.QuoteLine;
import com.mns.cda.saas_facturation.repository.QuoteLineRepository;
import com.mns.cda.saas_facturation.repository.QuoteRepository;
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

    @Override
    public QuoteLine create(OrderLine orderLine) {

        QuoteLine quoteLine = new QuoteLine(
                null,
                orderLine.getOrdLnQuantity(),
                orderLine.getArticle().getArtPriceExcludeTaxes(),
                orderLine.getArticle().getArtName(),
                orderLine.getArticle().getArtReference(),
                orderLine.getArticle().getTva().getTvaTaux(),
                null

        );
        return quoteLineRepository.save(quoteLine);
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
