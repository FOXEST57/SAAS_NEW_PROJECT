package com.mns.cda.saas_facturation.repository;

import com.mns.cda.saas_facturation.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}