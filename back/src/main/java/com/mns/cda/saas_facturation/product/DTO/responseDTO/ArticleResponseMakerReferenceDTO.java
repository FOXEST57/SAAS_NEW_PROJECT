package com.mns.cda.saas_facturation.product.DTO.responseDTO;

import java.util.List;

public record ArticleResponseMakerReferenceDTO(
        Long artId,
        String artName,
        String artReference,
        List<SupplierReferenceResponseArticleDTO> suppliers
){
}
