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
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long ctmId;

    @NotBlank
    protected String ctmFirstName;

    @NotBlank
    protected String ctmLastName;

    @Email
    protected String ctmEmail;

    @ValidPhoneNumber
    protected String ctmPhone;

    @ManyToOne
    @JoinColumn(name = "add_id")
    @NotNull
    protected Address address;

    @CreatedDate
    protected LocalDateTime ctmCreationDate;

    @LastModifiedDate
    protected LocalDateTime ctmModificationDate;

}
