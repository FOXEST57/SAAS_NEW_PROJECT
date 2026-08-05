package com.mns.cda.saas_facturation.cart.model;

import com.mns.cda.saas_facturation.config.LowercaseConverter;
import com.mns.cda.saas_facturation.enumeration.CartStatus;
import com.mns.cda.saas_facturation.user.model.Customer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long crtId;

    @NotBlank
    @Column(unique = true, nullable = false)
    @Convert(converter = LowercaseConverter.class)
    protected String crtRef;

    @CreatedDate
    @Column(updatable = false)
    protected LocalDateTime crtCreateDate;

    @LastModifiedDate
    protected LocalDateTime crtLastModifieDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    protected CartStatus crtStatus;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    protected Customer customer;

    @OneToMany(mappedBy = "cart")
    protected List<OrderLine> orderLines;
}
