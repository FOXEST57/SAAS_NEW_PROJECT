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

    private final ArticleRepository articleRepository;

    /*
    Stock actuel: Stock actuel présent dans l'entrepôt // retirer ce qui est commandé à partir de la facture
    👍Stock en attente de réception: Stock commandé par nous
    Stock commandé: Stock commandé par les clients // à partir du devis
    Stock disponible: Stock actuel - stock commandé // retirer à partir du devis
    Stock théorique: Stock actuel + stock en attente
    Stock total: Stock actuel - stock commandé + stock en attente // retirer ce qui est en devis + ce qui est en cours livraison status Accepted ou Pending
    */

    public int calculStockByStatus(Article article, DeliveryStatus status, LocalDateTime inventoryDate) {

        switch (status) {
            case DeliveryStatus.RECEIVED -> {
                int totalStockMarker = article.getMakerReferences() != null ? article.getMakerReferences().stream()
                        .filter(makerReference -> makerReference.getStatus() == status
                                && makerReference.getArtMrkUpdateDate().isAfter(inventoryDate))
                        .mapToInt(MakerReference::getArtMkrStock)
                        .sum()
                        : 0;
                int totalStockSupplier = article.getSuppliers() != null ? article.getSuppliers().stream()
                        .filter(supplierReference -> supplierReference.getStatus() == status
                                && supplierReference.getSplRefUpdateDate().isAfter(inventoryDate))
                        .mapToInt(SupplierReference::getSplRefStock)
                        .sum()
                        : 0;
                return totalStockMarker + totalStockSupplier;
            }
            case DeliveryStatus.PENDING -> {
                int totalStockMarker = article.getMakerReferences() != null ? article.getMakerReferences().stream()
                        .filter(makerReference -> makerReference.getStatus() == status)
                        .mapToInt(MakerReference::getArtMkrStock)
                        .sum()
                        : 0;
                int totalStockSupplier = article.getSuppliers() != null ? article.getSuppliers().stream()
                        .filter(supplierReference -> supplierReference.getStatus() == status)
                        .mapToInt(SupplierReference::getSplRefStock)
                        .sum()
                        : 0;
                return totalStockMarker + totalStockSupplier;
            }
            default -> {
                return 0;
            }
        }
    }

    protected int getPendingStock(Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new ResourceNotFoundException("Article non existant"));

        return calculStockByStatus(article, DeliveryStatus.PENDING, null);
    }
    
    protected int getTheoreticalStock(Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new ResourceNotFoundException("Article non existant"));

        Inventory lastInventory = article.getInventories()
                .stream()
                .max(Comparator.comparing(Inventory::getInvDate))
                .orElse(null);

        int lastInventoryStock = lastInventory.getInvStock();
        LocalDateTime lastInventoryDate = lastInventory.getInvDate();

        int orderedStock = calculStockByStatus(article, DeliveryStatus.RECEIVED, lastInventoryDate);
        //TODO retirer ce qui est parti
        return lastInventoryStock + orderedStock;

    }


}
