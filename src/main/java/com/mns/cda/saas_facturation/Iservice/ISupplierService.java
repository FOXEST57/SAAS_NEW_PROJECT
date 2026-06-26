package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.ArticleDTO;
import com.mns.cda.saas_facturation.DTO.SupplierDTO;
import com.mns.cda.saas_facturation.DTO.SupplierRequestDTO;
import com.mns.cda.saas_facturation.model.Supplier;

import java.util.List;
import java.util.Optional;

public interface ISupplierService {

    class SupplierNotFoundException extends Exception { }

    List<SupplierDTO> findAll();

    SupplierDTO findById(Long id) throws SupplierNotFoundException;

    SupplierDTO create(SupplierRequestDTO dto);

    void delete(Long id) throws SupplierNotFoundException;

    SupplierDTO modify(Long id, SupplierRequestDTO dto) throws SupplierNotFoundException;
}
