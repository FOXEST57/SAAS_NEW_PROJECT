package com.mns.cda.saas_facturation.DTO.responseDTO;

import java.util.List;

public record ArticleResponseMakerReferenceDTO(
        Long artId,
        String artName,
        String artReference,
        List<SupplierReferenceResponseDTO> suppliers
){
}
