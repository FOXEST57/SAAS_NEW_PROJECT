package com.mns.cda.saas_facturation.location.repository;

import com.mns.cda.saas_facturation.location.model.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {
}