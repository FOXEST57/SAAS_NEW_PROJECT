package com.mns.cda.saas_facturation.repository;

import com.mns.cda.saas_facturation.model.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {
}