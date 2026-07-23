package com.mns.cda.saas_facturation.DTO;

public record MakerDTO (
        Long mkrId,
        String mkrName,
        String mkrEmail,
        String mkrPhone,
        AddressDTO address
){
}
