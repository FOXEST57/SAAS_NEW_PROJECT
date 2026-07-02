package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.CityDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.CityRequestDTO;
import com.mns.cda.saas_facturation.model.City;

import java.util.List;
import java.util.Optional;

public interface ICityService {

    public static class CityNotFoundException extends Exception {};

    List<CityDTO> findAll();

    Optional<CityDTO> findById(Long cityId);

    CityDTO create(CityRequestDTO dto) throws ICountryService.CountryNotFoundException;

    CityDTO update(Long cityId, CityRequestDTO dto) throws CityNotFoundException, ICountryService.CountryNotFoundException;

    void delete(Long cityId) throws CityNotFoundException;

}
