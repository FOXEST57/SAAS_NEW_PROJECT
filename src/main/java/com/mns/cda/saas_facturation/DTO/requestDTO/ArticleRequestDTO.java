package com.mns.cda.saas_facturation.DTO.requestDTO;

import com.mns.cda.saas_facturation.model.ArticleSupplier;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO d'entrée représentant les données reçues par l'API pour créer ou modifier un article.
 *
 * <p>Ce record est utilisé exclusivement en <strong>entrée</strong> (requête HTTP) :
 * il encapsule et valide les données envoyées par le client avant qu'elles
 * n'atteignent la couche service.</p>
 *
 * <p>Les contraintes Bean Validation appliquées sur chaque champ sont vérifiées
 * automatiquement par Spring grâce à l'annotation {@code @Valid} présente dans
 * le contrôleur. En cas d'échec, le {@code GlobalExceptionInterceptor} retourne
 * un 400 avec le détail des champs invalides.</p>
 *
 * <p>Ce DTO ne contient pas d'identifiant ({@code artId}) car celui-ci est
 * généré par la base de données lors de la création, et fourni dans l'URL
 * lors d'une modification.</p>
 *
 * <p>Les entités liées (TVA et fournisseur) sont référencées par leur identifiant
 * uniquement — c'est le service qui se charge de les récupérer en base.</p>
 *
 * @param artReference          référence commerciale de l'article — ne doit pas être vide
 * @param artName               nom de l'article — ne doit pas être vide
 * @param artDescription        description de l'article — ne doit pas être vide
 * @param artPriceExcludeTaxes  prix hors taxes — doit être strictement supérieur à 0
 * @param artStock              quantité en stock — doit être supérieure ou égale à 0
 * @param tvaId                 identifiant de la TVA à associer — obligatoire, doit être supérieur ou égal à 1
 * @param supplierId            identifiant du fournisseur à associer — doit être un nombre positif
 *
 * @see com.mns.cda.saas_facturation.controller.ArticleController
 * @see com.mns.cda.saas_facturation.Iservice.IArticleService
 */

public record ArticleRequestDTO(
        @NotBlank @Column(unique = true) String artReference,
        @NotBlank String artName,
        @NotBlank String artDescription,
        @DecimalMin(value = "0.0", inclusive = false) BigDecimal artPriceExcludeTaxes,
        @Min(0) int artStock,
        @NotNull @Min(1) Long tvaId,
        @Positive Long categoryId,
        List<ArticleSupplierRequestDTO> suppliers
) {}