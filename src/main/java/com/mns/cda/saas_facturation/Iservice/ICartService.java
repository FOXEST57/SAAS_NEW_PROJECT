package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.CartDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.CartRequestDTO;

import java.util.List;

public interface ICartService {
    List<CartDTO> findAll();

    CartDTO findById(Long id) throws CartNotFoundException;

    CartDTO create(CartRequestDTO dto) throws ICustomerService.CustomerNotFoundException;

    CartDTO modify(Long id, CartRequestDTO dto) throws CartNotFoundException, ICustomerService.CustomerNotFoundException;

    void delete(Long id) throws CartNotFoundException;

    public static class CartNotFoundException extends Exception {}
}
