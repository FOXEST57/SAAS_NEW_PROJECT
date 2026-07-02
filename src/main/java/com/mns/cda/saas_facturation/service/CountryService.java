package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.CountryDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.CountryRequestDTO;
import com.mns.cda.saas_facturation.Iservice.ICountryService;
import com.mns.cda.saas_facturation.mapper.CountryMapper;
import com.mns.cda.saas_facturation.model.Country;
import com.mns.cda.saas_facturation.repository.CountryRepository;
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
    public CountryDTO update(Long cntId, CountryRequestDTO dto) throws CountryNotFoundException {
        Country country = countryRepository.findById(cntId).orElseThrow(CountryNotFoundException::new);

        country.setCntName(dto.cntName());

        return countryMapper.toDTO(countryRepository.save(country));
    }

    @Override
    public void delete(Long cntId) throws CountryNotFoundException {
        Country country = countryRepository.findById(cntId).orElseThrow(CountryNotFoundException::new);

        countryRepository.delete(country);
    }

}
