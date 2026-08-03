package com.mns.cda.saas_facturation.location.repository;

import com.mns.cda.saas_facturation.location.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}