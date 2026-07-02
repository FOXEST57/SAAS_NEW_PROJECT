package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.CustomerDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.CustomerRequestDTO;
import com.mns.cda.saas_facturation.Iservice.IAddressService;
import com.mns.cda.saas_facturation.Iservice.ICustomerService;
import com.mns.cda.saas_facturation.mapper.AddressMapper;
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
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

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
    public CustomerDTO create(CustomerRequestDTO dto) throws IAddressService.AddressNotFoundException {
        Address address = addressRepository.findById(dto.addId()).orElseThrow(IAddressService.AddressNotFoundException::new);

        Customer customer = new Customer();
        customer.setCtmFirstName(dto.ctmFirstName());
        customer.setCtmLastName(dto.ctmLastName());
        customer.setCtmEmail(dto.ctmEmail());
        customer.setCtmPhone(dto.ctmPhone());
        customer.setAddress(address);

        return toDTO(customerRepository.save(customer));
    }

    @Override
    public CustomerDTO update(Long ctmId, CustomerRequestDTO dto) throws CustomerNotFoundException, IAddressService.AddressNotFoundException {
        Customer customer = customerRepository.findById(ctmId).orElseThrow(CustomerNotFoundException::new);
        Address address = addressRepository.findById(dto.addId()).orElseThrow(IAddressService.AddressNotFoundException::new);

        customer.setCtmFirstName(dto.ctmFirstName());
        customer.setCtmLastName(dto.ctmLastName());
        customer.setCtmEmail(dto.ctmEmail());
        customer.setCtmPhone(dto.ctmPhone());
        customer.setAddress(address);

        return toDTO(customerRepository.save(customer));
    }

    @Override
    public void delete(Long ctmId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(ctmId).orElseThrow(CustomerNotFoundException::new);

        customerRepository.delete(customer);
    }

    @Override
    public CustomerDTO toDTO(Customer customer) {
        return new CustomerDTO(
                customer.getCtmFirstName(),
                customer.getCtmLastName(),
                customer.getCtmEmail(),
                customer.getCtmPhone(),
                addressMapper.toDTO(customer.getAddress())
        );
    }

}
