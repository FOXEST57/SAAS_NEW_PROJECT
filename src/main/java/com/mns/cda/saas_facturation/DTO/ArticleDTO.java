package com.mns.cda.saas_facturation.DTO;

import com.mns.cda.saas_facturation.DTO.responseDTO.ArticleSupplierResponseDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.CategoryResponseDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.SupplierResponseDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.TvaResponseDTO;
import com.mns.cda.saas_facturation.model.Tva;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO de sortie représentant un article retourné par l'API au client.
 *
 * <p>Ce record est utilisé exclusivement en <strong>sortie</strong> (réponse HTTP) :
 * il encapsule les données d'un article à afficher côté frontend, sans exposer
 * directement l'entité JPA {@code Article}.</p>
 *
 * <p>Il agrège des informations provenant de plusieurs entités liées :</p>
 * <ul>
 *   <li>la TVA associée via {@link Tva}</li>
 *   <li>le fournisseur associé via {@link SupplierResponseDTO}</li>
 * </ul>
 *
 * <p>Le prix TTC ({@code artPriceTTC}) est calculé côté service à partir du prix
 * hors taxes et du taux de TVA — il n'est pas stocké directement en base de données.</p>
 *
 * @param artId                 identifiant unique de l'article généré par la base de données
 * @param artReference          référence commerciale de l'article (ex : "REF-001")
 * @param artName               nom de l'article
 * @param artDescription        description détaillée de l'article
 * @param artPriceExcludeTaxes  prix hors taxes de l'article
 * @param artStock              quantité disponible en stock
 * @param tva                   taux de TVA applicable à l'article
 * @param artPriceTTC           prix toutes taxes comprises, calculé par le service
 * @param suppliers              informations du fournisseur associé à l'article
 *
 * @see Tva
 * @see SupplierResponseDTO
 */
public record ArticleDTO(
        Long artId,
        String artReference,
        String artName,
        String artDescription,
        BigDecimal artPriceExcludeTaxes,
        int artStock,
        TvaResponseDTO tva,
        BigDecimal artPriceTTC,
        CategoryResponseDTO category,
        List<ArticleSupplierResponseDTO> suppliers) {
}