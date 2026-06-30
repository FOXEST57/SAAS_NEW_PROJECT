package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.CountryDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.CountryRequestDTO;
import com.mns.cda.saas_facturation.Iservice.ICountryService;
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

    @Override
    public List<CountryDTO> findAll() {
        return countryRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public Optional<CountryDTO> findById(Long cntId) {
        return countryRepository.findById(cntId).map(this::toDTO);
    }

    @Override
    public CountryDTO create(CountryRequestDTO dto) {
        Country country = new Country(
                null,
                dto.cntName()
        );

        return toDTO(countryRepository.save(country));
    }

    @Override
    public CountryDTO update(Long cntId, CountryRequestDTO dto) throws CountryNotFoundException {
        Country country = countryRepository.findById(cntId).orElseThrow(CountryNotFoundException::new);

        country.setCntName(dto.cntName());

        return toDTO(countryRepository.save(country));
    }

    @Override
    public void delete(Long cntId) throws CountryNotFoundException {
        Country country = countryRepository.findById(cntId).orElseThrow(CountryNotFoundException::new);

        countryRepository.delete(country);
    }

    @Override
    public CountryDTO toDTO(Country country) {
        return new CountryDTO(
                country.getCntName()
        );
    }

}
