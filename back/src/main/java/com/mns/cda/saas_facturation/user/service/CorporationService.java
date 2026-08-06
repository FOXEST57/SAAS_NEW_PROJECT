package com.mns.cda.saas_facturation.user.service;

import com.mns.cda.saas_facturation.location.model.Address;
import com.mns.cda.saas_facturation.location.repository.AddressRepository;
import com.mns.cda.saas_facturation.user.DTO.CorporationDTO;
import com.mns.cda.saas_facturation.user.DTO.requestDTO.CorporationRequestDTO;
import com.mns.cda.saas_facturation.user.mapper.CorporationMapper;
import com.mns.cda.saas_facturation.user.model.Corporation;
import com.mns.cda.saas_facturation.user.model.Customer;
import com.mns.cda.saas_facturation.user.repository.CorporationRepository;

import com.mns.cda.saas_facturation.user.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CorporationService implements com.mns.cda.saas_facturation.user.Iservice.ICorporationService {

    private final CorporationRepository corporationRepository;
    private final CorporationMapper corporationMapper;
    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;

    @Override
    public List<CorporationDTO> findAll() {
        return corporationRepository.findAll()
                .stream()
                .map(corporationMapper::toDTO)
                .toList();
    }

    @Override
    public Optional<CorporationDTO> findById(Long id) {
        return corporationRepository.findById(id)
                .map(corporationMapper::toDTO);
    }

    @Override
    public CorporationDTO create(CorporationRequestDTO corporationRequestDTO, Long ownerId) {
        Address address = addressRepository.findById(corporationRequestDTO.addId())
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));

        Customer owner = customerRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));

        Corporation corporation = new Corporation();
        corporation.setCorpName(corporationRequestDTO.corpName());
        corporation.setCorpSiret(corporationRequestDTO.corpSiret());
        corporation.setCorpEmail(corporationRequestDTO.corpEmail());
        corporation.setCorpPhone(corporationRequestDTO.corpPhone());
        corporation.setCorpTva(corporationRequestDTO.corpTva());
        corporation.setCorpIban(corporationRequestDTO.corpIban());

        corporation.setCorpPreRefQuote(corporationRequestDTO.corpPreRefQuote());
        corporation.setCorpPreRefInvoice(corporationRequestDTO.corpPreRefInvoice());
        corporation.setCorpTag(corporationRequestDTO.corpTag());

        corporation.setAddress(address);
        corporation.setOwner(owner);

        return corporationMapper.toDTO(corporationRepository.save(corporation));
    }

    @Transactional
    @Override
    public void delete(Long id) {
        corporationRepository.deleteById(id);
    }

    @Override
    public CorporationDTO update(Long id, CorporationRequestDTO corporationRequestDTO) {
        Corporation corporation = corporationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Corporation not found"));

        Address address = addressRepository.findById(corporationRequestDTO.addId())
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));

        corporation.setCorpName(corporationRequestDTO.corpName());
        corporation.setCorpSiret(corporationRequestDTO.corpSiret());
        corporation.setCorpEmail(corporationRequestDTO.corpEmail());
        corporation.setCorpPhone(corporationRequestDTO.corpPhone());
        corporation.setCorpTva(corporationRequestDTO.corpTva());
        corporation.setCorpIban(corporationRequestDTO.corpIban());
        corporation.setCorpPreRefQuote(corporationRequestDTO.corpPreRefQuote());
        corporation.setCorpPreRefInvoice(corporationRequestDTO.corpPreRefInvoice());
        corporation.setCorpTag(corporationRequestDTO.corpTag());
        corporation.setAddress(address);

        return corporationMapper.toDTO(corporationRepository.save(corporation));
    }

}
