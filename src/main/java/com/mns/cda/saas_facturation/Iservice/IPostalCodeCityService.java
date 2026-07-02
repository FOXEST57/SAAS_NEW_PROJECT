package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.PostalCodeCityDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.PostalCodeCityRequestDTO;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.model.PostalCodeCity;

import java.util.List;
import java.util.Optional;

public interface IPostalCodeCityService {

    List<PostalCodeCityDTO> findAll();

    Optional<PostalCodeCityDTO> findById(PostalCodeCity.PostalCodeCityId id);

    PostalCodeCityDTO create(PostalCodeCityRequestDTO dto) throws ResourceNotFoundException;

    PostalCodeCityDTO update(PostalCodeCity.PostalCodeCityId id, PostalCodeCityRequestDTO dto) throws ResourceNotFoundException;

    void delete(PostalCodeCity.PostalCodeCityId id) throws ResourceNotFoundException;

}
