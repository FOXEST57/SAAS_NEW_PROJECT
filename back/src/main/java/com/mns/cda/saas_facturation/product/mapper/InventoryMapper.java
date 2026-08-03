package com.mns.cda.saas_facturation.product.mapper;

import com.mns.cda.saas_facturation.product.DTO.InventoryDTO;
import com.mns.cda.saas_facturation.product.model.Inventory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class InventoryMapper {

    private final ArticleMapper articleMapper;

    public InventoryDTO toDTO(Inventory inventory) {
        return new InventoryDTO(
                inventory.getInvId(),
                inventory.getInvDate(),
                inventory.getInvStock(),
                articleMapper.inventoryToDto(inventory.getArticle())
        );
    }

}
