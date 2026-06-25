package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.Model.SupplierModel;

import java.util.List;
import java.util.Optional;

public interface ISupplierService {

    class SupplierNotFoundException extends Exception { }

    class ExistingSupplierException extends Exception { }


    List<SupplierModel> findAll();

    Optional<SupplierModel> findById(Long id) throws SupplierNotFoundException;

    void create(SupplierModel supplierModel) throws ExistingSupplierException;

    void delete(Long id) throws SupplierNotFoundException;

    void modify(Long id, SupplierModel supplierToUpdate) throws SupplierNotFoundException, ExistingSupplierException;
}
