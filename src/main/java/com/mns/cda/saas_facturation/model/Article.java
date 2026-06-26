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

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Article {

    //L'entité Article aucun champ ne peut être null ou vide quand c'est du String et pour les nombres ils ne peuvent pas être négatif ou = 0 pour le prix avant taxe

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long artId;

    @NotBlank
    protected String artReference;

    @NotBlank
    protected String artName;

    @NotNull
    protected String artDescription;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    protected BigDecimal artPriceExcludeTaxes;

    @NotNull
    @Min(0)
    protected int artStock;

    @ManyToOne
    @JoinColumn(name = "tva_id")
    protected Tva tva;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = true)
    protected Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true)
    protected Category category;
}
