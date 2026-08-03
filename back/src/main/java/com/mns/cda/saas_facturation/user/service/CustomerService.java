package com.mns.cda.saas_facturation.user.service;

import com.mns.cda.saas_facturation.user.DTO.CustomerDTO;
import com.mns.cda.saas_facturation.user.DTO.requestDTO.CustomerRequestDTO;
import com.mns.cda.saas_facturation.user.Iservice.ICustomerService;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.exception.SameAccountException;
import com.mns.cda.saas_facturation.user.mapper.CustomerMapper;
import com.mns.cda.saas_facturation.user.model.AccountType;
import com.mns.cda.saas_facturation.location.model.Address;
import com.mns.cda.saas_facturation.user.model.Customer;
import com.mns.cda.saas_facturation.user.repository.AccountTypeRepository;
import com.mns.cda.saas_facturation.location.repository.AddressRepository;
import com.mns.cda.saas_facturation.user.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomerService implements ICustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final AddressRepository addressRepository;
    private final AccountTypeRepository accountTypeRepository;
    private final PasswordEncoder passwordEncoder;

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
    public CustomerDTO create(CustomerRequestDTO dto) throws ResourceNotFoundException, SameAccountException {
        Address address = addressRepository.findById(dto.addId()).orElseThrow(() -> new ResourceNotFoundException("Adresse non existante"));
        AccountType accountType = accountTypeRepository.findById(dto.accTypeId()).orElseThrow(() -> new ResourceNotFoundException("Type de compte non existant"));
        List<Customer> customers = dto.customerIds() != null
                ? new ArrayList<>(dto.customerIds()
                    .stream()
                    .map(id -> {
                            Customer cust = customerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Client non existant"));
                            if (cust.getAccountType() == accountType) {
                                throw new SameAccountException("Le type de compte " + accountType + " ne peut pas avoir une liste de clients du même type");
                            }
                            return cust;
                    })
                    .toList()
        )
                : new ArrayList<>();

        Customer customer = new Customer();
        customer.setCtmFirstName(dto.ctmFirstName());
        customer.setCtmLastName(dto.ctmLastName());
        customer.setCtmEmail(dto.ctmEmail());
        customer.setCtmPhone(dto.ctmPhone());
        customer.setAddress(address);
        customer.setAccountType(accountType);
        customer.setPassword(passwordEncoder.encode(dto.password()));
        customer.setCustomers(customers);

        return customerMapper.toDTO(customerRepository.save(customer));
    }

    @Override
    public CustomerDTO update(Long ctmId, CustomerRequestDTO dto) throws ResourceNotFoundException, SameAccountException {
        Customer customer = customerRepository.findById(ctmId).orElseThrow(() -> new ResourceNotFoundException("Client non existant"));
        Address address = addressRepository.findById(dto.addId()).orElseThrow(() -> new ResourceNotFoundException("Adresse non existante"));
        AccountType accountType = accountTypeRepository.findById(dto.accTypeId()).orElseThrow(() -> new ResourceNotFoundException("Type de compte non existant"));
        List<Customer> customers = dto.customerIds() != null
                ? new ArrayList<>(
                        dto.customerIds()
                        .stream()
                        .map(id -> {
                            Customer cust = customerRepository.findById(id)
                                    .orElseThrow(() -> new ResourceNotFoundException("Client non existant"));
                        if (cust.getAccountType() == accountType) {
                        throw new SameAccountException("Le type de compte " + accountType + " ne peut pas avoir une liste de clients du même type");
                         }
                        return cust;
                        })
                        .toList()
                )
                : new ArrayList<>();

        customer.setCtmFirstName(dto.ctmFirstName());
        customer.setCtmLastName(dto.ctmLastName());
        customer.setCtmEmail(dto.ctmEmail());
        customer.setCtmPhone(dto.ctmPhone());
        customer.setAddress(address);
        customer.setAccountType(accountType);
        customer.setCustomers(customers);

        return customerMapper.toDTO(customerRepository.save(customer));
    }

    @Override
    public void delete(Long ctmId) throws ResourceNotFoundException {
        Customer customer = customerRepository.findById(ctmId).orElseThrow(() -> new ResourceNotFoundException("Client non existant"));

        customerRepository.delete(customer);
    }

}
