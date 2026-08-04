package com.mns.cda.saas_facturation.cart.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class InvoiceLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long invLnId;

    @NotNull
    @Min(1)
    protected int invLnQuantity;

    @NotNull
    @Min(0)
    protected BigDecimal invLnPriceHT;

    @NotBlank
    protected String articleName;

    @NotBlank
    protected String articleRef;

    @NotNull
    protected BigDecimal tvaRate;

    @ManyToOne
    @NotNull
    protected Invoice invoice;

}
