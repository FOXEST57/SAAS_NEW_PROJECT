package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.CityDTO;
import com.mns.cda.saas_facturation.DTO.CityRequestDTO;
import com.mns.cda.saas_facturation.model.City;

import java.util.List;
import java.util.Optional;

public interface ICityService {

    public static class CityNotFoundException extends Exception {};

    List<CityDTO> findAll();

    Optional<CityDTO> findById(Long cityId);

    void create(CityRequestDTO dto) throws IPostalCodeService.PostalCodeNotFoundException;

    CityDTO modify(Long cityId, CityRequestDTO dto) throws CityNotFoundException, IPostalCodeService.PostalCodeNotFoundException;

    void delete(Long cityId) throws CityNotFoundException;
}
