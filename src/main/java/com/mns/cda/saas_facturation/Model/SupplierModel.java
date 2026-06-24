package com.mns.cda.saas_facturation.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SupplierModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long splId;

    @Column(length = 50)
    private String splName;

    @Column(length = 50)
    private String splEmail;

    @Column(length = 50)
    private String splPhone;

    @Column(length = 50)
    private String splAdress;

}
