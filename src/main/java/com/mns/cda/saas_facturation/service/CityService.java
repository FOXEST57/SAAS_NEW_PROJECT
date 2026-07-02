package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.CityDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.CityRequestDTO;
import com.mns.cda.saas_facturation.Iservice.ICityService;
import com.mns.cda.saas_facturation.Iservice.ICountryService;
import com.mns.cda.saas_facturation.mapper.CityMapper;
import com.mns.cda.saas_facturation.model.City;
import com.mns.cda.saas_facturation.model.Country;
import com.mns.cda.saas_facturation.repository.CityRepository;
import com.mns.cda.saas_facturation.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CityService implements ICityService {

    private final CityRepository cityRepository;
    private final CityMapper cityMapper;
    private final CountryRepository countryRepository;

    @Override
    public List<CityDTO> findAll() {
        return cityRepository.findAll()
                .stream()
                .map(cityMapper::toDTO)
                .toList();
    }

    @Override
    public Optional<CityDTO> findById(Long cityId) {
        return cityRepository.findById(cityId).map(cityMapper::toDTO);
    }

    @Override
    public CityDTO create(CityRequestDTO dto) throws ICountryService.CountryNotFoundException {
        Country country = countryRepository.findById(dto.cntId()).orElseThrow(ICountryService.CountryNotFoundException::new);

        City city = new City(
                null,
                dto.cityName(),
                country
        );

        return cityMapper.toDTO(cityRepository.save(city));
    }

    @Override
    public CityDTO update(Long cityId, CityRequestDTO dto) throws CityNotFoundException, ICountryService.CountryNotFoundException {
        City city = cityRepository.findById(cityId).orElseThrow(CityNotFoundException::new);
        Country country = countryRepository.findById(dto.cntId()).orElseThrow(ICountryService.CountryNotFoundException::new);

        city.setCityName(dto.cityName());
        city.setCountry(country);

        return cityMapper.toDTO(cityRepository.save(city));
    }

    @Override
    public void delete(Long cityId) throws CityNotFoundException {
        City city = cityRepository.findById(cityId).orElseThrow(CityNotFoundException::new);
        
        cityRepository.delete(city);
    }

}
