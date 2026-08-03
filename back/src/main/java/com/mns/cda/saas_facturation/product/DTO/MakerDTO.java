package com.mns.cda.saas_facturation.product.DTO;

import com.mns.cda.saas_facturation.location.DTO.AddressDTO;

public record MakerDTO (
        Long mkrId,
        String mkrName,
        String mkrEmail,
        String mkrPhone,
        AddressDTO address
){
}
