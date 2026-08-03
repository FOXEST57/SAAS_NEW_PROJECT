package com.mns.cda.saas_facturation.location.model;

import com.mns.cda.saas_facturation.config.LowercaseConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long cntId;

    @NotBlank
    @Column(unique = true)
    @Convert(converter = LowercaseConverter.class)
    protected String cntName;

}
