package com.mns.cda.saas_facturation.location.service;

import com.mns.cda.saas_facturation.location.DTO.CountryDTO;
import com.mns.cda.saas_facturation.location.DTO.requestDTO.CountryRequestDTO;
import com.mns.cda.saas_facturation.location.Iservice.ICountryService;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.location.mapper.CountryMapper;
import com.mns.cda.saas_facturation.location.model.Country;
import com.mns.cda.saas_facturation.location.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CountryService implements ICountryService {

    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;

    @Override
    public List<CountryDTO> findAll() {
        return countryRepository.findAll()
                .stream()
                .map(countryMapper::toDTO)
                .toList();
    }

    @Override
    public Optional<CountryDTO> findById(Long cntId) {
        return countryRepository.findById(cntId).map(countryMapper::toDTO);
    }

    @Override
    public CountryDTO create(CountryRequestDTO dto) {
        Country country = new Country(
                null,
                dto.cntName()
        );

        return countryMapper.toDTO(countryRepository.save(country));
    }

    @Override
    public CountryDTO update(Long cntId, CountryRequestDTO dto) throws ResourceNotFoundException {
        Country country = countryRepository.findById(cntId).orElseThrow(() -> new ResourceNotFoundException("Pays non existant"));

        country.setCntName(dto.cntName());

        return countryMapper.toDTO(countryRepository.save(country));
    }

    @Override
    public void delete(Long cntId) throws ResourceNotFoundException {
        Country country = countryRepository.findById(cntId).orElseThrow(() -> new ResourceNotFoundException("Pays non existant"));

        countryRepository.delete(country);
    }

}
