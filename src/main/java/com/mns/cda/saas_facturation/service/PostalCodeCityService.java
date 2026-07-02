package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.PostalCodeCityDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.PostalCodeCityRequestDTO;
import com.mns.cda.saas_facturation.Iservice.IPostalCodeService;
import com.mns.cda.saas_facturation.Iservice.IPostalCodeCityService;
import com.mns.cda.saas_facturation.Iservice.ICityService;
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
import java.util.Optional;


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
    public Optional<PostalCodeCityDTO> findById(PostalCodeCity.PostalCodeCityId id) {
        return postalCodeCityRepository.findById(id).map(postalCodeCityMapper::toDTO);
    }


    //Post
    @Override
    public PostalCodeCityDTO create(PostalCodeCityRequestDTO dto) throws ResourceNotFoundException, ResourceNotFoundException {

        PostalCode postalCode = postalCodeRepository.findById(dto.pcodeId()).orElseThrow(() -> new ResourceNotFoundException("Code postal non existant"));

        City city = cityRepository.findById(dto.cityId()).orElseThrow(() -> new ResourceNotFoundException("Ville non existante"));

        PostalCodeCity postalCodeCity = new PostalCodeCity(
                dto.pcodeId(),
                dto.cityId(),
                postalCode,
                city
        );

        return postalCodeCityMapper.toDTO(postalCodeCityRepository.save(postalCodeCity));
    }


    //PUT
    @Override
    public PostalCodeCityDTO update(PostalCodeCity.PostalCodeCityId id, PostalCodeCityRequestDTO dto) throws ResourceNotFoundException {
        PostalCodeCity postalCodeCity = postalCodeCityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Lien entre code postal et ville non existant"));

        PostalCode postalCode = postalCodeRepository.findById(dto.pcodeId()).orElseThrow(() -> new ResourceNotFoundException("Code postal non existant"));
        City city = cityRepository.findById(dto.cityId()).orElseThrow(() -> new ResourceNotFoundException("Ville non existante"));

        postalCodeCity.setPostalCode(postalCode);
        postalCodeCity.setCity(city);

        return postalCodeCityMapper.toDTO(postalCodeCityRepository.save(postalCodeCity));
    }

    @Override
    public void delete(PostalCodeCity.PostalCodeCityId id) throws ResourceNotFoundException {
        postalCodeCityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Lien entre code postal et ville non existant"));
        postalCodeCityRepository.deleteById(id);
    }

}
