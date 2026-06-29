package com.mns.cda.saas_facturation.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entité JPA représentant un taux de TVA applicable aux articles.
 *
 * <p>Un taux de TVA est composé d'un libellé ({@code tvaName}) et d'une valeur
 * numérique ({@code tvaTaux}). Il est associé à un ou plusieurs articles via
 * une relation {@code @ManyToOne} portée par l'entité {@link Article}.</p>
 *
 * <p>Le taux accepte la valeur {@code 0.0} (inclusive) afin de permettre
 * de représenter une TVA à 0% pour les produits exonérés.</p>
 *
 * <p>Cette entité est retournée directement au client sans couche DTO de sortie dédiée,
 * contrairement à {@link Article} et {@link Supplier} qui utilisent des DTOs.</p>
 *
 * @see Article
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Tva {

    /**
     * Identifiant unique du taux de TVA, généré automatiquement par la base de données.
     * Utilise la stratégie {@code IDENTITY} : la base incrémente elle-même la valeur.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tvaId;

    /**
     * Libellé du taux de TVA (ex : "TVA 20%", "TVA 5.5%", "TVA 0%").
     * Ne peut pas être vide ou composé uniquement d'espaces blancs.
     */
    @NotBlank
    private String tvaName;

    /**
     * Valeur numérique du taux de TVA exprimée en pourcentage (ex : {@code 20.0} pour 20%).
     * Doit être supérieure ou égale à {@code 0.0} — une valeur négative n'est pas autorisée.
     */
    @DecimalMin(value = "0.0")
    private BigDecimal tvaTaux;
}