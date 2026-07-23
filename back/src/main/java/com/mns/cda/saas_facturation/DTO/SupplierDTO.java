package com.mns.cda.saas_facturation.DTO;

import com.mns.cda.saas_facturation.model.Supplier;

/**
 * DTO (Data Transfer Object) représentant un fournisseur exposé par l'API.
 *
 * <p>Utilisé en réponse des endpoints fournisseur pour éviter d'exposer
 * directement l'entité JPA {@link Supplier} et ses relations (ex: liste d'articles).
 *
 * <p>Champs exposés :
 * <ul>
 *   <li>{@code splId} — identifiant unique du fournisseur en base de données</li>
 *   <li>{@code splName} — nom du fournisseur</li>
 *   <li>{@code splEmail} — adresse splEmail du fournisseur</li>
 *   <li>{@code splPhone} — numéro de téléphone au format international E.164 (ex: +33612345678)</li>
 *   <li>{@code splAddress} — adresse postale du fournisseur</li>
 * </ul>
 */
public record SupplierDTO(
        Long splId,
        String splName,
        String splEmail,
        String splPhone,
        AddressDTO address
) {
}
