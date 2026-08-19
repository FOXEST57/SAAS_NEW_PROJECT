package com.mns.cda.saas_facturation.product.DTO;

public record ActualStockDTO(
        Long artId,
        String artName,
        String artRef,
        Long actualStock
){
}
