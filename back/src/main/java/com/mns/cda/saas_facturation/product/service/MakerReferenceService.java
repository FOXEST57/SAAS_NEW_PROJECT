package com.mns.cda.saas_facturation.product.service;

import com.mns.cda.saas_facturation.product.DTO.MakerDTO;
import com.mns.cda.saas_facturation.product.DTO.MakerReferenceDTO;
import com.mns.cda.saas_facturation.product.DTO.updateDTO.UpdateMakerReferenceDTO;
import com.mns.cda.saas_facturation.product.DTO.requestDTO.MakerReferenceRequestDTO;
import com.mns.cda.saas_facturation.product.DTO.responseDTO.ArticleResponseMakerReferenceDTO;
import com.mns.cda.saas_facturation.product.Iservice.IMakerReferenceService;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.product.mapper.ArticleMapper;
import com.mns.cda.saas_facturation.product.mapper.MakerMapper;
import com.mns.cda.saas_facturation.product.mapper.MakerReferenceMapper;
import com.mns.cda.saas_facturation.product.model.Article;
import com.mns.cda.saas_facturation.product.model.Maker;
import com.mns.cda.saas_facturation.product.model.MakerReference;
import com.mns.cda.saas_facturation.product.repository.ArticleRepository;
import com.mns.cda.saas_facturation.product.repository.DeliveryRepository;
import com.mns.cda.saas_facturation.product.repository.MakerReferenceRepository;
import com.mns.cda.saas_facturation.product.repository.MakerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MakerReferenceService implements IMakerReferenceService {

    private final MakerReferenceRepository makerReferenceRepository;
    private final ArticleRepository articleRepository;
    private final MakerRepository makerRepository;
    private final MakerReferenceMapper makerReferenceMapper;
    private final MakerMapper makerMapper;
    private final ArticleMapper articleMapper;
    private final DeliveryRepository deliveryRepository;

    @Override
    public List<MakerReferenceDTO> findAll() {
        return makerReferenceRepository.findAll()
                .stream()
                .map(makerReferenceMapper::toDto)
                .toList();
    }

    @Override
    public List<MakerDTO> findAllByArticle(Long artId)  {
        return makerReferenceRepository.findByMkrRefId_ArticleId(artId)
                .stream()
                .map(makerMapper::ReferenceToDTO)
                .toList();
    }

    @Override
    public List<ArticleResponseMakerReferenceDTO> findAllByMaker(Long mkrId)  {
        return makerReferenceRepository.findByMkrRefId_MakerId(mkrId)
                .stream()
                .map(articleMapper::makerReferenceToDTO)
                .toList();
    }

    @Override // Utiliser la clé primaire
    public MakerReferenceDTO findById(Long artId, Long mkrId) throws ResourceNotFoundException {

        return makerReferenceMapper.toDto(makerReferenceRepository.findById(
                new MakerReference.MakerReferenceId(artId,mkrId)
        ).orElseThrow(() -> new ResourceNotFoundException("Référence fabricant non existante")));
    }

    @Override
    public MakerReferenceDTO create(MakerReferenceRequestDTO dto) throws ResourceNotFoundException {
        //

        Article article = articleRepository.findById(dto.artId())
                .orElseThrow(() -> new ResourceNotFoundException("Article non existant"));

        Maker maker = makerRepository.findById(dto.mkrId())
                .orElseThrow(() -> new ResourceNotFoundException("Fabricant non existant"));

        MakerReference makerReference = new MakerReference(
                new MakerReference.MakerReferenceId(dto.artId(), dto.mkrId()),
                article,
                maker,
                dto.deliveryIds().stream().map(id ->
                        deliveryRepository.findById(id).orElseThrow(
                                () -> new ResourceNotFoundException("La commande avec l'id " + id + " n'existe pas")
                        )).toList(),
                dto.artMkrReference()
        );

        return makerReferenceMapper.toDto(makerReferenceRepository.save(makerReference));
    }

    @Override
    public MakerReferenceDTO modify(Long artId, Long mkrId, UpdateMakerReferenceDTO dto) throws ResourceNotFoundException {
       MakerReference makerReference = makerReferenceRepository.findById(
               new MakerReference.MakerReferenceId(artId,mkrId)
       ).orElseThrow(() -> new ResourceNotFoundException("Référence fabricant non existante"));

       makerReference.setArtMkrReference(dto.reference());

       return makerReferenceMapper.toDto(makerReferenceRepository.save(makerReference));
    }

    @Override
    public void delete(Long artId, Long mkrId) throws ResourceNotFoundException {
        makerReferenceRepository.delete(
                makerReferenceRepository.findById(
                        new MakerReference.MakerReferenceId(artId, mkrId)
                ).orElseThrow(() -> new ResourceNotFoundException("Référence fabricant non existante")));
    }
}
