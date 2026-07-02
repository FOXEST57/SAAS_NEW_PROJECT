package com.mns.cda.saas_facturation.DTO;

import java.time.LocalDateTime;

public record CartDTO (
        Long crtId,
        String crtRef,
        LocalDateTime crtCreateDate,
        LocalDateTime crtLastModifieDate,
        String crtStatus,
        CustomerDTO customer
){
}
