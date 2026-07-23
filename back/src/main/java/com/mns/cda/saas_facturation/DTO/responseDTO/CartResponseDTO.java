package com.mns.cda.saas_facturation.DTO.responseDTO;

import java.util.List;

public record CartResponseDTO(
        Long crtId,
        String crtRef,
        String crtStatus,
        List<OrderLineResponseDTO> orderLines

){
}
