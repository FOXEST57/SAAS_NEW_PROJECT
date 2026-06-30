package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.MakerDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.MakerRequestDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.MakerResponseDTO;
import com.mns.cda.saas_facturation.Iservice.IMakerService;
import com.mns.cda.saas_facturation.model.Maker;
import com.mns.cda.saas_facturation.repository.MakerRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MakerService implements IMakerService {

    private final MakerRepository makerRepository;

    @Override
    public List<MakerDTO> findAll() {
        return makerRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public MakerDTO findById(Long id) throws MakerNotFoundException {
        return toDto(makerRepository.findById(id)
                .orElseThrow(IMakerService.MakerNotFoundException::new));
    }

    @Override
    public MakerDTO create(MakerRequestDTO dto) {

        Maker maker = new Maker(
                null,
                dto.mkrName()
        );

        return toDto(makerRepository.save(maker));
    }

    @Override
    public void delete(Long id) throws MakerNotFoundException {

        Maker maker = makerRepository.findById(id)
                .orElseThrow(IMakerService.MakerNotFoundException::new);

        makerRepository.delete(maker);
    }

    @Override
    public MakerDTO modify(Long id, MakerRequestDTO dto) throws IMakerService.MakerNotFoundException {

        Maker maker = makerRepository.findById(id)
                .orElseThrow(IMakerService.MakerNotFoundException::new);

        maker.setMkrName(dto.mkrName());

        return toDto(makerRepository.save(maker));
    }

    @Override
    public MakerDTO toDto(Maker maker) {
        return new MakerDTO(
                maker.getMkrId(),
                maker.getMkrName()
        );
    }

    @Override
    public MakerResponseDTO toResponseDTO (Maker maker) {
        return new MakerResponseDTO(
                maker.getMkrId(),
                maker.getMkrName()
        );
    }
}
