package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.QuoteLineDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.QuoteLineRequestDTO;
import com.mns.cda.saas_facturation.Iservice.IQuoteLineService;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.mapper.QuoteLineMapper;
import com.mns.cda.saas_facturation.model.Quote;
import com.mns.cda.saas_facturation.model.QuoteLine;
import com.mns.cda.saas_facturation.repository.QuoteLineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QuoteLineService implements IQuoteLineService {

    private final QuoteLineRepository quoteLineRepository;
    private final QuoteLineMapper quoteLineMapper;
    private final Quote

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
    public QuoteLineDTO create(QuoteLineRequestDTO dto) {
        Quote quote = ;

        QuoteLine quoteLine = new QuoteLine(
                dto.qotLnQuantity(),
                dto.qotLnPriceHT(),


        );

        return quoteLineMapper.toDTO(quoteLineRepository.save(quoteLine));
    }

    @Override
    public QuoteLineDTO update(Long cntId, QuoteLineRequestDTO dto) throws ResourceNotFoundException {
        QuoteLine quoteLine = quoteLineRepository.findById(cntId).orElseThrow(() -> new ResourceNotFoundException("Pays non existant"));

        quoteLine.setCntName(dto.cntName());

        return quoteLineMapper.toDTO(quoteLineRepository.save(quoteLine));
    }

    @Override
    public void delete(Long cntId) throws ResourceNotFoundException {
        QuoteLine quoteLine = quoteLineRepository.findById(cntId).orElseThrow(() -> new ResourceNotFoundException("Pays non existant"));

        quoteLineRepository.delete(quoteLine);
    }

}
