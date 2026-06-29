package com.mns.cda.saas_facturation.model;


import com.mns.cda.saas_facturation.validation.ValidPhoneNumber;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    protected String ctmFirstName;

    @NotBlank
    protected String ctmLastName;

    @Email
    protected String ctmEmail;

    @ValidPhoneNumber
    protected String ctmPhone;

    @NotBlank
    protected String ctmAddress;

    @CreatedDate
    protected LocalDateTime ctmCreationDate;

    @LastModifiedDate
    protected LocalDateTime ctmModificationDate;

    @ManyToOne
    @NotBlank
    protected City city;

}
