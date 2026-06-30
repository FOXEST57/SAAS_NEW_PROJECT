package com.mns.cda.saas_facturation.DTO;

import com.mns.cda.saas_facturation.DTO.responseDTO.ArticleResponseSupplierDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.SupplierResponseDTO;
import com.mns.cda.saas_facturation.model.ArticleSupplier;

public record ArticleSupplierDTO(
        ArticleSupplier.ArticleSupplierId artSplId,
        ArticleResponseSupplierDTO article,
        SupplierResponseDTO supplier,
        String artSplReference,
        int artSplStock
) {
}
