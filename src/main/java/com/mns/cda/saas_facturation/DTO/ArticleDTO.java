package com.mns.cda.saas_facturation.DTO;

import com.mns.cda.saas_facturation.model.Tva;

import java.math.BigDecimal;

// Création d'un DTO qui pour tout ce qui va être affichage et sortie du Back pour le model Article
public record ArticleDTO(
        Long artId,
        String artReference,
        String artName,
        String artDescription,
        BigDecimal artPriceExcludeTaxes,
        int artStock,
        Tva tva,
        BigDecimal artPriceTTC) {
}