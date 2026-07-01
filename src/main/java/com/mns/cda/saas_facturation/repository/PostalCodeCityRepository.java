package com.mns.cda.saas_facturation.repository;

import com.mns.cda.saas_facturation.model.PostalCodeCity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostalCodeCityRepository extends JpaRepository<PostalCodeCity, PostalCodeCity.PostalCodeCityId> {
}