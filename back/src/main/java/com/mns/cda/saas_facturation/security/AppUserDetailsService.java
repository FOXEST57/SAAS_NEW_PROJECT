package com.mns.cda.saas_facturation.security;

import com.mns.cda.saas_facturation.model.Customer;
import com.mns.cda.saas_facturation.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    protected final CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Customer> optionalUserDetails = customerRepository.findByEmail(username);

        if (optionalUserDetails.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }
        return new AppUserDetails(optionalUserDetails.get());
    }
}
