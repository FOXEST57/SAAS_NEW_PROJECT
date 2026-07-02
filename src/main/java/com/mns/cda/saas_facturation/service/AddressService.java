package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.AddressDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.AddressRequestDTO;
import com.mns.cda.saas_facturation.Iservice.IAddressService;
import com.mns.cda.saas_facturation.Iservice.ICityService;
import com.mns.cda.saas_facturation.Iservice.IPostalCodeCityService;
import com.mns.cda.saas_facturation.Iservice.IPostalCodeService;
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
        return addressRepository.findById(addId)
                .map(addressMapper::toDTO);
    }

    @Override
    public AddressDTO create(AddressRequestDTO dto)
            throws IPostalCodeService.PostalCodeNotFoundException,
            ICityService.CityNotFoundException,
            IPostalCodeCityService.PostalCodeCityNotFoundException {

        PostalCode postalCode = postalCodeRepository.findById(dto.pCodeId())
                .orElseThrow(IPostalCodeService.PostalCodeNotFoundException::new);

        City city = cityRepository.findById(dto.cityId())
                .orElseThrow(ICityService.CityNotFoundException::new);

        postalCodeCityRepository.findById(new PostalCodeCity.PostalCodeCityId(postalCode.getPCodeId(),city.getCityId()))
                .orElseThrow(IPostalCodeCityService.PostalCodeCityNotFoundException::new);

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
    public AddressDTO update(Long addId, AddressRequestDTO dto)
            throws AddressNotFoundException,
            IPostalCodeService.PostalCodeNotFoundException,
            ICityService.CityNotFoundException,
            IPostalCodeCityService.PostalCodeCityNotFoundException {

        Address address = addressRepository.findById(addId)
                .orElseThrow(AddressNotFoundException::new);

        PostalCode postalCode = postalCodeRepository.findById(dto.pCodeId())
                .orElseThrow(IPostalCodeService.PostalCodeNotFoundException::new);

        City city = cityRepository.findById(dto.cityId())
                .orElseThrow(ICityService.CityNotFoundException::new);

        postalCodeCityRepository.findById(new PostalCodeCity.PostalCodeCityId(postalCode.getPCodeId(),city.getCityId()))
                .orElseThrow(IPostalCodeCityService.PostalCodeCityNotFoundException::new);

        address.setAddNumber(dto.addNumber());
        address.setAddStreet(dto.addStreet());
        address.setAddComplement(dto.addComplement());
        address.setPostalCode(postalCode);
        address.setCity(city);

        return addressMapper.toDTO(addressRepository.save(address));
    }

    @Override
    public void delete(Long addId) throws AddressNotFoundException {
        Address address = addressRepository.findById(addId)
                .orElseThrow(AddressNotFoundException::new);
        
        addressRepository.delete(address);
    }
}
