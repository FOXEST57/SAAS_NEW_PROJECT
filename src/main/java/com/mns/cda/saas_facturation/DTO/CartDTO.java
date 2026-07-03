package com.mns.cda.saas_facturation.DTO;

import com.mns.cda.saas_facturation.DTO.responseDTO.OrderLineResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public record CartDTO (
        Long crtId,
        String crtRef,
        LocalDateTime crtCreateDate,
        LocalDateTime crtLastModifieDate,
        String crtStatus,
        CustomerDTO customer,
        List<OrderLineResponseDTO> orderLines
){
}
