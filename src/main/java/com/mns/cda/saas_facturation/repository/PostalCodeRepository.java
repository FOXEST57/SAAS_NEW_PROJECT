package com.mns.cda.saas_facturation.repository;

import com.mns.cda.saas_facturation.model.PostalCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostalCodeRepository extends JpaRepository<PostalCode, Long> {
}