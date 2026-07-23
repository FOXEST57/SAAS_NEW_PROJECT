package com.mns.cda.saas_facturation.repository;

import com.mns.cda.saas_facturation.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}