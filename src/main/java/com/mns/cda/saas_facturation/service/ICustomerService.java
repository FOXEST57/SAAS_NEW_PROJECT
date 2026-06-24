package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.model.Customer;

import java.util.List;
import java.util.Optional;

public interface ICustomerService {

    public static class CustomerNotFoundException extends Exception {};

    List<Customer> findAll();

    Optional<Customer> findById(Long ctmId);

    void create(Customer customer);

    void modify(Long ctmId, Customer customer) throws CustomerNotFoundException;

    void delete(Long ctmId) throws CustomerNotFoundException;
}
