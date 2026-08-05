package com.mns.cda.saas_facturation.product.service;

import com.mns.cda.saas_facturation.cart.repository.QuoteLineRepository;
import com.mns.cda.saas_facturation.enumeration.DeliveryStatus;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.product.model.Article;
import com.mns.cda.saas_facturation.product.model.Inventory;
import com.mns.cda.saas_facturation.product.model.MakerReference;
import com.mns.cda.saas_facturation.product.model.SupplierReference;
import com.mns.cda.saas_facturation.product.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static java.lang.Integer.sum;

@Service
@RequiredArgsConstructor
public class StockService {

    private final ArticleRepository articleRepository;
    private final QuoteLineRepository quoteLineRepository;

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
                int totalStockMarker = article.getMakerReferences() != null
                        ? article.getMakerReferences()
                        .stream()
                        .filter(makerReference -> makerReference.getStatus() == status
                                && makerReference.getArtMrkUpdateDate().isAfter(inventoryDate))
                        .mapToInt(MakerReference::getArtMkrStock)
                        .sum()
                        : 0;
                int totalStockSupplier = article.getSuppliers() != null
                        ? article.getSuppliers()
                        .stream()
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

    public int getPendingStock(Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new ResourceNotFoundException("Article non existant"));

        return calculStockByStatus(article, DeliveryStatus.PENDING, null);
    }
    
    public int getActualStock(Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new ResourceNotFoundException("Article non existant"));

        Inventory lastInventory = article.getInventories()
                .stream()
                .max(Comparator.comparing(Inventory::getInvDate))
                .orElse(null);

        // Récupération du dernier inventaire (quantité + date)
        int lastInventoryStock = lastInventory != null ? lastInventory.getInvStock() : 0;
        LocalDateTime lastInventoryDate = lastInventory != null ? lastInventory.getInvDate() : LocalDateTime.of(1900,1,1, 0,0,0);

        // Calcul du stock arrivé entre temps (commandes reçues des fournisseurs et fabricants)
        //int orderedStock = calculStockByStatus(article, DeliveryStatus.RECEIVED, lastInventoryDate);

        // Récupération des commandes clients qui ont été validées (donc article parti)
        List<Integer> quoteLines = quoteLineRepository.getSentQuantityByArticle(lastInventoryDate, article.getArtReference());
        int deliveredStock = quoteLines.stream().reduce(0 , Integer::sum);

        return lastInventoryStock - deliveredStock;

    }


}
