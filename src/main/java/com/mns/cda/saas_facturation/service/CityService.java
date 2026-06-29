package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.CityDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.CityRequestDTO;
import com.mns.cda.saas_facturation.Iservice.ICityService;
import com.mns.cda.saas_facturation.Iservice.IPostalCodeService;
import com.mns.cda.saas_facturation.model.City;
import com.mns.cda.saas_facturation.model.PostalCode;
import com.mns.cda.saas_facturation.repository.CityRepository;
import com.mns.cda.saas_facturation.repository.PostalCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CityService implements ICityService {

    private final CityRepository cityRepository;
    private final PostalCodeRepository postalCodeRepository;
    private final PostalCodeService postalCodeService;

    @Override
    public List<CityDTO> findAll() {
        return cityRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public Optional<CityDTO> findById(Long cityId) {
        return cityRepository.findById(cityId).map(this::toDTO);
    }

    @Override
    public void create(CityRequestDTO dto) throws IPostalCodeService.PostalCodeNotFoundException {
        PostalCode postalCode = postalCodeRepository.findById(dto.postalCodeId()).orElseThrow(IPostalCodeService.PostalCodeNotFoundException::new);

        City city = new City(
                null,
                dto.cityName(),
                postalCode
        );

        cityRepository.save(city);
    }

    @Override
    public CityDTO modify(Long cityId, CityRequestDTO dto) throws CityNotFoundException, IPostalCodeService.PostalCodeNotFoundException {
        City city = cityRepository.findById(cityId).orElseThrow(CityNotFoundException::new);
        PostalCode postalCode = postalCodeRepository.findById(dto.postalCodeId()).orElseThrow(IPostalCodeService.PostalCodeNotFoundException::new);

        city.setCityName(dto.cityName());
        city.setPostalCode(postalCode);

        return toDTO(cityRepository.save(city));
    }

    @Override
    public void delete(Long cityId) throws CityNotFoundException {
        City city = cityRepository.findById(cityId).orElseThrow(CityNotFoundException::new);
        
        cityRepository.delete(city);
    }


    protected CityDTO toDTO(City city) {
        return new CityDTO(
            city.getCityName(),
            postalCodeService.toDTO(city.getPostalCode())
        );
    }

}
