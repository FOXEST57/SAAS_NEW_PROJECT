package com.mns.cda.saas_facturation.model;

import com.mns.cda.saas_facturation.validation.ValidPhoneNumber;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Entité JPA représentant un fournisseur d'articles.
 *
 * <p>Un fournisseur peut être associé à plusieurs articles via une relation
 * {@code @OneToMany} bidirectionnelle — la clé étrangère {@code supplier_id}
 * est portée par la table {@code Article}.</p>
 *
 * <p>L'unicité du nom du fournisseur est garantie par la contrainte
 * {@code unique = true} sur la colonne {@code splName}. Toute tentative
 * d'insertion d'un doublon déclenche une {@code DataIntegrityViolationException}
 * interceptée par le {@code GlobalExceptionInterceptor} en réponse HTTP 409.</p>
 *
 * <p>Le numéro de téléphone est validé via la contrainte custom {@link ValidPhoneNumber}
 * basée sur la bibliothèque Google libphonenumber (format E.164).</p>
 *
 * @see Article
 * @see ValidPhoneNumber
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "SUPPLIER")
public class Supplier {

    /**
     * Identifiant unique du fournisseur, généré automatiquement par la base de données.
     * Utilise la stratégie {@code IDENTITY} : la base incrémente elle-même la valeur.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long splId;

    /**
     * Nom commercial du fournisseur.
     * Limité à 50 caractères, ne peut pas être {@code null} ni vide.
     * Unique en base : deux fournisseurs ne peuvent pas porter le même nom.
     */
    @Column(length = 50, nullable = false, unique = true)
    @NotBlank(message = "Le nom du fournisseur est obligatoire")
    private String splName;

    /**
     * Adresse email du fournisseur.
     * Limité à 50 caractères, ne peut pas être {@code null} ni vide.
     * Doit respecter le format email standard (ex : {@code contact@fournisseur.fr}).
     */
    @Column(length = 50, nullable = false)
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email est mal formaté")
    private String splEmail;

    /**
     * Numéro de téléphone du fournisseur.
     * Limité à 50 caractères, ne peut pas être {@code null} ni vide.
     * Validé par la contrainte custom {@link ValidPhoneNumber} qui vérifie
     * le format E.164 via Google libphonenumber (ex : {@code +33612345678}).
     */
    @Column(length = 50, nullable = false)
    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @ValidPhoneNumber
    private String splPhone;

    /**
     * Adresse postale du fournisseur.
     * Limité à 50 caractères, ne peut pas être {@code null} ni vide.
     */
    @Column(length = 50, nullable = false)
    @NotBlank(message = "L'adresse est obligatoire")
    private String splAddress;

    /**
     * Liste des articles associés à ce fournisseur.
     * Relation {@code @OneToMany} bidirectionnelle mappée par le champ {@code supplier}
     * de l'entité {@link Article}. Initialisée à une liste vide pour éviter
     * les {@code NullPointerException} lors de l'ajout d'articles.
     */
    @OneToMany(mappedBy = "supplier")
    private List<ArticleSupplier> articles ;
}