package com.mns.cda.saas_facturation.DTO;

import com.mns.cda.saas_facturation.model.Tva;

public record ArticleDTO(
        Long artId,
        String artReference,
        String artName,
        String artDescription,
        Double artPriceExcludeTaxes,
        int artStock,
        Tva tva,
        Double artPriceTTC) {
}