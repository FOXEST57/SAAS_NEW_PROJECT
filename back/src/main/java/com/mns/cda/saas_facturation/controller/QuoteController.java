package com.mns.cda.saas_facturation.controller;

import com.mns.cda.saas_facturation.DTO.QuoteDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.QuoteRequestDTO;
import com.mns.cda.saas_facturation.DTO.updateDTO.PatchQuoteLineQuantity;
import com.mns.cda.saas_facturation.Iservice.IQuoteService;
import com.mns.cda.saas_facturation.enumeration.QuoteStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/quote")
@Tag(name = "Quote", description = "Routes de gestion des devis.")
@RestController
@CrossOrigin
public class QuoteController {

    private final IQuoteService quoteService;

    @GetMapping("/list")
    public List<QuoteDTO> getQuotes() {
        return quoteService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuoteDTO> getQuoteById(@PathVariable Long id) {
        return new ResponseEntity<>(quoteService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<QuoteDTO> createQuote(@RequestBody QuoteRequestDTO quoteRequestDTO) {
        return new ResponseEntity<>(quoteService.create(quoteRequestDTO), HttpStatus.CREATED);
    }

    @PatchMapping("/quantity/{id}")
    public ResponseEntity<QuoteDTO> updateQuantityQuote(@PathVariable Long id, @RequestBody PatchQuoteLineQuantity quantity) {
        return new ResponseEntity<>(quoteService.updateQuantity(id, quantity), HttpStatus.OK);
    }

    @PatchMapping("/status/{id}")
    public ResponseEntity<QuoteDTO> updateStatusQuote(@PathVariable Long id, @RequestBody QuoteStatus qotStatus) {
        return new ResponseEntity<>(quoteService.updateStatus(id, qotStatus), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuote(@PathVariable Long id) {
        quoteService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
