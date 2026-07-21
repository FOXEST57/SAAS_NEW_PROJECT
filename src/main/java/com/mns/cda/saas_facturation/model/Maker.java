package com.mns.cda.saas_facturation.model;

import com.mns.cda.saas_facturation.validation.ValidPhoneNumber;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "Le nom du fabriquant est obligatoire")
    private String mkrName;

    /**
     * Adresse Email du fabricant.
     * Limité à 50 caractères, ne peut pas être {@code null} ni vide.
     * Doit respecter le format Email standard (ex : {@code contact@fabricant.fr}).
     */
    @Column(length = 50, nullable = false)
    @NotBlank(message = "L'Email est obligatoire")
    @Email(message = "L'Email est mal formaté")
    private String mkrEmail;

    /**
     * Numéro de téléphone du fabricant.
     * Limité à 50 caractères, ne peut pas être {@code null} ni vide.
     * Validé par la contrainte custom {@link ValidPhoneNumber} qui vérifie
     * le format E.164 via Google libphonenumber (ex : {@code +33612345678}).
     */
    @Column(length = 50, nullable = false)
    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @ValidPhoneNumber
    private String mkrPhone;

    @ManyToOne
    @JoinColumn(name = "address_id")
    @NotNull
    protected Address address;
}
