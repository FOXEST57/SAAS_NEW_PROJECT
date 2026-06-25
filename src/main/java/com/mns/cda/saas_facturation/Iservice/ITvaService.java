package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.TvaRequestDTO;
import com.mns.cda.saas_facturation.DTO.UpdateTvaTauxDTO;
import com.mns.cda.saas_facturation.model.Tva;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ITvaService {

    public static class TvaNotFoundException extends Exception {}

    List<Tva> findAll();

    Optional<Tva> findById(Long id);

    Tva create(TvaRequestDTO tva);

    void delete(Long id);

    Tva patchTaux(long id, BigDecimal tvaTaux) throws TvaNotFoundException;

    Tva update(long id, TvaRequestDTO tva) throws TvaNotFoundException;
}
