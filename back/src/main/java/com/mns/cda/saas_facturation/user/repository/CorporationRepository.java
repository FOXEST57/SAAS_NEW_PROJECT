package com.mns.cda.saas_facturation.user.repository;

import com.mns.cda.saas_facturation.user.model.Corporation;
import com.mns.cda.saas_facturation.user.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CorporationRepository extends JpaRepository<Corporation, Long> {

    Corporation findCorporationByOwner_CtmId(Long ownerCtmId);
}