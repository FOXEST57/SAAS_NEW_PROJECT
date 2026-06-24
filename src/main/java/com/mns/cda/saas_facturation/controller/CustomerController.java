package com.mns.cda.saas_facturation.controller;

import com.mns.cda.saas_facturation.model.Customer;
import com.mns.cda.saas_facturation.service.ICustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RequestMapping("/customer")
@RestController
public class CustomerController {

    private final ICustomerService customerService;

    @GetMapping("/list")
    public ResponseEntity<List<Customer>> getCustomers() {
        return new ResponseEntity<>(customerService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{ctmId}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long ctmId) {
        Optional<Customer> optionalCustomer = customerService.findById(ctmId);

        if (optionalCustomer.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(optionalCustomer.get(), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<Void> createCustomer(@RequestBody @Valid Customer customer) {
        customerService.create(customer);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{ctmId}")
    public ResponseEntity<Customer> modifyCustomer(@PathVariable Long ctmId, @RequestBody @Valid Customer customer) {
        try {
            customerService.modify(ctmId, customer);
            return new ResponseEntity<>(customer, HttpStatus.OK);
        } catch (ICustomerService.CustomerNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("{ctmId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long ctmId) {
        try {
            customerService.delete(ctmId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ICustomerService.CustomerNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
