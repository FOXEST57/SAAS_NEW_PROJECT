package com.mns.cda.saas_facturation.Service;

import com.mns.cda.saas_facturation.Model.SupplierModel;

import java.util.List;
import java.util.Optional;

public interface ISupplierService {

    public static class SupplierNotFoundException extends Exception { }

    List<SupplierModel> findAll();

    Optional<SupplierModel> findById(Long id) throws SupplierNotFoundException;

    void create(SupplierModel supplierModel);

    void delete(Long id) throws SupplierNotFoundException;

    void modify(Long id, SupplierModel supplierToUpdate) throws SupplierNotFoundException;
}
