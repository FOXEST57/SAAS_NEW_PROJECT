package com.mns.cda.saas_facturation.product.repository;

import com.mns.cda.saas_facturation.product.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findInventoriesByArticle_ArtId(Long articleArtId);
}