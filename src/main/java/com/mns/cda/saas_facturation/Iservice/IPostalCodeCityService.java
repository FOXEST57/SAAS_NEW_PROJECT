package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.PostalCodeCityDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.PostalCodeCityRequestDTO;
import com.mns.cda.saas_facturation.model.PostalCodeCity;

import java.util.List;
import java.util.Optional;

public interface IPostalCodeCityService {

    public static class PostalCodeCityNotFoundException extends Exception {}

    List<PostalCodeCityDTO> findAll();

    Optional<PostalCodeCityDTO> findById(PostalCodeCity.PostalCodeCityId id);

    PostalCodeCityDTO create(PostalCodeCityRequestDTO dto) throws IPostalCodeCityService.PostalCodeCityNotFoundException, ICityService.CityNotFoundException, IPostalCodeService.PostalCodeNotFoundException;

    PostalCodeCityDTO update(PostalCodeCity.PostalCodeCityId id, PostalCodeCityRequestDTO dto) throws IPostalCodeCityService.PostalCodeCityNotFoundException, IPostalCodeService.PostalCodeNotFoundException, ICityService.CityNotFoundException;

    void delete(PostalCodeCity.PostalCodeCityId id) throws PostalCodeCityNotFoundException;

    PostalCodeCityDTO toDTO(PostalCodeCity postalCodeCity);

}
