package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.requestDTO.PostalCodeCityRequestDTO;
import com.mns.cda.saas_facturation.Iservice.IPostalCodeService;
import com.mns.cda.saas_facturation.Iservice.IPostalCodeCityService;
import com.mns.cda.saas_facturation.Iservice.ICityService;
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

    protected final PostalCodeCityRepository postalCodeCityRepository;
    protected final CityRepository cityRepository;
    protected final PostalCodeRepository postalCodeRepository;

    //GetAll
    @Override
    public List<PostalCodeCity> findAll() {
        return postalCodeCityRepository.findAll();
    }

    //Get By Id
    @Override
    public Optional<PostalCodeCity> findById(PostalCodeCity.PostalCodeCityId id) {
        return postalCodeCityRepository.findById(id);
    }


    //Post
    @Override
    public PostalCodeCity create(PostalCodeCityRequestDTO dto) throws IPostalCodeService.PostalCodeNotFoundException, ICityService.CityNotFoundException {

        PostalCode postalCode = postalCodeRepository.findById(dto.pcodeId()).orElseThrow(IPostalCodeService.PostalCodeNotFoundException::new);

        City city = cityRepository.findById(dto.cityId()).orElseThrow(ICityService.CityNotFoundException::new);

        PostalCodeCity postalCodeCity = new PostalCodeCity(
                dto.pcodeId(),
                dto.cityId(),
                postalCode,
                city
        );

        return postalCodeCityRepository.save(postalCodeCity);
    }



    //PUT
    @Override
    public PostalCodeCity update(PostalCodeCity.PostalCodeCityId id, PostalCodeCityRequestDTO dto) throws PostalCodeCityNotFoundException, IPostalCodeService.PostalCodeNotFoundException, ICityService.CityNotFoundException {
        PostalCodeCity postalCodeCity = postalCodeCityRepository.findById(id).orElseThrow(PostalCodeCityNotFoundException::new);

        PostalCode postalCode = postalCodeRepository.findById(dto.pcodeId()).orElseThrow(IPostalCodeService.PostalCodeNotFoundException::new);
        City city = cityRepository.findById(dto.cityId()).orElseThrow(ICityService.CityNotFoundException::new);

        postalCodeCity.setPostalCode(postalCode);
        postalCodeCity.setCity(city);

        return postalCodeCityRepository.save(postalCodeCity);
    }

    @Override
    public void delete(PostalCodeCity.PostalCodeCityId id) throws PostalCodeCityNotFoundException {
        postalCodeCityRepository.findById(id).orElseThrow(PostalCodeCityNotFoundException::new);
        postalCodeCityRepository.deleteById(id);
    }

}
