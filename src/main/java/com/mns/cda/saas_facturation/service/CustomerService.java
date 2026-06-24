package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.model.Customer;
import com.mns.cda.saas_facturation.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomerService implements ICustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> findById(Long ctmId) {
        return customerRepository.findById(ctmId);
    }

    @Override
    public void create(Customer customer) {
        customer.setCtmId(null);
        customerRepository.save(customer);
    }

    @Override
    public void modify(Long ctmId, Customer customer) throws CustomerNotFoundException {
        Optional<Customer> optionalCustomer = customerRepository.findById(ctmId);

        if (optionalCustomer.isEmpty()) {
            throw new CustomerNotFoundException();
        }

        customer.setCtmId(ctmId);
        customerRepository.save(customer);
    }

    @Override
    public void delete(Long ctmId) throws CustomerNotFoundException {
        Optional<Customer> optionalCustomer = customerRepository.findById(ctmId);

        if (optionalCustomer.isEmpty()) {
            throw new CustomerNotFoundException();
        }

        customerRepository.deleteById(ctmId);
    }

}
