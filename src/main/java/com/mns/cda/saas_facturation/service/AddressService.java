package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.AddressDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.AddressRequestDTO;
import com.mns.cda.saas_facturation.Iservice.IAddressService;
import com.mns.cda.saas_facturation.Iservice.ICityService;
import com.mns.cda.saas_facturation.Iservice.IPostalCodeService;
import com.mns.cda.saas_facturation.model.Address;
import com.mns.cda.saas_facturation.model.City;
import com.mns.cda.saas_facturation.model.PostalCode;
import com.mns.cda.saas_facturation.repository.AddressRepository;
import com.mns.cda.saas_facturation.repository.CityRepository;
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
    private final PostalCodeService postalCodeService;
    private final CityRepository cityRepository;
    private final CityService cityService;

    @Override
    public List<AddressDTO> findAll() {
        return addressRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public Optional<AddressDTO> findById(Long addId) {
        return addressRepository.findById(addId).map(this::toDTO);
    }

    @Override
    public AddressDTO create(AddressRequestDTO dto) throws IPostalCodeService.PostalCodeNotFoundException, ICityService.CityNotFoundException {
        PostalCode postalCode = postalCodeRepository.findById(dto.pcodeId()).orElseThrow(IPostalCodeService.PostalCodeNotFoundException::new);
        City city = cityRepository.findById(dto.cityId()).orElseThrow(ICityService.CityNotFoundException::new);

        Address address = new Address(
                null,
                dto.addNumber(),
                dto.addStreet(),
                dto.addComplement(),
                postalCode,
                city
        );

        return toDTO(addressRepository.save(address));
    }

    @Override
    public AddressDTO update(Long addId, AddressRequestDTO dto) throws AddressNotFoundException, IPostalCodeService.PostalCodeNotFoundException, ICityService.CityNotFoundException {
        Address address = addressRepository.findById(addId).orElseThrow(AddressNotFoundException::new);
        PostalCode postalCode = postalCodeRepository.findById(dto.pcodeId()).orElseThrow(IPostalCodeService.PostalCodeNotFoundException::new);
        City city = cityRepository.findById(dto.pcodeId()).orElseThrow(ICityService.CityNotFoundException::new);

        address.setAddNumber(dto.addNumber());
        address.setAddStreet(dto.addStreet());
        address.setAddComplement(dto.addComplement());
        address.setPostalCode(postalCode);
        address.setCity(city);

        return toDTO(addressRepository.save(address));
    }

    @Override
    public void delete(Long addId) throws AddressNotFoundException {
        Address address = addressRepository.findById(addId).orElseThrow(AddressNotFoundException::new);
        
        addressRepository.delete(address);
    }

    @Override
    public AddressDTO toDTO(Address address) {
        return new AddressDTO(
            address.getAddNumber(),
            address.getAddStreet(),
            address.getAddComplement(),
            postalCodeService.toDTO(address.getPostalCode()),
            cityService.toDTO(address.getCity())
        );
    }

}
