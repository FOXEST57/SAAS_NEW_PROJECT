package com.mns.cda.saas_facturation.cart.model;

import com.mns.cda.saas_facturation.enumeration.InvoiceStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long invoiceId;

    @NotBlank
    @Column(unique = true)
    protected String invoiceNumber;

    @CreatedDate
    @NotNull
    @Column(updatable = false)
    protected LocalDateTime invoiceCreatedDate;

    @LastModifiedDate
    @NotNull
    protected LocalDateTime invoiceModifiedDate;

    @NotBlank
    @Column(nullable = false)
    protected String invoicePathPDF;

    @NotNull
    @Enumerated(EnumType.STRING)
    protected InvoiceStatus invoiceStatus;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotNull
    protected List<InvoiceLine> invoiceLines = new ArrayList<>();
    
    @OneToOne
    @JoinColumn(unique = true)
    protected Command command;

    @NotNull
    protected Long creatorId;

    @NotNull
    @Email
    protected String receiverEmail;

    @OneToMany(mappedBy = "invoice")
    protected List<Payment> payments = new ArrayList<>();
}
