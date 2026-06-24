package com.mns.cda.saas_facturation.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Tva {

    //L'entité TVA aucun champ les taux ne peuvent être négatif

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tvaId;

    @DecimalMin(value = "0.0")
    private Double tvaTaux;
}
