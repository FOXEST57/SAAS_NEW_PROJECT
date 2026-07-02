package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.CustomerDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.CustomerRequestDTO;
import com.mns.cda.saas_facturation.model.Customer;

import java.util.List;
import java.util.Optional;

public interface ICustomerService {

    public static class CustomerNotFoundException extends Exception {};

    List<CustomerDTO> findAll();

    Optional<CustomerDTO> findById(Long ctmId);

    CustomerDTO create(CustomerRequestDTO customer) throws IAddressService.AddressNotFoundException;

    CustomerDTO update(Long ctmId, CustomerRequestDTO customer) throws CustomerNotFoundException, IAddressService.AddressNotFoundException;

    void delete(Long ctmId) throws CustomerNotFoundException;

}
