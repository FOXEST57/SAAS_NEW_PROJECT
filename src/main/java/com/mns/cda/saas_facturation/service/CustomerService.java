package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.CustomerDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.CustomerRequestDTO;
import com.mns.cda.saas_facturation.Iservice.ICustomerService;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.mapper.CustomerMapper;
import com.mns.cda.saas_facturation.model.Address;
import com.mns.cda.saas_facturation.model.Customer;
import com.mns.cda.saas_facturation.repository.AddressRepository;
import com.mns.cda.saas_facturation.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomerService implements ICustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final AddressRepository addressRepository;

    @Override
    public List<CustomerDTO> findAll() {
        return customerRepository.findAll()
                .stream()
                .map(customerMapper::toDTO)
                .toList();
    }

    @Override
    public Optional<CustomerDTO> findById(Long ctmId) {
        return customerRepository.findById(ctmId)
                .map(customerMapper::toDTO);
    }

    @Override
    public CustomerDTO create(CustomerRequestDTO dto) throws ResourceNotFoundException {
        Address address = addressRepository.findById(dto.addId()).orElseThrow(() -> new ResourceNotFoundException("Adresse non existante"));

        Customer customer = new Customer();
        customer.setCtmFirstName(dto.ctmFirstName());
        customer.setCtmLastName(dto.ctmLastName());
        customer.setCtmEmail(dto.ctmEmail());
        customer.setCtmPhone(dto.ctmPhone());
        customer.setAddress(address);

        return customerMapper.toDTO(customerRepository.save(customer));
    }

    @Override
    public CustomerDTO update(Long ctmId, CustomerRequestDTO dto) throws ResourceNotFoundException {
        Customer customer = customerRepository.findById(ctmId).orElseThrow(() -> new ResourceNotFoundException("Client non existant"));
        Address address = addressRepository.findById(dto.addId()).orElseThrow(() -> new ResourceNotFoundException("Adresse non existante"));

        customer.setCtmFirstName(dto.ctmFirstName());
        customer.setCtmLastName(dto.ctmLastName());
        customer.setCtmEmail(dto.ctmEmail());
        customer.setCtmPhone(dto.ctmPhone());
        customer.setAddress(address);

        return customerMapper.toDTO(customerRepository.save(customer));
    }

    @Override
    public void delete(Long ctmId) throws ResourceNotFoundException {
        Customer customer = customerRepository.findById(ctmId).orElseThrow(() -> new ResourceNotFoundException("Client non existant"));

        customerRepository.delete(customer);
    }

}
