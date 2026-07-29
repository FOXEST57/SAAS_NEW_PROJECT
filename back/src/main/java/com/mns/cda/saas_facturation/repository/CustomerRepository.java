package com.mns.cda.saas_facturation.repository;

import com.mns.cda.saas_facturation.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByCtmEmail(String email);
}