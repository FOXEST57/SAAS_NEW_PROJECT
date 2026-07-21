package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.MakerDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.MakerRequestDTO;
import com.mns.cda.saas_facturation.Iservice.IMakerService;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.mapper.MakerMapper;
import com.mns.cda.saas_facturation.model.Address;
import com.mns.cda.saas_facturation.model.Maker;
import com.mns.cda.saas_facturation.repository.AddressRepository;
import com.mns.cda.saas_facturation.repository.MakerRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MakerService implements IMakerService {

    private final MakerRepository makerRepository;
    private final MakerMapper makerMapper;
    private final AddressRepository addressRepository;

    @Override
    public List<MakerDTO> findAll() {
        return makerRepository.findAll()
                .stream()
                .map(makerMapper::toDto)
                .toList();
    }

    @Override
    public MakerDTO findById(Long id) throws ResourceNotFoundException {
        return makerMapper.toDto(makerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fabricant non existant")));
    }

    @Override
    public MakerDTO create(MakerRequestDTO dto) {

        Address address = addressRepository.findById(dto.addressId())
                .orElseThrow(() -> new ResourceNotFoundException("Adresse non existante"));

        Maker maker = new Maker(
                null,
                dto.mkrName(),
                dto.mkrEmail(),
                dto.mkrPhone(),
                address
        );

        return makerMapper.toDto(makerRepository.save(maker));
    }

    @Override
    public void delete(Long id) throws ResourceNotFoundException {

        Maker maker = makerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fabricant non existant"));

        makerRepository.delete(maker);
    }

    @Override
    public MakerDTO modify(Long id, MakerRequestDTO dto) throws ResourceNotFoundException {

        Maker maker = makerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fabricant non existant"));

        Address address = addressRepository.findById(dto.addressId())
                .orElseThrow(() -> new ResourceNotFoundException("Adresse non existante"));

        maker.setMkrName(dto.mkrName());
        maker.setMkrEmail(dto.mkrEmail());
        maker.setMkrPhone(dto.mkrPhone());
        maker.setAddress(address);

        return makerMapper.toDto(makerRepository.save(maker));
    }
}
