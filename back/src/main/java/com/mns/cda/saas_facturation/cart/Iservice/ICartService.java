package com.mns.cda.saas_facturation.cart.Iservice;

import com.mns.cda.saas_facturation.cart.DTO.CartDTO;
import com.mns.cda.saas_facturation.cart.DTO.patchDTO.PatchCartStatus;
import com.mns.cda.saas_facturation.cart.DTO.requestDTO.CartRequestDTO;
import com.mns.cda.saas_facturation.security.AppUserDetails;
import com.mns.cda.saas_facturation.user.model.Customer;

import java.util.List;

public interface ICartService {
    List<CartDTO> findAll();

    CartDTO findById(Long id);

    CartDTO create(Customer creator, CartRequestDTO dto);

    CartDTO quoteToRevisitedCart(Long quoteId);

    CartDTO modify(Long id, CartRequestDTO dto);

    CartDTO patchStatus(PatchCartStatus dto);

    void delete(Long id);

}
