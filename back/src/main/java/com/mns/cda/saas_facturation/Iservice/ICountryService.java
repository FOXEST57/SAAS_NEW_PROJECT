package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.CountryDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.CountryRequestDTO;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

public interface ICountryService {

    List<CountryDTO> findAll();

    Optional<CountryDTO> findById(Long cntId);

    CountryDTO create(CountryRequestDTO country);

    CountryDTO update(Long cntId, CountryRequestDTO country) throws ResourceNotFoundException;

    void delete(Long cntId) throws ResourceNotFoundException;

}
