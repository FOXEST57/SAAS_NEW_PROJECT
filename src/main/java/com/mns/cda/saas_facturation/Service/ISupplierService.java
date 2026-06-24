package com.mns.cda.saas_facturation.Service;

import com.mns.cda.saas_facturation.Model.SupplierModel;

import java.util.List;
import java.util.Optional;

public interface ISupplierService {
    List<SupplierModel> findAll();

    SupplierModel findById(long id);

    void create(SupplierModel supplierModel);

    void delete(long id);

    void update(long id, SupplierModel supplierToUpdate);
}
