package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.MakerReferenceDTO;
import com.mns.cda.saas_facturation.DTO.UpdateMakerReferenceDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.MakerReferenceRequestDTO;
import com.mns.cda.saas_facturation.Iservice.IArticleService;
import com.mns.cda.saas_facturation.Iservice.IMakerReferenceService;
import com.mns.cda.saas_facturation.Iservice.IMakerService;
import com.mns.cda.saas_facturation.mapper.MakerReferenceMapper;
import com.mns.cda.saas_facturation.model.Article;
import com.mns.cda.saas_facturation.model.Maker;
import com.mns.cda.saas_facturation.model.MakerReference;
import com.mns.cda.saas_facturation.repository.ArticleRepository;
import com.mns.cda.saas_facturation.repository.MakerReferenceRepository;
import com.mns.cda.saas_facturation.repository.MakerRepository;
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

    @Override
    public List<MakerReferenceDTO> findAll() {
        return makerReferenceRepository.findAll()
                .stream()
                .map(makerReferenceMapper::toDto)
                .toList();
    }

    @Override
    public List<MakerReferenceDTO> findAllByArticle(Long artId)  {
        return makerReferenceRepository.findByMkrRefId_ArticleId(artId)
                .stream()
                .map(makerReferenceMapper::toDto)
                .toList();
    }

    @Override
    public List<MakerReferenceDTO> findAllByMaker(Long mkrId)  {
        return makerReferenceRepository.findByMkrRefId_MakerId(mkrId)
                .stream()
                .map(makerReferenceMapper::toDto)
                .toList();
    }

    @Override // Utiliser la clé primaire
    public MakerReferenceDTO findById(Long artId, Long mkrId) throws IMakerReferenceService.MakerReferenceNotFoundException {

        return makerReferenceMapper.toDto(makerReferenceRepository.findById(
                new MakerReference.MakerReferenceId(artId,mkrId)
        ).orElseThrow(IMakerReferenceService.MakerReferenceNotFoundException::new));
    }

    @Override
    public MakerReferenceDTO create(MakerReferenceRequestDTO dto) throws IArticleService.ArticleNotFoundException,
            IMakerService.MakerNotFoundException {
        //

        Article article = articleRepository.findById(dto.artId())
                .orElseThrow(IArticleService.ArticleNotFoundException::new);

        Maker maker = makerRepository.findById(dto.mkrId())
                .orElseThrow(IMakerService.MakerNotFoundException::new);

        MakerReference makerReference = new MakerReference(
                new MakerReference.MakerReferenceId(),
                article,
                maker,
                dto.mkrRefReference()
        );

        return makerReferenceMapper.toDto(makerReferenceRepository.save(makerReference));
    }

    @Override
    public MakerReferenceDTO modify(Long artId, Long mkrId, UpdateMakerReferenceDTO dto) throws MakerReferenceNotFoundException {

       MakerReference makerReference = makerReferenceRepository.findById(
               new MakerReference.MakerReferenceId(artId,mkrId)
       ).orElseThrow(MakerReferenceNotFoundException::new);

       makerReference.setMkrRefReference(dto.mkrRefReference());

       return makerReferenceMapper.toDto(makerReferenceRepository.save(makerReference));

    }

    @Override
    public void delete(Long artId, Long mkrId) throws MakerReferenceNotFoundException {
        makerReferenceRepository.delete(
                makerReferenceRepository.findById(
                        new MakerReference.MakerReferenceId(artId, mkrId)
                ).orElseThrow(MakerReferenceNotFoundException::new));
    }
}
