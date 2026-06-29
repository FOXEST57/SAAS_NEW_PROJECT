package com.mns.cda.saas_facturation.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Entité JPA représentant une catégorie d'articles.
 *
 * <p>Les catégories sont organisées en structure arborescente auto-référentielle :
 * une catégorie peut avoir une catégorie parente ({@code catParent}) et plusieurs
 * catégories enfants ({@code catChildren}), permettant ainsi de modéliser
 * une hiérarchie de catégories sur plusieurs niveaux.</p>
 *
 * <p>Exemple de hiérarchie :</p>
 * <pre>
 * Informatique (catParent = null)
 * ├── Ordinateurs portables (catParent = Informatique)
 * └── Périphériques (catParent = Informatique)
 *     └── Claviers (catParent = Périphériques)
 * </pre>
 *
 * <p>Une catégorie racine (premier niveau) a {@code catParent} à {@code null}.</p>
 *
 * @see Article
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Category {

    /**
     * Identifiant unique de la catégorie, généré automatiquement par la base de données.
     * Utilise la stratégie {@code IDENTITY} : la base incrémente elle-même la valeur.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long catId;

    /**
     * Nom de la catégorie (ex : "Informatique", "Bureautique").
     * Ne peut pas être vide ou composé uniquement d'espaces blancs.
     */
    @NotBlank
    @Column(unique = true)
    protected String catName;

    /**
     * Catégorie parente de cette catégorie.
     * Relation {@code @ManyToOne} auto-référentielle optionnelle :
     * si {@code null}, cette catégorie est une catégorie racine (premier niveau).
     * La colonne de jointure en base est {@code cat_parent_id}.
     */
    @ManyToOne
    @JoinColumn(name = "cat_parent_id", nullable = true)
    protected Category catParent;

    /**
     * Liste des catégories enfants de cette catégorie.
     * Relation {@code @OneToMany} bidirectionnelle mappée par le champ {@code catParent}
     * de l'entité enfant. Une catégorie sans enfants retourne une liste vide.
     */
    @OneToMany(mappedBy = "catParent")
    protected List<Category> catChildren;
}