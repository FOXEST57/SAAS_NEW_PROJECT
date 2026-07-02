package com.mns.cda.saas_facturation.model;

import jakarta.persistence.*;
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
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long crtId;

    @NotBlank
    @Column(unique = true, nullable = false)
    protected String crtRef;

    @CreatedDate
    protected LocalDateTime crtCreateDate;

    @LastModifiedDate
    protected LocalDateTime crtLastModifieDate;

    @NotBlank
    @Column(nullable = false)
    protected String crtStatus;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    protected Customer customer;
}
