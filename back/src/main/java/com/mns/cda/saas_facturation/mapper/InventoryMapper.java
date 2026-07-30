package com.mns.cda.saas_facturation.mapper;

import com.mns.cda.saas_facturation.DTO.InventoryDTO;
import com.mns.cda.saas_facturation.model.Inventory;
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
