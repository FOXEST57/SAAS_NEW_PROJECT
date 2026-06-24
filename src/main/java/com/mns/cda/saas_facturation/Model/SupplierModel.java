package com.mns.cda.saas_facturation.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SupplierModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long splId;

    @Column(length = 50, nullable = false)
    private String splName;

    @Column(length = 50, nullable = false, unique = true)
    @NotBlank(message = "L'email est obligatoire")
    private String splEmail;

    @Column(length = 50, nullable = false, unique = true)
    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    private String splPhone;

    @Column(length = 50, nullable = false)
    @NotBlank(message = "L'adresse est obligatoire")
    private String splAdress;


}
