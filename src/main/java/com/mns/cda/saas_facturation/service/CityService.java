package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.model.City;
import com.mns.cda.saas_facturation.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CityService implements ICityService {

    private final CityRepository cityRepository;

    @Override
    public List<City> findAll() {
        return cityRepository.findAll();
    }

    @Override
    public Optional<City> findById(Long cityId) {
        return cityRepository.findById(cityId);
    }

    @Override
    public void create(City city) {
        city.setCityId(null);
        cityRepository.save(city);
    }

    @Override
    public void modify(Long cityId, City city) throws CityNotFoundException {
        Optional<City> optionalCity = cityRepository.findById(cityId);

        if (optionalCity.isEmpty()) {
            throw new CityNotFoundException();
        }

        city.setCityId(cityId);
        cityRepository.save(city);
    }

    @Override
    public void delete(Long cityId) throws CityNotFoundException {
        Optional<City> optionalCity = cityRepository.findById(cityId);
        
        if (optionalCity.isEmpty()) {
            throw new CityNotFoundException();
        }
        
        cityRepository.deleteById(cityId);
    }

}
