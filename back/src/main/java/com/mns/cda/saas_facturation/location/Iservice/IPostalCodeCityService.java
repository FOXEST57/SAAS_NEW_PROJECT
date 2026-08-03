package com.mns.cda.saas_facturation.location.Iservice;

import com.mns.cda.saas_facturation.location.DTO.PostalCodeCityDTO;
import com.mns.cda.saas_facturation.location.DTO.requestDTO.PostalCodeCityRequestDTO;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;

import java.util.List;

public interface IPostalCodeCityService {

    List<PostalCodeCityDTO> findAll();

    PostalCodeCityDTO findById(Long pCodeId, Long cityId) throws ResourceNotFoundException;

    PostalCodeCityDTO create(PostalCodeCityRequestDTO dto) throws ResourceNotFoundException;

    PostalCodeCityDTO update(Long pCodeId, Long cityId, PostalCodeCityRequestDTO dto) throws ResourceNotFoundException;

    void delete(Long pCodeId, Long cityId) throws ResourceNotFoundException;
}
