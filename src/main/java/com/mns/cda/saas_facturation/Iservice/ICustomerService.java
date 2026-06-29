package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.CustomerDTO;
import com.mns.cda.saas_facturation.DTO.CustomerRequestDTO;
import com.mns.cda.saas_facturation.model.Customer;

import java.util.List;
import java.util.Optional;

public interface ICustomerService {

    public static class CustomerNotFoundException extends Exception {};

    List<CustomerDTO> findAll();

    Optional<CustomerDTO> findById(Long ctmId);

    void create(CustomerRequestDTO customer) throws ICityService.CityNotFoundException;

    CustomerDTO modify(Long ctmId, CustomerRequestDTO customer) throws CustomerNotFoundException, ICityService.CityNotFoundException;

    void delete(Long ctmId) throws CustomerNotFoundException;
}
