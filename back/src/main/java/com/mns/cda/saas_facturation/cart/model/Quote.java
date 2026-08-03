package com.mns.cda.saas_facturation.cart.model;

import com.mns.cda.saas_facturation.enumeration.QuoteStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Quote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long qotId;

    @NotBlank
    @Column(unique = true)
    protected String qotNumber;

    @CreatedDate
    @NotNull
    @Column(updatable = false)
    protected LocalDateTime qotCreatedDate;

    @NotNull
    protected LocalDate qotExpirationDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    protected QuoteStatus qotStatus;

    @ManyToOne
    protected Quote qotParent;

    @ManyToOne
    @NotNull
    protected Cart cart;

    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotNull
    protected List<QuoteLine> qotLines = new ArrayList<>();

}
