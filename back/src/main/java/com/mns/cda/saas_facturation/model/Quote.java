package com.mns.cda.saas_facturation.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Quote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long qotId;

    @NotBlank
    protected String qotNumber;

    @CreatedDate
    @NotNull
    protected LocalDateTime qotCreatedDate;

    @NotNull
    protected LocalDate expirationDate;

    @NotNull
    protected String qotStatus;

    @ManyToOne
    protected Quote qotParent;

    @OneToMany
    @NotNull
    protected List<QuoteLine> qotLines;

}
