package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.requestDTO.PostalCodeCityRequestDTO;
import com.mns.cda.saas_facturation.model.PostalCodeCity;

import java.util.List;
import java.util.Optional;

public interface IPostalCodeCityService {

    public static class PostalCodeCityNotFoundException extends Exception {}

    List<PostalCodeCity> findAll();

    Optional<PostalCodeCity> findById(PostalCodeCity.PostalCodeCityId id);

    PostalCodeCity create(PostalCodeCityRequestDTO dto) throws IPostalCodeCityService.PostalCodeCityNotFoundException, ICityService.CityNotFoundException, IPostalCodeService.PostalCodeNotFoundException;

    PostalCodeCity update(PostalCodeCity.PostalCodeCityId id, PostalCodeCityRequestDTO dto) throws IPostalCodeCityService.PostalCodeCityNotFoundException, IPostalCodeService.PostalCodeNotFoundException, ICityService.CityNotFoundException;

    void delete(PostalCodeCity.PostalCodeCityId id) throws PostalCodeCityNotFoundException;

}
