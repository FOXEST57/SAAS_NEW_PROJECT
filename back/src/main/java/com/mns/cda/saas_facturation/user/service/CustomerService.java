package com.mns.cda.saas_facturation.user.service;

import com.mns.cda.saas_facturation.enumeration.AccountTypeEnum;
import com.mns.cda.saas_facturation.enumeration.InvitationTypeEnum;
import com.mns.cda.saas_facturation.user.DTO.CustomerDTO;
import com.mns.cda.saas_facturation.user.DTO.requestDTO.CreateUserFromInvitationDTO;
import com.mns.cda.saas_facturation.user.DTO.requestDTO.CustomerOwnerRequestDTO;
import com.mns.cda.saas_facturation.user.DTO.updateDTO.CustomerUpdateDTO;
import com.mns.cda.saas_facturation.user.Iservice.ICustomerService;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.exception.SameAccountException;
import com.mns.cda.saas_facturation.user.mapper.CustomerMapper;
import com.mns.cda.saas_facturation.user.model.AccountType;
import com.mns.cda.saas_facturation.location.model.Address;
import com.mns.cda.saas_facturation.user.model.Corporation;
import com.mns.cda.saas_facturation.user.model.Customer;
import com.mns.cda.saas_facturation.user.model.Invitation;
import com.mns.cda.saas_facturation.user.repository.AccountTypeRepository;
import com.mns.cda.saas_facturation.location.repository.AddressRepository;
import com.mns.cda.saas_facturation.user.repository.CustomerRepository;
import com.mns.cda.saas_facturation.user.repository.InvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    private final CorporationService corporationService;
    private final InvitationRepository invitationRepository;
    private final CustomerService customerService;

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
    public CustomerDTO createOwner(CustomerOwnerRequestDTO dto) throws ResourceNotFoundException, SameAccountException {
        Address address = addressRepository.findById(dto.addId()).orElseThrow(() -> new ResourceNotFoundException("Adresse non existante"));
        AccountType accountType = accountTypeRepository.findAccountTypeByAccTypeLibelle(AccountTypeEnum.OWNER);

        // On crée l'utilisateur
        Customer customer = new Customer();
        customer.setCtmFirstName(dto.ctmFirstName());
        customer.setCtmLastName(dto.ctmLastName());
        customer.setCtmEmail(dto.ctmEmail());
        customer.setCtmPhone(dto.ctmPhone());
        customer.setAddress(address);
        customer.setAccountType(accountType);
        customer.setPassword(passwordEncoder.encode(dto.password()));

        customerRepository.save(customer);

        //On lui crée son entreprise
        Corporation corporation = corporationService.create(dto.corporation(), customer.getCtmId());

        customer.setCorporation(corporation);

        return customerMapper.toDTO(customerRepository.save(customer));

    }

    @Override
    public CustomerDTO createCustomer(CreateUserFromInvitationDTO dto) {

        Invitation invitation = invitationRepository.findByInvToken(dto.token())
                .orElseThrow(() -> new IllegalArgumentException("Token d'invitation invalide"));


        if (invitation.isUsed()) {
            throw new IllegalStateException("Invitation déjà utilisée");
        }
        if (invitation.getInvExpirationDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Invitation expirée");
        }
        Address address = addressRepository.findById(dto.addressId())
                .orElseThrow(() -> new ResourceNotFoundException("Adresse non existante"));

        Customer customer = new Customer();
        customer.setCtmFirstName(dto.firstName());
        customer.setCtmLastName(dto.lastName());
        customer.setCtmEmail(invitation.getInvEmail());
        customer.setCtmPhone(dto.phone());
        customer.setPassword(passwordEncoder.encode(dto.password()));
        customer.setAddress(address);

        if (invitation.getInvitationType() == InvitationTypeEnum.EMPLOYEE) {
            customer.setCorporation(invitation.getCorporation());
        }
        if (invitation.getInvitationType() == InvitationTypeEnum.CUSTOMER) {
            customer.getCorporations().add(invitation.getCorporation());
        }
        invitation.setUsed(true);
        invitationRepository.save(invitation);

        return customerMapper.toDTO(customer);
    }

    @Override
    public CustomerDTO update(Long ctmId, CustomerUpdateDTO dto) throws ResourceNotFoundException, SameAccountException {
        Customer customer = customerRepository.findById(ctmId).orElseThrow(() -> new ResourceNotFoundException("Client non existant"));
        Address address = addressRepository.findById(dto.addId()).orElseThrow(() -> new ResourceNotFoundException("Adresse non existante"));

        if (!ctmId.equals(customer.getCtmId())) {
            throw new IllegalArgumentException("Ne peut pas modifier les informations de cet utilisateur.");
        }

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
