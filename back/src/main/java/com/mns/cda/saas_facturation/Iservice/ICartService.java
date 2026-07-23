package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.CartDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.CartRequestDTO;

import java.util.List;

public interface ICartService {
    List<CartDTO> findAll();

    CartDTO findById(Long id);

    CartDTO create(CartRequestDTO dto);

    CartDTO modify(Long id, CartRequestDTO dto);

    void delete(Long id);

}
