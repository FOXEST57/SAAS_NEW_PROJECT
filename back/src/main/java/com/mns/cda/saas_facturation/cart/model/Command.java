package com.mns.cda.saas_facturation.cart.model;

import com.mns.cda.saas_facturation.enumeration.CommandStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Command {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long cmdId;

    @CreatedDate
    @Column(updatable = false)
    protected LocalDateTime cmdCreateDate;

    @LastModifiedDate
    protected LocalDateTime cmdModifiedDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    protected CommandStatus cmdStatus;

    @OneToOne
    @JoinColumn(name = "quote_id", unique = true)
    protected Quote quote;

    @NotNull
    protected Long creatorId;

    @NotNull
    @Email
    protected String receiverEmail;
}
