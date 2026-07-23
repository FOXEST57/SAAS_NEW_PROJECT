package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.PostalCodeCityDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.PostalCodeCityRequestDTO;
import com.mns.cda.saas_facturation.Iservice.IPostalCodeCityService;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.mapper.PostalCodeCityMapper;
import com.mns.cda.saas_facturation.model.PostalCode;
import com.mns.cda.saas_facturation.model.PostalCodeCity;
import com.mns.cda.saas_facturation.model.City;
import com.mns.cda.saas_facturation.repository.PostalCodeRepository;
import com.mns.cda.saas_facturation.repository.PostalCodeCityRepository;
import com.mns.cda.saas_facturation.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PostalCodeCityService implements IPostalCodeCityService {

    private final PostalCodeCityRepository postalCodeCityRepository;
    private final PostalCodeCityMapper postalCodeCityMapper;
    private final PostalCodeRepository postalCodeRepository;
    private final CityRepository cityRepository;

    //GetAll
    @Override
    public List<PostalCodeCityDTO> findAll() {

        return postalCodeCityRepository.findAll()
                .stream()
                .map(postalCodeCityMapper::toDTO)
                .toList();
    }

    //Get By Id
    @Override
    public PostalCodeCityDTO findById(Long pCodeId, Long cityId) {
        postalCodeRepository.findById(pCodeId).orElseThrow(() -> new ResourceNotFoundException("Code postal non existant"));
        cityRepository.findById(cityId).orElseThrow(() -> new ResourceNotFoundException("Ville non existante"));
        PostalCodeCity.PostalCodeCityId id = new PostalCodeCity.PostalCodeCityId(pCodeId, cityId);
        PostalCodeCity postalCodeCity = postalCodeCityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Lien entre code postal et ville non existant"));

        return postalCodeCityMapper.toDTO(postalCodeCity);
    }


    //Post
    @Override
    public PostalCodeCityDTO create(PostalCodeCityRequestDTO dto) throws ResourceNotFoundException {

        PostalCode postalCode = postalCodeRepository.findById(dto.pCodeId()).orElseThrow(() -> new ResourceNotFoundException("Code postal non existant"));

        City city = cityRepository.findById(dto.cityId()).orElseThrow(() -> new ResourceNotFoundException("Ville non existante"));

        PostalCodeCity postalCodeCity = new PostalCodeCity(
                dto.pCodeId(),
                dto.cityId(),
                postalCode,
                city
        );

        return postalCodeCityMapper.toDTO(postalCodeCityRepository.save(postalCodeCity));
    }


    //PUT
    @Override
    public PostalCodeCityDTO update(Long pCodeId, Long cityId, PostalCodeCityRequestDTO dto) throws ResourceNotFoundException {
        postalCodeRepository.findById(pCodeId).orElseThrow(() -> new ResourceNotFoundException("Code postal non existant"));
        cityRepository.findById(cityId).orElseThrow(() -> new ResourceNotFoundException("Ville non existante"));
        PostalCodeCity.PostalCodeCityId id = new PostalCodeCity.PostalCodeCityId(pCodeId, cityId);
        PostalCodeCity postalCodeCity = postalCodeCityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Lien entre code postal et ville non existant"));

        PostalCode postalCode = postalCodeRepository.findById(dto.pCodeId()).orElseThrow(() -> new ResourceNotFoundException("Code postal non existant"));
        City city = cityRepository.findById(dto.cityId()).orElseThrow(() -> new ResourceNotFoundException("Ville non existante"));

        postalCodeCity.setPostalCode(postalCode);
        postalCodeCity.setCity(city);

        return postalCodeCityMapper.toDTO(postalCodeCityRepository.save(postalCodeCity));
    }

    @Override
    public void delete(Long pCodeId, Long cityId) throws ResourceNotFoundException {
        postalCodeRepository.findById(pCodeId).orElseThrow(() -> new ResourceNotFoundException("Code postal non existant"));
        cityRepository.findById(cityId).orElseThrow(() -> new ResourceNotFoundException("Ville non existante"));
        PostalCodeCity.PostalCodeCityId id = new PostalCodeCity.PostalCodeCityId(pCodeId, cityId);
        postalCodeCityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Lien entre code postal et ville non existant"));
        postalCodeCityRepository.deleteById(id);
    }

}
