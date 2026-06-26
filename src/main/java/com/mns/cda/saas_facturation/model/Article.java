package com.mns.cda.saas_facturation.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entité JPA représentant un article du catalogue produits.
 *
 * <p>Un article est le cœur du système de facturation. Il agrège plusieurs
 * relations vers d'autres entités :</p>
 * <ul>
 *   <li>{@link Tva} — taux de TVA applicable pour le calcul du prix TTC</li>
 *   <li>{@link Supplier} — fournisseur de l'article (optionnel)</li>
 *   <li>{@link Category} — catégorie de classement de l'article (optionnelle)</li>
 * </ul>
 *
 * <p>Les contraintes de validation Bean ({@code @NotBlank}, {@code @NotNull}, etc.)
 * sont appliquées à deux niveaux :</p>
 * <ul>
 *   <li>au niveau JPA/base de données via les contraintes de colonne</li>
 *   <li>au niveau applicatif via Bean Validation lors de la désérialisation des DTOs</li>
 * </ul>
 *
 * <p>Le prix TTC n'est pas stocké en base — il est calculé dynamiquement
 * par le service à partir de {@code artPriceExcludeTaxes} et du taux {@code tva}.</p>
 *
 * @see Tva
 * @see Supplier
 * @see Category
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Article {

    /**
     * Identifiant unique de l'article, généré automatiquement par la base de données.
     * Utilise la stratégie {@code IDENTITY} : la base incrémente elle-même la valeur.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long artId;

    /**
     * Référence commerciale unique de l'article (ex : "REF-001").
     * La contrainte {@code unique = true} garantit l'unicité en base de données.
     */
    @NotBlank
    @Column(unique = true)
    protected String artReference;

    /** Nom de l'article — ne peut pas être vide ou composé uniquement d'espaces. */
    @NotBlank
    protected String artName;

    /** Description détaillée de l'article — ne peut pas être {@code null}. */
    @NotNull
    protected String artDescription;

    /**
     * Prix hors taxes de l'article.
     * Doit être strictement supérieur à 0 ({@code inclusive = false}).
     * Utilisé par le service pour calculer le prix TTC avec le taux de TVA associé.
     */
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    protected BigDecimal artPriceExcludeTaxes;

    /**
     * Quantité disponible en stock.
     * Doit être supérieure ou égale à 0 — une valeur négative n'est pas autorisée.
     */
    @NotNull
    @Min(0)
    protected int artStock;

    /**
     * Taux de TVA associé à cet article.
     * Relation {@code @ManyToOne} : plusieurs articles peuvent partager le même taux de TVA.
     * La colonne de jointure en base est {@code tva_id}.
     */
    @ManyToOne
    @JoinColumn(name = "tva_id")
    protected Tva tva;

    /**
     * Fournisseur associé à cet article.
     * Relation {@code @ManyToOne} optionnelle : un article peut exister sans fournisseur
     * ({@code nullable = true}).
     * La colonne de jointure en base est {@code supplier_id}.
     */
    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = true)
    protected Supplier supplier;

    /**
     * Catégorie de classement de l'article.
     * Relation {@code @ManyToOne} optionnelle : un article peut exister sans catégorie
     * ({@code nullable = true}).
     * La colonne de jointure en base est {@code category_id}.
     */
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true)
    protected Category category;
}