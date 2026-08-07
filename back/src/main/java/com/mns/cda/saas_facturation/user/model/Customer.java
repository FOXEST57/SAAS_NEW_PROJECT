package com.mns.cda.saas_facturation.user.model;


import com.mns.cda.saas_facturation.config.LowercaseConverter;
import com.mns.cda.saas_facturation.enumeration.AccountTypeEnum;
import com.mns.cda.saas_facturation.location.model.Address;
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
import java.util.ArrayList;
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
    protected Address address;

    @CreatedDate
    @Column(updatable = false)
    protected LocalDateTime ctmCreationDate;

    @LastModifiedDate
    protected LocalDateTime ctmModificationDate;


    @NotNull
    @Enumerated(EnumType.STRING)
    protected AccountTypeEnum accountType;

    //Liste de client à qui l'employer à envoyer une invitation null si pas employee
    @OneToMany(mappedBy = "customer")
    private List<Invitation> invitations = new ArrayList<>();

    //Entreprise dans laquelle travail l'employer null si pas employee.
    @ManyToOne
    @JoinColumn(name = "corp_corporation_id")
    protected Corporation corporation;

    //Liste des entreprises avec qui les clients ont un lien null si pas User.
    @ManyToMany(mappedBy = "customers")
    protected List<Corporation> corporations = new ArrayList<>();
}
