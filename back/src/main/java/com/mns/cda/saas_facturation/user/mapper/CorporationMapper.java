package com.mns.cda.saas_facturation.user.mapper;

import com.mns.cda.saas_facturation.location.DTO.AddressDTO;
import com.mns.cda.saas_facturation.location.mapper.AddressMapper;
import com.mns.cda.saas_facturation.location.model.Address;
import com.mns.cda.saas_facturation.user.DTO.CorporationDTO;
import com.mns.cda.saas_facturation.user.model.Corporation;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CorporationMapper {

    private final AddressMapper addressMapper;

    public CorporationDTO toDTO(Corporation corporation) {
        AddressDTO address = addressMapper.toDTO(corporation.getAddress());


        return new CorporationDTO(
                corporation.getCorpId(),
                corporation.getCorpName(),
                corporation.getCorpSiret(),
                corporation.getCorpCreationDate(),
                corporation.getCorpPreRefQuote(),
                corporation.getCorpPreRefInvoice(),
                corporation.getCorpPreRefCart(),
                corporation.getCorpEmail(),
                corporation.getCorpPhone(),
                corporation.getCorpTva(),
                corporation.getCorpIban(),
                corporation.getCorpTag(),
                address
        );
    }
}
