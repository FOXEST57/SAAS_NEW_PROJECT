package com.mns.cda.saas_facturation.product.mapper;

import com.mns.cda.saas_facturation.location.mapper.AddressMapper;
import com.mns.cda.saas_facturation.product.DTO.MakerDTO;
import com.mns.cda.saas_facturation.product.model.Maker;
import com.mns.cda.saas_facturation.product.model.MakerReference;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MakerMapper {

    private final AddressMapper addressMapper;

    public MakerDTO toDto(Maker maker) {
        return new MakerDTO(
                maker.getMkrId(),
                maker.getMkrName(),
                maker.getMkrEmail(),
                maker.getMkrPhone(),
                addressMapper.toDTO(maker.getAddress())
        );
    }

    public MakerDTO ReferenceToDTO (MakerReference makerReference) {
        Maker maker = makerReference.getMaker();
        return new MakerDTO(
                maker.getMkrId(),
                maker.getMkrName(),
                maker.getMkrEmail(),
                maker.getMkrPhone(),
                addressMapper.toDTO(maker.getAddress())
        );
    }
}
