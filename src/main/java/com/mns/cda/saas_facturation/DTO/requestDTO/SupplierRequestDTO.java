package com.mns.cda.saas_facturation.DTO.requestDTO;

import com.mns.cda.saas_facturation.validation.ValidPhoneNumber;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO d'entrée représentant les données reçues par l'API pour créer ou modifier un fournisseur.
 *
 * <p>Ce record est utilisé exclusivement en <strong>entrée</strong> (requête HTTP) :
 * il encapsule et valide les données envoyées par le client avant qu'elles
 * n'atteignent la couche service.</p>
 *
 * <p>Les contraintes appliquées sur chaque champ sont vérifiées automatiquement
 * par Spring grâce à l'annotation {@code @Valid} présente dans le contrôleur.
 * En cas d'échec, le {@code GlobalExceptionInterceptor} retourne un 400
 * avec le détail des champs invalides.</p>
 *
 * <p>Le numéro de téléphone est validé via une contrainte custom {@link ValidPhoneNumber}
 * basée sur la bibliothèque Google libphonenumber, qui vérifie le format E.164
 * (ex : {@code +33612345678}).</p>
 *
 * @param name        nom du fournisseur — ne doit pas être vide
 * @param email       adresse splEmail du fournisseur — ne doit pas être vide et doit être au format valide
 * @param phoneNumber numéro de téléphone du fournisseur — ne doit pas être vide et doit respecter le format E.164
 * @param address     adresse postale du fournisseur — ne doit pas être vide
 *
 * @see ValidPhoneNumber
 * @see com.mns.cda.saas_facturation.controller.SupplierController
 * @see com.mns.cda.saas_facturation.Iservice.ISupplierService
 */
public record SupplierRequestDTO(
        @NotBlank @Column(unique = true) String name,
        @NotBlank @Email String email,
        @NotBlank @ValidPhoneNumber String phoneNumber,
        @NotBlank String address
)
{}
