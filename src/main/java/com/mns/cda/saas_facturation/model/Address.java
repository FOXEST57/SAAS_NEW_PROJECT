package com.mns.cda.saas_facturation.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long addId;

    protected String addNumber;

    protected String addStreet;

    protected String addComplement;

    @ManyToOne
    @JoinColumn(name = "pcode_id")
    @NotNull
    protected PostalCode postalCode;

    @ManyToOne
    @JoinColumn(name = "city_id")
    @NotNull
    protected City city;

}
