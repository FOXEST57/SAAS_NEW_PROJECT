package com.mns.cda.saas_facturation.cart.model;

import com.mns.cda.saas_facturation.enumeration.PaymentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long payId;

    @CreatedDate
    @NotNull
    @Column(updatable = false)
    protected LocalDateTime payCreatedDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    protected PaymentType payType;

    @NotNull
    protected Boolean payAccount;

    @NotNull
    protected BigDecimal payAmount;

    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    @NotNull
    protected Invoice invoice;
}