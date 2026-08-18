package com.mns.cda.saas_facturation.product.service;

import com.mns.cda.saas_facturation.cart.repository.QuoteLineRepository;
import com.mns.cda.saas_facturation.enumeration.DeliveryStatus;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.product.model.*;
import com.mns.cda.saas_facturation.product.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final ArticleRepository articleRepository;
    private final QuoteLineRepository quoteLineRepository;

    /*
    Notion -> Définition | Source | Sert à
    Physique -> Ce qui est réellement en entrepôt | Mouvements | Inventaire, litiges
    Attendu -> Commandé aux fournisseurs, pas encore reçu | Lignes fournisseur `PENDING` / `ACCEPTED` | Suivi des appros
    Réservé -> Engagé auprès des clients, pas encore sorti | Devis acceptés non livrés | Ne pas vendre deux fois
    Disponible -> Physique − Réservé | Calcul | Le chiffre du commercial
    Théorique -> Physique + Attendu | Calcul | Ce qu'on aura si tout arrive
    Projeté -> Physique + Attendu − Réservé | Calcul | Faut-il réapprovisionner?
     */

    /*
    👍Stock actuel: Stock actuel présent dans l'entrepôt // retirer ce qui est commandé à partir de la facture
    👍Stock en attente de réception: Stock commandé par nous
    👍Stock commandé: Stock commandé par les clients // à partir de la commande
    👍Stock disponible: Stock actuel - stock commandé // retirer à partir de la commande
    👍Stock théorique: Stock actuel + stock en attente
    👍Stock total: Stock actuel - stock commandé + stock en attente // retirer ce qui est en devis + ce qui est en cours livraison status Accepted ou Pending
    */

    public int calculStockByStatus(Article article, DeliveryStatus status, LocalDateTime inventoryDate) {
        switch (status) {
            case DeliveryStatus.RECEIVED -> {
                int totalStockMaker = article.getMakerReferences() != null
                        ? article.getMakerReferences()
                        .stream()
                        .map(MakerReference::getDeliveries)
                        .map(deliveries -> deliveries
                                .stream()
                                .filter(delivery -> delivery.getDlvStatus() == status
                                        && delivery.getDlvUpdatedDate().isAfter(inventoryDate))
                                .mapToInt(Delivery::getDlvQuantity)
                                .sum()
                        )
                        .mapToInt(Integer::intValue)
                        .sum()
                        : 0;
                int totalStockSupplier = article.getSupplierReferences() != null
                        ? article.getSupplierReferences()
                        .stream()
                        .map(SupplierReference::getDeliveries)
                        .map(deliveries -> deliveries
                                .stream()
                                .filter(delivery -> delivery.getDlvStatus() == status
                                        && delivery.getDlvUpdatedDate().isAfter(inventoryDate))
                                .mapToInt(Delivery::getDlvQuantity)
                                .sum()
                        )
                        .mapToInt(Integer::intValue)
                        .sum()
                        : 0;
                return totalStockMaker + totalStockSupplier;
            }
            case DeliveryStatus.PENDING -> {
                int totalStockMarker = article.getMakerReferences() != null
                        ? article.getMakerReferences()
                        .stream()
                        .map(MakerReference::getDeliveries)
                        .map(deliveries -> deliveries
                                .stream()
                                .filter(delivery -> delivery.getDlvStatus() == status)
                                .mapToInt(Delivery::getDlvQuantity)
                                .sum()
                        )
                        .mapToInt(Integer::intValue)
                        .sum()
                        : 0;
                int totalStockSupplier = article.getSupplierReferences() != null
                        ? article.getSupplierReferences()
                        .stream()
                        .map(SupplierReference::getDeliveries)
                        .map(deliveries -> deliveries
                                .stream()
                                .filter(delivery -> delivery.getDlvStatus() == status)
                                .mapToInt(Delivery::getDlvQuantity)
                                .sum()
                        )
                        .mapToInt(Integer::intValue)
                        .sum()
                        : 0;
                return totalStockMarker + totalStockSupplier;
            }
            default -> {
                return 0;
            }
        }
    }

    public int getPendingStockByArticle(Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new ResourceNotFoundException("Article non existant"));

        return calculStockByStatus(article, DeliveryStatus.PENDING, null);
    }
    
    public int getActualStockByArticle(Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new ResourceNotFoundException("Article non existant"));

        Inventory lastInventory = article.getInventories()
                .stream()
                .max(Comparator.comparing(Inventory::getInvDate))
                .orElse(null);

        // Récupération du dernier inventaire (quantité + date)
        int lastInventoryStock = lastInventory != null ? lastInventory.getInvStock() : 0;
        LocalDateTime lastInventoryDate = lastInventory != null ? lastInventory.getInvDate() : LocalDateTime.of(1900,1,1, 0,0,0);

        // Calcul du stock arrivé entre temps (commandes reçues des fournisseurs et fabricants)
        int orderedStock = calculStockByStatus(article, DeliveryStatus.RECEIVED, lastInventoryDate);

        // Récupération des commandes clients qui ont été validées (donc article parti)
        List<Integer> quoteLines = quoteLineRepository.getSentQuantityByArticle(lastInventoryDate, article.getArtReference());
        int deliveredStock = quoteLines.stream().reduce(0 , Integer::sum);

        return lastInventoryStock + orderedStock - deliveredStock;

    }

    public int getOrderedStockByArticle(Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new ResourceNotFoundException("Article non existant"));

        return quoteLineRepository
                .getOrderedQuantityByArticle(article.getArtReference())
                .stream()
                .reduce(0 , Integer::sum);
    }

    public int getAvailableStockByArticle(Long articleId) {
        return getActualStockByArticle(articleId) - getOrderedStockByArticle(articleId);
    }

    public int getTheoreticalStockByArticle(Long articleId) {
        return getActualStockByArticle(articleId) + getPendingStockByArticle(articleId);
    }

    public int getTotalStockByArticle(Long articleId) {
        return getActualStockByArticle(articleId) + getPendingStockByArticle(articleId) - getOrderedStockByArticle(articleId);
    }

}
