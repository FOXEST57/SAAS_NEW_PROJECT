package com.mns.cda.saas_facturation.repository;

import com.mns.cda.saas_facturation.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {
}