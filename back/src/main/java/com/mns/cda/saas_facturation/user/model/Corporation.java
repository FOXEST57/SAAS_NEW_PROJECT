package com.mns.cda.saas_facturation.user.model;

import com.mns.cda.saas_facturation.location.model.Address;
import com.mns.cda.saas_facturation.validation.ValidPhoneNumber;
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
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Corporation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long corpId;

    @NotBlank
    protected String corpName;

    @NotBlank
    protected String corpSiret;

    @CreatedDate
    @Column(updatable = false)
    protected LocalDateTime corpCreationDate;

    @LastModifiedDate
    protected LocalDateTime corpModificationDate;

    protected String corpPreRefQuote;

    protected String corpPreRefInvoice;

    @Email
    @NotBlank
    protected String corpEmail;

    @ValidPhoneNumber
    @NotBlank
    protected String corpPhone;

    @NotBlank
    protected String corpTva;

    @NotBlank
    protected String corpIban;

    protected String corpTag;

    @ManyToOne
    @JoinColumn(name = "address_add_id")
    @NotNull
    protected Address address;

    @ManyToOne
    @JoinColumn(name = "owner_ctm_id", unique = true)
    protected Customer owner;

    @OneToMany(mappedBy = "employer", cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<Customer> employees;

    @OneToMany(mappedBy = "client")
    protected List<Customer> customers;
}
