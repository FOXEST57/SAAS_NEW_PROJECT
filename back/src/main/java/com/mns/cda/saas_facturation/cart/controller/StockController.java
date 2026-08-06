package com.mns.cda.saas_facturation.cart.controller;


import com.mns.cda.saas_facturation.cart.DTO.ActualStockDTO;
import com.mns.cda.saas_facturation.cart.repository.QuoteLineRepository;
import com.mns.cda.saas_facturation.product.model.Article;
import com.mns.cda.saas_facturation.product.model.Inventory;
import com.mns.cda.saas_facturation.product.repository.ArticleRepository;
import com.mns.cda.saas_facturation.product.service.StockService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stock")
@Tag(name = "Stock", description = "Routes de gestion des stocks.")
@CrossOrigin
public class StockController {

    private final StockService stockService;
    private final ArticleRepository articleRepository;
    private final QuoteLineRepository quoteLineRepository;

    @GetMapping("/pending/{artId}")
    public ResponseEntity<Integer> getPendingStockByArticle(@PathVariable Long artId) {
        return new ResponseEntity<>(stockService.getPendingStock(artId), HttpStatus.OK);
    }

//    @GetMapping("/list")
//    public List<ActualStockDTO> getAllActualStock() {
//        List<Article> articles = articleRepository.findAll();
//        List<ActualStockDTO> actualStocks = new ArrayList<>();
//        articles.forEach(article -> {
//            int stock = stockService.getActualStock(article.getArtId());
//            ActualStockDTO actualStockDTO = new ActualStockDTO(
//                    article.getArtId(),
//                    article.getArtName(),
//                    article.getArtReference(),
//                    stock
//            );
//            actualStocks.add(actualStockDTO);
//        });
//        return actualStocks;
//    }
    @GetMapping("/list")
    public List<ActualStockDTO> getAllActualStock(){

        return quoteLineRepository.getActualStock();
    }

    @GetMapping("/{artId}")
    public ResponseEntity<Integer> getActualStockByArticle(@PathVariable Long artId) {
            return new ResponseEntity<>(stockService.getActualStock(artId), HttpStatus.OK);
    }

}
