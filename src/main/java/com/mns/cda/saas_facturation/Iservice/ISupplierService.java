package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.SupplierDTO;
import com.mns.cda.saas_facturation.model.Supplier;

import java.util.List;
import java.util.Optional;

public interface ISupplierService {

    class SupplierNotFoundException extends Exception { }

    class ExistingSupplierException extends Exception { }


    List<SupplierDTO> findAll();

    SupplierDTO findById(Long id) throws SupplierNotFoundException;

    void create(Supplier supplier) throws ExistingSupplierException;

    void delete(Long id) throws SupplierNotFoundException;

    void modify(Long id, Supplier supplierToUpdate) throws SupplierNotFoundException, ExistingSupplierException;
}
