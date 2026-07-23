package com.mns.cda.saas_facturation.mapper;

import com.mns.cda.saas_facturation.DTO.CountryDTO;
import com.mns.cda.saas_facturation.model.Country;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CountryMapper {

    public CountryDTO toDTO(Country country) {
        return new CountryDTO(
                country.getCntId(),
                country.getCntName()
        );
    }

}
