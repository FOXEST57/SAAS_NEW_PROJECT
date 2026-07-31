package com.mns.cda.saas_facturation.model;

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
public class QuoteLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long qotLnId;

    @NotNull
    protected int qotLnQuantity;

    @NotNull
    @Min(0)
    protected BigDecimal qotLnPriceHT;

    @NotBlank
    protected String articleName;

    @NotBlank
    protected String articleRef;

    @NotNull
    protected BigDecimal tvaRate;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    protected Cart cart;
}
