package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.AddressDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.AddressRequestDTO;
import com.mns.cda.saas_facturation.Iservice.IAddressService;
import com.mns.cda.saas_facturation.Iservice.ICityService;
import com.mns.cda.saas_facturation.Iservice.IPostalCodeCityService;
import com.mns.cda.saas_facturation.Iservice.IPostalCodeService;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.mapper.AddressMapper;
import com.mns.cda.saas_facturation.model.Address;
import com.mns.cda.saas_facturation.model.City;
import com.mns.cda.saas_facturation.model.PostalCode;
import com.mns.cda.saas_facturation.model.PostalCodeCity;
import com.mns.cda.saas_facturation.repository.AddressRepository;
import com.mns.cda.saas_facturation.repository.CityRepository;
import com.mns.cda.saas_facturation.repository.PostalCodeCityRepository;
import com.mns.cda.saas_facturation.repository.PostalCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AddressService implements IAddressService {

    private final AddressRepository addressRepository;
    private final PostalCodeRepository postalCodeRepository;
    private final CityRepository cityRepository;
    private final PostalCodeCityRepository postalCodeCityRepository;
    private final AddressMapper addressMapper;

    @Override
    public List<AddressDTO> findAll() {
        return addressRepository.findAll()
                .stream()
                .map(addressMapper::toDTO)
                .toList();
    }

    @Override
    public Optional<AddressDTO> findById(Long addId) {
        return addressRepository.findById(addId).map(addressMapper::toDTO);
    }

    @Override
    public AddressDTO create(AddressRequestDTO dto) throws ResourceNotFoundException {
        PostalCode postalCode = postalCodeRepository.findById(dto.pCodeId())
                .orElseThrow(() -> new ResourceNotFoundException("Code postal non existant"));
        City city = cityRepository.findById(dto.cityId())
                .orElseThrow(() -> new ResourceNotFoundException("Ville non existante"));
        postalCodeCityRepository.findById(new PostalCodeCity.PostalCodeCityId(postalCode.getPCodeId(),city.getCityId()))
                .orElseThrow(() -> new ResourceNotFoundException("Lien entre code postal et ville non existant"));

        Address address = new Address(
                null,
                dto.addNumber(),
                dto.addStreet(),
                dto.addComplement(),
                postalCode,
                city,
                null
        );

        return addressMapper.toDTO(addressRepository.save(address));
    }

    @Override
    public AddressDTO update(Long addId, AddressRequestDTO dto) throws ResourceNotFoundException {
        Address address = addressRepository.findById(addId)
                .orElseThrow(() -> new ResourceNotFoundException("Adresse non existente"));
        PostalCode postalCode = postalCodeRepository.findById(dto.pCodeId())
                .orElseThrow(() -> new ResourceNotFoundException("Code postal non exitant"));
        City city = cityRepository.findById(dto.cityId()).orElseThrow(() -> new ResourceNotFoundException("Ville non existante"));
        postalCodeCityRepository.findById(new PostalCodeCity.PostalCodeCityId(postalCode.getPCodeId(),city.getCityId()))
                .orElseThrow(() -> new ResourceNotFoundException("Lien entre code postal et ville non existant"));

        address.setAddNumber(dto.addNumber());
        address.setAddStreet(dto.addStreet());
        address.setAddComplement(dto.addComplement());
        address.setPostalCode(postalCode);
        address.setCity(city);

        return addressMapper.toDTO(addressRepository.save(address));
    }

    @Override
    public void delete(Long addId) throws ResourceNotFoundException {
        Address address = addressRepository.findById(addId)
                .orElseThrow(() -> new ResourceNotFoundException("Adresse non existante"));
        
        addressRepository.delete(address);
    }
}
