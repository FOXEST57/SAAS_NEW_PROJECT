package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.CustomerDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.CustomerRequestDTO;
import com.mns.cda.saas_facturation.Iservice.ICityService;
import com.mns.cda.saas_facturation.Iservice.ICustomerService;
import com.mns.cda.saas_facturation.model.City;
import com.mns.cda.saas_facturation.model.Customer;
import com.mns.cda.saas_facturation.repository.CityRepository;
import com.mns.cda.saas_facturation.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomerService implements ICustomerService {

    private final CustomerRepository customerRepository;
    private final CityRepository cityRepository;
    private final CityService cityService;

    @Override
    public List<CustomerDTO> findAll() {
        return customerRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public Optional<CustomerDTO> findById(Long ctmId) {
        return customerRepository.findById(ctmId)
                .map(this::toDTO);
    }

    @Override
    public void create(CustomerRequestDTO dto) throws ICityService.CityNotFoundException {
        City city = cityRepository.findById(dto.cityId()).orElseThrow(ICityService.CityNotFoundException::new);

        Customer customer = new Customer(
                null,
                dto.ctmFirstName(),
                dto.ctmLastName(),
                dto.ctmEmail(),
                dto.ctmPhone(),
                dto.ctmAddress(),
                null,
                null,
                city
        );

        customerRepository.save(customer);
    }

    @Override
    public CustomerDTO modify(Long ctmId, CustomerRequestDTO dto) throws CustomerNotFoundException, ICityService.CityNotFoundException {
        Customer customer = customerRepository.findById(ctmId).orElseThrow(CustomerNotFoundException::new);
        City city = cityRepository.findById(dto.cityId()).orElseThrow(ICityService.CityNotFoundException::new);

        customer.setCtmFirstName(dto.ctmFirstName());
        customer.setCtmLastName(dto.ctmLastName());
        customer.setCtmEmail(dto.ctmEmail());
        customer.setCtmPhone(dto.ctmPhone());
        customer.setCtmAddress(dto.ctmAddress());
        customer.setCtmModificationDate(LocalDateTime.now());
        customer.setCity(city);

        return toDTO(customerRepository.save(customer));
    }

    @Override
    public void delete(Long ctmId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(ctmId).orElseThrow(CustomerNotFoundException::new);

        customerRepository.delete(customer);
    }

    protected CustomerDTO toDTO(Customer customer) {
        return new CustomerDTO(
                customer.getCtmFirstName(),
                customer.getCtmLastName(),
                customer.getCtmEmail(),
                customer.getCtmPhone(),
                customer.getCtmAddress(),
                cityService.toDTO(customer.getCity())
        );
    }

}
