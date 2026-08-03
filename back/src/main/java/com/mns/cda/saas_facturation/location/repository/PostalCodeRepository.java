package com.mns.cda.saas_facturation.location.repository;

import com.mns.cda.saas_facturation.location.model.PostalCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostalCodeRepository extends JpaRepository<PostalCode, Long> {
}