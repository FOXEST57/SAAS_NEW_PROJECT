package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.TvaRequestDTO;
import com.mns.cda.saas_facturation.Iservice.ITvaService;
import com.mns.cda.saas_facturation.model.Tva;
import com.mns.cda.saas_facturation.repository.TvaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TvaService implements ITvaService {

    protected final TvaRepository tvaRepository;

    @Override
    public List<Tva> findAll() {
        return tvaRepository.findAll();
    }

    @Override
    public Optional<Tva> findById(Long id) {
        return tvaRepository.findById(id);
    }

    @Override
    public Tva create(TvaRequestDTO tva) {
        Tva tvaEntity = new Tva(
                null,
                tva.tvaName(),
                tva.tvaTaux());

        return tvaRepository.save(tvaEntity);
    }

    @Override
    public void delete(Long id) {
        tvaRepository.deleteById(id);
    }

    @Override
    public Tva patchTaux(long id, BigDecimal tvaTaux) throws TvaNotFoundException {
        Tva tvaEntity = tvaRepository.findById(id)
                .orElseThrow(TvaNotFoundException::new);

        tvaEntity.setTvaTaux(tvaTaux);
        tvaRepository.save(tvaEntity);

        return tvaEntity;
    }
    @Override
    public Tva update(long id, TvaRequestDTO dto) throws TvaNotFoundException {
        Tva tvaEntity = tvaRepository.findById(id)
                .orElseThrow(TvaNotFoundException::new);

        tvaEntity.setTvaName(dto.tvaName());
        tvaEntity.setTvaTaux(dto.tvaTaux());

        return tvaRepository.save(tvaEntity);
    }

}
