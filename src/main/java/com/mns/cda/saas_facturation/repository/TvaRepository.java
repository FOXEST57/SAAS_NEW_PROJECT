package com.mns.cda.saas_facturation.repository;

import com.mns.cda.saas_facturation.model.Tva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TvaRepository extends JpaRepository <Tva,Long> {
}
