package com.mns.cda.saas_facturation.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PostalCodeCity.PostalCodeCityId.class)
@Entity
public class PostalCodeCity {

    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostalCodeCityId implements Serializable {
        @Column(name = "pcode_id")
        Long pCodeId;
        @Column(name = "city_id")
        Long cityId;
    }

    @Id
    protected Long pCodeId;

    @Id
    protected Long cityId;

    @ManyToOne
    @MapsId("pCodeId")
    @JoinColumn(name = "pcode_id")
    protected PostalCode postalCode;

    @ManyToOne
    @MapsId("cityId")
    @JoinColumn(name = "city_id")
    protected City city;

}
