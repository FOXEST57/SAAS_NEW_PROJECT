package com.mns.cda.saas_facturation.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long ctmId;

    protected String ctmFirstName;

    protected String ctmLastName;

    @Email
    protected String ctmEmail;

    protected String ctmPhone;

    protected String ctmAddress;

    @CreatedDate
    protected LocalDateTime ctmCreationDate;

    @LastModifiedDate
    protected LocalDateTime ctmModificationDate;

}
