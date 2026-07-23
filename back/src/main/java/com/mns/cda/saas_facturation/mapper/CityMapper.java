package com.mns.cda.saas_facturation.mapper;

import com.mns.cda.saas_facturation.DTO.CityDTO;
import com.mns.cda.saas_facturation.model.City;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CityMapper {

    private final CountryMapper countryMapper;

    public CityDTO toDTO(City city) {
        return new CityDTO(
                city.getCityId(),
                city.getCityName(),
                countryMapper.toDTO(city.getCountry())
        );
    }

}
