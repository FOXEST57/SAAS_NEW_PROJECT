package com.mns.cda.saas_facturation.user.Iservice;

import com.mns.cda.saas_facturation.user.DTO.CustomerDTO;
import com.mns.cda.saas_facturation.user.DTO.requestDTO.CustomerRequestDTO;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.exception.SameAccountException;

import java.util.List;
import java.util.Optional;

public interface ICustomerService {

    List<CustomerDTO> findAll();

    Optional<CustomerDTO> findById(Long ctmId);

    CustomerDTO create(CustomerRequestDTO customer) throws ResourceNotFoundException, SameAccountException;

    CustomerDTO update(Long ctmId, CustomerRequestDTO customer) throws ResourceNotFoundException, SameAccountException;

    void delete(Long ctmId) throws ResourceNotFoundException;

}
