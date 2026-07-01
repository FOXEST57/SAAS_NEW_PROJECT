package com.mns.cda.saas_facturation.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long addId;

    protected Integer addNumber;

    protected String addStreet;

    protected String addComplement;

    @ManyToOne
    @NotNull
    protected PostalCode postalCode;

    @ManyToOne
    @NotNull
    protected City city;

}
