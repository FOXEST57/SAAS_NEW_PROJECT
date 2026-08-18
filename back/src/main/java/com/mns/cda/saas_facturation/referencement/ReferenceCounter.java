package com.mns.cda.saas_facturation.referencement;


import com.mns.cda.saas_facturation.user.model.Corporation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ReferenceCounter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rfcId;

    @ManyToOne
    @JoinColumn(name = "crp_id")
    private Corporation corporation;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReferenceType objectType;

    @Column(nullable = false)
    private Long counter;
}
