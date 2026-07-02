package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.AddressDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.AddressRequestDTO;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

public interface IAddressService {

    List<AddressDTO> findAll();

    Optional<AddressDTO> findById(Long addressId);

    AddressDTO create(AddressRequestDTO dto) throws ResourceNotFoundException;

    AddressDTO update(Long addressId, AddressRequestDTO dto) throws ResourceNotFoundException;

    void delete(Long addressId) throws ResourceNotFoundException;

}
