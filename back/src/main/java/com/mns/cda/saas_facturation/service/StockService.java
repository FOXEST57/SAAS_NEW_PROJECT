package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.enumeration.DeliveryStatus;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.model.*;
import com.mns.cda.saas_facturation.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class StockService {

    private MakerReference makerReference;
    private SupplierReference supplierReference;
    private OrderLine orderLine;
    private final ArticleRepository articleRepository;

    /*
    Stock actuel: Stock actuel présent dans l'entrepôt
    👍Stock en attente de réception: Stock commandé par nous
    Stock commandé: Stock commandé par les clients
    Stock disponible: Stock actuel - stock commandé
    Stock théorique: Stock actuel + stock en attente
    Stock total: Stock actuel - stock commandé + stock en attente
     */

    public int calculStockByStatus(Article article, DeliveryStatus status) {
        int totalStockMarker = article.getMakerReferences() != null ? article.getMakerReferences().stream()
                .filter(makerReference -> makerReference.getStatus() == status)
                .mapToInt(makerReference -> makerReference.getArtMkrStock())
                .sum()
                : 0;


        int totalStockSupplier = article.getSuppliers() != null ? article.getSuppliers().stream()
                .filter(supplierReference -> supplierReference.getStatus() == status)
                .mapToInt(supplierReference -> supplierReference.getSplRefStock())
                .sum()
                : 0;

        return totalStockMarker + totalStockSupplier;
    }

    protected int getPendingStock(Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new ResourceNotFoundException("Article non existant"));

        return calculStockByStatus(article, DeliveryStatus.PENDING);
    }

    protected int getActualStock(Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new ResourceNotFoundException("Article non existant"));

        Inventory lastInventory = article.getInventories()
                .stream()
                .max(Comparator.comparing(Inventory::getInvDate))
                .orElse(null);

        int lastInventoryStock = lastInventory != null ? lastInventory.getInvStock() : 0;
        LocalDateTime lastInventoryDate = lastInventory != null ? lastInventory.getInvDate() : null;

        return 0;

    }


}
