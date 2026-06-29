package com.mns.cda.saas_facturation.controller;

import com.mns.cda.saas_facturation.DTO.CustomerDTO;
import com.mns.cda.saas_facturation.DTO.CustomerRequestDTO;
import com.mns.cda.saas_facturation.Iservice.ICityService;
import com.mns.cda.saas_facturation.model.Customer;
import com.mns.cda.saas_facturation.Iservice.ICustomerService;
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
    public ResponseEntity<List<CustomerDTO>> getCustomers() {
        return new ResponseEntity<>(customerService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{ctmId}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long ctmId) {
        Optional<CustomerDTO> optionalCustomer = customerService.findById(ctmId);

        if (optionalCustomer.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(optionalCustomer.get(), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<Void> createCustomer(@RequestBody @Valid CustomerRequestDTO customer) throws ICityService.CityNotFoundException {
        customerService.create(customer);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{ctmId}")
    public ResponseEntity<CustomerDTO> modifyCustomer(@PathVariable Long ctmId, @RequestBody @Valid CustomerRequestDTO customer) throws ICustomerService.CustomerNotFoundException, ICityService.CityNotFoundException {
            CustomerDTO customerModified = customerService.modify(ctmId, customer);

            return new ResponseEntity<>(customerModified, HttpStatus.OK);
    }

    @DeleteMapping("{ctmId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long ctmId) throws ICustomerService.CustomerNotFoundException {
        customerService.delete(ctmId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
