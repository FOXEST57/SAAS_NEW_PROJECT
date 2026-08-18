package com.mns.cda.saas_facturation.product.service;

import com.mns.cda.saas_facturation.enumeration.DeliveryStatus;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.product.DTO.DeliveryDTO;
import com.mns.cda.saas_facturation.product.DTO.requestDTO.DeliveryRequestDTO;
import com.mns.cda.saas_facturation.product.DTO.updateDTO.UpdateDeliveryDTO;
import com.mns.cda.saas_facturation.product.Iservice.IDeliveryService;
import com.mns.cda.saas_facturation.product.mapper.DeliveryMapper;
import com.mns.cda.saas_facturation.product.model.*;
import com.mns.cda.saas_facturation.product.repository.*;
import com.mns.cda.saas_facturation.product.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryService implements IDeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final MakerReferenceRepository makerReferenceRepository;
    private final SupplierReferenceRepository supplierReferenceRepository;
    private final DeliveryMapper deliveryMapper;
    private final ArticleRepository articleRepository;
    private final MakerRepository makerRepository;

    @Override
    public List<DeliveryDTO> findAll() {
        return deliveryRepository.findAll()
                .stream()
                .map(deliveryMapper::toDto)
                .toList();
    }

    @Override // Utiliser la clé primaire
    public DeliveryDTO findById(Long dlvId) throws ResourceNotFoundException {
        return deliveryMapper.toDto(deliveryRepository.findById(dlvId)
                .orElseThrow(() -> new ResourceNotFoundException("Référence fabricant non existante")));
    }

    @Override
    public DeliveryDTO create(DeliveryRequestDTO dto) throws ResourceNotFoundException {
        if (dto.splReferenceId() == null && dto.mkrReferenceId() == null || dto.splReferenceId() != null && dto.mkrReferenceId() != null) {
            throw new IllegalArgumentException("Une commande doit être liée exclusivement à un fabricant ou à un fournisseur");
        }

        MakerReference makerReference = dto.mkrReferenceId() != null
                ? makerReferenceRepository.findById(dto.mkrReferenceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Fabricant non existant"))
                : null;

        SupplierReference supplierReference = dto.splReferenceId() != null
                ? supplierReferenceRepository.findById(dto.splReferenceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Fournisseur non existant"))
                : null;

        Delivery delivery = new Delivery();
        delivery.setDlvQuantity(dto.dlvQuantity());
        delivery.setDlvBuyingPrice(dto.dlvBuyingPrice());
        delivery.setDlvStatus(DeliveryStatus.ACCEPTED);
        delivery.setMakerReference(makerReference);
        delivery.setSupplierReference(supplierReference);

        return deliveryMapper.toDto(deliveryRepository.save(delivery));
    }

    @Override
    public DeliveryDTO modify(Long dlvId, UpdateDeliveryDTO dto) throws ResourceNotFoundException {
       Delivery delivery = deliveryRepository.findById(dlvId)
               .orElseThrow(() -> new ResourceNotFoundException("Commande non existante"));

       delivery.setDlvQuantity(dto.dlvQuantity());
       delivery.setDlvBuyingPrice(dto.dlvBuyingPrice());

       return deliveryMapper.toDto(deliveryRepository.save(delivery));
    }

    @Override
    public DeliveryDTO modifyStatus(Long dlvId, DeliveryStatus status) {
        Delivery delivery = deliveryRepository.findById(dlvId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non existante"));

        delivery.setDlvStatus(status);

        return deliveryMapper.toDto(deliveryRepository.save(delivery));
    }

    @Override
    public void delete(Long dlvId) throws ResourceNotFoundException {
        deliveryRepository.delete(
                deliveryRepository.findById(dlvId)
                        .orElseThrow(() -> new ResourceNotFoundException("Commande non existante"))
        );
    }
}
