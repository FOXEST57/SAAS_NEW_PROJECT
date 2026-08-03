package com.mns.cda.saas_facturation.location.mapper;

import com.mns.cda.saas_facturation.location.DTO.CountryDTO;
import com.mns.cda.saas_facturation.location.model.Country;
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
