package com.mns.cda.saas_facturation.mapper;

import com.mns.cda.saas_facturation.DTO.PostalCodeCityDTO;
import com.mns.cda.saas_facturation.model.PostalCodeCity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PostalCodeCityMapper {

    private final PostalCodeMapper postalCodeMapper;
    private final CityMapper cityMapper;

    public PostalCodeCityDTO toDTO(PostalCodeCity postalCodeCity) {
        return new PostalCodeCityDTO(
                postalCodeMapper.toDTO(postalCodeCity.getPostalCode()),
                cityMapper.toDTO(postalCodeCity.getCity())
        );
    }

}
