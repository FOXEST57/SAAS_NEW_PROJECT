package com.mns.cda.saas_facturation.DTO.responseDTO;

import com.mns.cda.saas_facturation.DTO.CustomerDTO;

import java.time.LocalDateTime;

public record CartResponseDTO(
        Long crtId,
        String crtRef,
        LocalDateTime crtCreateDate,
        LocalDateTime crtLastModifieDate,
        String crtStatus,
        CustomerDTO customer
){
}
