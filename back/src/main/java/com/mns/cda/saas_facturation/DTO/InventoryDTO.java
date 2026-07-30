package com.mns.cda.saas_facturation.DTO;

import com.mns.cda.saas_facturation.DTO.responseDTO.ArticleResponseInventoryDTO;

import java.time.LocalDateTime;

public record InventoryDTO(
        Long invId,
        LocalDateTime invDate,
        int invStock,
        ArticleResponseInventoryDTO article
) {
}
