package com.mns.cda.saas_facturation.repository;

import com.mns.cda.saas_facturation.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findInventoriesByArticle_ArtId(Long articleArtId);
}