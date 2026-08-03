package com.mns.cda.saas_facturation.product.DTO;

import com.mns.cda.saas_facturation.product.DTO.responseDTO.ArticleResponseInventoryDTO;

import java.time.LocalDateTime;

public record InventoryDTO(
        Long invId,
        LocalDateTime invDate,
        int invStock,
        ArticleResponseInventoryDTO article
) {
}
