package com.mns.cda.saas_facturation.mapper;

import com.mns.cda.saas_facturation.DTO.AddressDTO;
import com.mns.cda.saas_facturation.model.Address;
import com.mns.cda.saas_facturation.service.CityService;
import com.mns.cda.saas_facturation.service.PostalCodeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AddressMapper {

    private final PostalCodeService postalCodeService;
    private final CityService cityService;

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
