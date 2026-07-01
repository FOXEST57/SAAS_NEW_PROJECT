package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.AddressDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.AddressRequestDTO;
import com.mns.cda.saas_facturation.model.Address;

import java.util.List;
import java.util.Optional;

public interface IAddressService {

    public static class AddressNotFoundException extends Exception {};

    List<AddressDTO> findAll();

    Optional<AddressDTO> findById(Long addressId);

    AddressDTO create(AddressRequestDTO dto) throws IPostalCodeService.PostalCodeNotFoundException, ICityService.CityNotFoundException, IPostalCodeCityService.PostalCodeCityNotFoundException;

    AddressDTO update(Long addressId, AddressRequestDTO dto) throws AddressNotFoundException, IPostalCodeService.PostalCodeNotFoundException, ICityService.CityNotFoundException, IPostalCodeCityService.PostalCodeCityNotFoundException;

    void delete(Long addressId) throws AddressNotFoundException;

    AddressDTO toDTO(Address address);

}
