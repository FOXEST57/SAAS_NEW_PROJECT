package com.mns.cda.saas_facturation.Service;

import com.mns.cda.saas_facturation.Exception.RessourceIntrouvableException;
import com.mns.cda.saas_facturation.Model.SupplierModel;
import com.mns.cda.saas_facturation.Repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public List<SupplierModel> findAll() {
        return supplierRepository.findAll();
    }

    public Optional<SupplierModel> findById(long id) {
        return supplierRepository.findById(id);
    }

    public void create(SupplierModel supplierModel) {
        supplierModel.setSplId(0L);
        supplierRepository.save(supplierModel);
    }

    public void delete(long id) {
        if (!supplierRepository.existsById(id)) {
            throw new RessourceIntrouvableException("Fournisseur introuvable");
        }
        supplierRepository.deleteById(id);
    }

    public void update(long id, SupplierModel supplierToUpdate) {
        if (!supplierRepository.existsById(id)) {
            throw new RessourceIntrouvableException("Fournisseur introuvable");
        }
        supplierToUpdate.setSplId(id);
        supplierRepository.save(supplierToUpdate);
    }
}