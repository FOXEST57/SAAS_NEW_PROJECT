package com.mns.cda.saas_facturation.cart.DTO;

public record ActualStockDTO(
        Long artId,
        String artName,
        String artRef,
        int actualStock
){
}
