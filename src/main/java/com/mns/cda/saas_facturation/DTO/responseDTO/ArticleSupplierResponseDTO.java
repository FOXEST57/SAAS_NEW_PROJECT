package com.mns.cda.saas_facturation.DTO.responseDTO;

import com.mns.cda.saas_facturation.model.ArticleSupplier;

public record ArticleSupplierResponseDTO(
        ArticleSupplier.ArticleSupplierId artSplId,
        SupplierResponseDTO supplier,
        String artSplReference,
        int artSplStock
) {
}
