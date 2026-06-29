package com.mns.cda.saas_facturation.DTO.responseDTO;

import com.mns.cda.saas_facturation.DTO.ArticleDTO;
import com.mns.cda.saas_facturation.DTO.SupplierDTO;

/**
 * DTO de réponse allégé représentant un fournisseur dans le contexte d'une ressource imbriquée.
 *
 * <p>Utilisé lorsqu'un fournisseur est inclus dans la réponse d'une autre ressource (ex: {@link ArticleDTO}),
 * afin d'éviter les boucles de sérialisation JSON infinies causées par les relations bidirectionnelles
 * JPA ({@code Supplier} → {@code Article} → {@code Supplier} → ...).
 *
 * <p>Contrairement au {@link SupplierDTO} complet, ce DTO n'expose que les informations
 * essentielles à l'identification du fournisseur :
 * <ul>
 *   <li>{@code splId} — identifiant unique du fournisseur</li>
 *   <li>{@code splName} — nom du fournisseur</li>
 * </ul>
 */
public record SupplierResponseDTO(
        Long splId,
        String splName
) {
}
