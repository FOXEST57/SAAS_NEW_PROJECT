package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.CartDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.CartRequestDTO;
import com.mns.cda.saas_facturation.Iservice.ICartService;
import com.mns.cda.saas_facturation.Iservice.ICustomerService;
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
    public CartDTO findById(Long id) throws ICartService.CartNotFoundException {
        return cartRepository.findById(id).map(cartMapper::toDTO)
                .orElseThrow(ICartService.CartNotFoundException::new);

    }

    @Override
    public CartDTO create(CartRequestDTO dto) throws ICustomerService.CustomerNotFoundException {

        Customer customer = customerRepository.findById(dto.customerId())
                .orElseThrow(ICustomerService.CustomerNotFoundException::new);

        Cart cart = new Cart();
        cart.setCrtRef(dto.crtRef());
        cart.setCrtStatus(dto.crtStatus());
        cart.setCustomer(customer);

        return cartMapper.toDTO(cartRepository.save(cart));
    }

    @Override
    public CartDTO modify(Long id, CartRequestDTO dto) throws ICartService.CartNotFoundException, ICustomerService.CustomerNotFoundException {

        Cart cart = cartRepository.findById(id)
                .orElseThrow(ICartService.CartNotFoundException::new);

        Customer customer = customerRepository.findById(dto.customerId())
                .orElseThrow(ICustomerService.CustomerNotFoundException::new);

        cart.setCrtRef(dto.crtRef());
        cart.setCrtStatus(dto.crtStatus());
        cart.setCustomer(customer);

        return cartMapper.toDTO(cartRepository.save(cart));
    }

    @Override
    public void delete(Long id) throws ICartService.CartNotFoundException {
        cartRepository.delete(cartRepository.findById(id)
                .orElseThrow(ICartService.CartNotFoundException::new));
    }

}
