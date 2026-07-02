package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.CustomerDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.CustomerRequestDTO;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

public interface ICustomerService {

    List<CustomerDTO> findAll();

    Optional<CustomerDTO> findById(Long ctmId);

    CustomerDTO create(CustomerRequestDTO customer) throws ResourceNotFoundException;

    CustomerDTO update(Long ctmId, CustomerRequestDTO customer) throws ResourceNotFoundException;

    void delete(Long ctmId) throws ResourceNotFoundException;

}
