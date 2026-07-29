package com.mns.cda.saas_facturation.model;


import com.mns.cda.saas_facturation.config.LowercaseConverter;
import com.mns.cda.saas_facturation.validation.ValidPhoneNumber;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @NotBlank
    @Column(unique = true)
    @Convert(converter = LowercaseConverter.class)
    protected String ctmEmail;

    @ValidPhoneNumber
    @NotBlank
    protected String ctmPhone;

    @Pattern(//regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$",
            message = "Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial"
    )
    protected String password;

    @ManyToOne
    @JoinColumn(name = "add_id")
    @NotNull
    protected Address address;

    @CreatedDate
    protected LocalDateTime ctmCreationDate;

    @LastModifiedDate
    protected LocalDateTime ctmModificationDate;

    @ManyToOne
    @JoinColumn(name = "acc_type_id")
    @NotNull
    protected AccountType accountType;

    @ManyToMany
    protected List<Customer> customers;

}
