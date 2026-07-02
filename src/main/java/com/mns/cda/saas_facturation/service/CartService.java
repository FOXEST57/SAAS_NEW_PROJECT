package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.CartDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.CartRequestDTO;
import com.mns.cda.saas_facturation.Iservice.ICartService;
import com.mns.cda.saas_facturation.Iservice.ICustomerService;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.mapper.CartMapper;
import com.mns.cda.saas_facturation.model.Cart;
import com.mns.cda.saas_facturation.model.Customer;
import com.mns.cda.saas_facturation.repository.CartRepository;
import com.mns.cda.saas_facturation.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService {

    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;

    @Override
    public List<CartDTO> findAll() {
        return cartRepository.findAll()
                .stream()
                .map(cartMapper::toDTO)
                .toList();
    }

    @Override
    public CartDTO findById(Long id) {
        return cartRepository.findById(id).map(cartMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Le panier avec l'id " +id+ " n'existe pas" ));

    }

    @Override
    public CartDTO create(CartRequestDTO dto) {

        Customer customer = customerRepository.findById(dto.ctmId())
                .orElseThrow(() -> new ResourceNotFoundException("Le client avec l'id " +dto.ctmId()+ " n'existe pas" ));

        Cart cart = new Cart();
        cart.setCrtRef(dto.crtRef());
        cart.setCrtStatus(dto.crtStatus());
        cart.setCustomer(customer);

        return cartMapper.toDTO(cartRepository.save(cart));
    }

    @Override
    public CartDTO modify(Long id, CartRequestDTO dto) {

        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Le panier avec l'id " +id+ " n'existe pas" ));

        Customer customer = customerRepository.findById(dto.ctmId())
                .orElseThrow(() -> new ResourceNotFoundException("Le client avec l'id " +dto.ctmId()+ " n'existe pas" ));

        cart.setCrtRef(dto.crtRef());
        cart.setCrtStatus(dto.crtStatus());
        cart.setCustomer(customer);

        return cartMapper.toDTO(cartRepository.save(cart));
    }

    @Override
    public void delete(Long id) {
        cartRepository.delete(cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Le panier avec l'id " +id+ " n'existe pas" )));
    }

}
