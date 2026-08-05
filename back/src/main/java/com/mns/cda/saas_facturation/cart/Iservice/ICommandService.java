package com.mns.cda.saas_facturation.cart.Iservice;

import com.mns.cda.saas_facturation.cart.DTO.CommandDTO;
import com.mns.cda.saas_facturation.cart.DTO.requestDTO.CommandRequestDTO;
import com.mns.cda.saas_facturation.cart.DTO.patchDTO.PatchCommandStatus;

import java.util.List;

public interface ICommandService {
    List<CommandDTO> findAll();

    CommandDTO findById(Long id);

    CommandDTO create(CommandRequestDTO dto);

    CommandDTO patchStatus(PatchCommandStatus dto);

    CommandDTO delete(Long id);
}
