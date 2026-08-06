package com.mns.cda.saas_facturation.user.model;

import com.mns.cda.saas_facturation.config.LowercaseConverter;
import com.mns.cda.saas_facturation.enumeration.InvitationTypeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long invId;

    @Email
    @NotBlank
    @Column(unique = true)
    @Convert(converter = LowercaseConverter.class)
    protected String invEmail;

    @NotBlank
    @Column(unique = true)
    protected String invToken;

    @Enumerated(EnumType.STRING)
    protected InvitationTypeEnum invitationType;


    @CreatedDate
    @Column(updatable = false)
    protected LocalDateTime invCreationDate;

    protected LocalDateTime invExpirationDate;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

}
