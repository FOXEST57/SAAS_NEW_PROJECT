package com.mns.cda.saas_facturation.location.Iservice;

import com.mns.cda.saas_facturation.location.DTO.CityDTO;
import com.mns.cda.saas_facturation.location.DTO.requestDTO.CityRequestDTO;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

public interface ICityService {

    List<CityDTO> findAll();

    Optional<CityDTO> findById(Long cityId);

    CityDTO create(CityRequestDTO dto) throws ResourceNotFoundException;

    CityDTO update(Long cityId, CityRequestDTO dto) throws ResourceNotFoundException;

    void delete(Long cityId) throws ResourceNotFoundException;

}
