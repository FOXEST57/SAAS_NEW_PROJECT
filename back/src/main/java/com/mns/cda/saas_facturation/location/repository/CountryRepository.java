package com.mns.cda.saas_facturation.location.repository;

import com.mns.cda.saas_facturation.location.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {
}