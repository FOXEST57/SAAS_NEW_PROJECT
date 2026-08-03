package com.mns.cda.saas_facturation.location.model;

import com.mns.cda.saas_facturation.config.LowercaseConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long cityId;

    @NotBlank
    @Column(unique = true)
    @Convert(converter = LowercaseConverter.class)
    protected String cityName;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "cnt_id")
    protected Country country;

}
