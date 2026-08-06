package com.mns.cda.saas_facturation.cart.controller;


import com.mns.cda.saas_facturation.cart.DTO.ActualStockDTO;
import com.mns.cda.saas_facturation.cart.repository.QuoteLineRepository;
import com.mns.cda.saas_facturation.product.service.StockService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stock")
@Tag(name = "Stock", description = "Routes de gestion des stocks.")
@CrossOrigin
public class StockController {

    private final StockService stockService;
    private final QuoteLineRepository quoteLineRepository;

    @GetMapping("/pending/{artId}")
    public ResponseEntity<Integer> getPendingStockByArticle(@PathVariable Long artId) {
        return new ResponseEntity<>(stockService.getPendingStockByArticle(artId), HttpStatus.OK);
    }

    @GetMapping("/actual/list")
    public List<ActualStockDTO> getAllActualStock(){

        return quoteLineRepository.getActualStock();
    }

    @GetMapping("/actual/{artId}")
    public ResponseEntity<Integer> getActualStockByArticle(@PathVariable Long artId) {
            return new ResponseEntity<>(stockService.getActualStockByArticle(artId), HttpStatus.OK);
    }

    @GetMapping("/ordered/{artId}")
    public ResponseEntity<Integer> getOrderedStockByArticle(@PathVariable Long artId) {
        return new ResponseEntity<>(stockService.getOrderedStockByArticle(artId), HttpStatus.OK);
    }

    @GetMapping("/available/{artId}")
    public ResponseEntity<Integer> getAvailableStockByArticle(@PathVariable Long artId) {
        return new ResponseEntity<>(stockService.getAvailableStockByArticle(artId), HttpStatus.OK);
    }

    @GetMapping("/theoretical/{artId}")
    public ResponseEntity<Integer> getTheoreticalStockByArticle(@PathVariable Long artId) {
        return new ResponseEntity<>(stockService.getTheoreticalStockByArticle(artId), HttpStatus.OK);
    }

    @GetMapping("/total/{artId}")
    public ResponseEntity<Integer> getTotalStockByArticle(@PathVariable Long artId) {
        return new ResponseEntity<>(stockService.getTotalStockByArticle(artId), HttpStatus.OK);
    }

}
