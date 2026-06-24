package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.model.City;

import java.util.List;
import java.util.Optional;

public interface ICityService {

    public static class CityNotFoundException extends Exception {};

    List<City> findAll();

    Optional<City> findById(Long cityId);

    void create(City city);

    void modify(Long cityId, City city) throws CityNotFoundException;

    void delete(Long cityId) throws CityNotFoundException;
}
