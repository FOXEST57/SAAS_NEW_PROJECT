package com.mns.cda.saas_facturation.Service;

import com.mns.cda.saas_facturation.Exception.RessourceIntrouvableException;
import com.mns.cda.saas_facturation.Model.SupplierModel;
import com.mns.cda.saas_facturation.Repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierService implements ISupplierService {

    private final SupplierRepository supplierRepository;

    @Override
    public List<SupplierModel> findAll() {
        return supplierRepository.findAll();
    }

    @Override
    public SupplierModel findById(long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Fournisseur introuvable"));
    }

    @Override
    public void create(SupplierModel supplierModel) {
        supplierModel.setSplId(0L);
        supplierRepository.save(supplierModel);
    }

    @Override
    public void delete(long id) {
        if (!supplierRepository.existsById(id)) {
            throw new RessourceIntrouvableException("Fournisseur introuvable");
        }
        supplierRepository.deleteById(id);
    }

    @Override
    public void update(long id, SupplierModel supplierToUpdate) {
        if (!supplierRepository.existsById(id)) {
            throw new RessourceIntrouvableException("Fournisseur introuvable");
        }
        supplierToUpdate.setSplId(id);
        supplierRepository.save(supplierToUpdate);
    }

}