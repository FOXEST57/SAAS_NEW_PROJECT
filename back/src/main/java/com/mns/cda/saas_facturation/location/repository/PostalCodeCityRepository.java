package com.mns.cda.saas_facturation.location.repository;

import com.mns.cda.saas_facturation.location.model.PostalCodeCity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostalCodeCityRepository extends JpaRepository<PostalCodeCity, PostalCodeCity.PostalCodeCityId> {
}