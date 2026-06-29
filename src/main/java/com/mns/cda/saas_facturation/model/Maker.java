package com.mns.cda.saas_facturation.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Maker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mkrId;

    @Column(nullable = false, unique = true)
    @NotNull(message = "Le nom du fabriquant est obligatoire")
    private String mkrName;
}
