package com.mns.cda.saas_facturation.mapper;

import com.mns.cda.saas_facturation.DTO.AddressDTO;
import com.mns.cda.saas_facturation.model.Address;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AddressMapper {

    private final PostalCodeMapper postalCodeMapper;
    private final CityMapper cityMapper;

    public AddressDTO toDTO(Address address) {
        return new AddressDTO(
                address.getAddId(),
                address.getAddNumber(),
                address.getAddStreet(),
                address.getAddComplement(),
                postalCodeMapper.toDTO(address.getPostalCode()),
                cityMapper.toDTO(address.getCity())
        );
    }
}
