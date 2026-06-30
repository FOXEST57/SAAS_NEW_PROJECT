package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.UpdateMakerReferenceDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.MakerReferenceRequestDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.ArticleResponseMakerReferenceDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.MakerReferenceResponseDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.MakerResponseDTO;
import com.mns.cda.saas_facturation.Iservice.IArticleService;
import com.mns.cda.saas_facturation.Iservice.IMakerReferenceService;
import com.mns.cda.saas_facturation.Iservice.IMakerService;
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

    @Override
    public List<MakerReferenceResponseDTO> findAll() {
        return makerReferenceRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    public List<MakerReferenceResponseDTO> findAllByArticle(Long artId)  {
        return makerReferenceRepository.findByArtMkrId_ArticleId(artId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    public List<MakerReferenceResponseDTO> findAllByMaker(Long mkrId)  {
        return makerReferenceRepository.findByArtMkrId_MakerId(mkrId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    public MakerReferenceResponseDTO findById(Long artId, Long mkrId) throws IMakerReferenceService.MakerReferenceNotFoundException {

        return toResponseDto(makerReferenceRepository.findById(
                new MakerReference.MakerReferenceId(artId,mkrId)
        ).orElseThrow(IMakerReferenceService.MakerReferenceNotFoundException::new));
    }

    @Override
    public MakerReferenceResponseDTO create(MakerReferenceRequestDTO dto) throws IArticleService.ArticleNotFoundException,
            IMakerService.MakerNotFoundException {

        Article article = articleRepository.findById(dto.artId())
                .orElseThrow(IArticleService.ArticleNotFoundException::new);

        Maker maker = makerRepository.findById(dto.mkrId())
                .orElseThrow(IMakerService.MakerNotFoundException::new);

        MakerReference makerReference = new MakerReference(
                null,
                article,
                maker,
                dto.mkrRefReference(),
                dto.mkrRefStock()
        );

        return toResponseDto(makerReferenceRepository.save(makerReference));
    }

    @Override
    public MakerReferenceResponseDTO modify(Long artId, Long mkrId, UpdateMakerReferenceDTO dto) throws MakerReferenceNotFoundException {

       MakerReference makerReference = makerReferenceRepository.findById(
               new MakerReference.MakerReferenceId(artId,mkrId)
       ).orElseThrow(MakerReferenceNotFoundException::new);

       makerReference.setMkrRefReference(dto.mkrRefReference());
       makerReference.setMkrRefStock(dto.mkrRefStock());

       return toResponseDto(makerReferenceRepository.save(makerReference));

    }

    @Override
    public void delete(Long artId, Long mkrId) throws MakerReferenceNotFoundException {
        makerReferenceRepository.delete(
                makerReferenceRepository.findById(
                        new MakerReference.MakerReferenceId(artId, mkrId)
                ).orElseThrow(MakerReferenceNotFoundException::new));
    }

    @Override
    public MakerReferenceResponseDTO toResponseDto(MakerReference makerReference) {

        ArticleResponseMakerReferenceDTO articleResponseMakerReferenceDTO = new ArticleResponseMakerReferenceDTO(
                makerReference.getArticle().getArtId(),
                makerReference.getArticle().getArtName()
        );

        MakerResponseDTO makerResponseDTO = new MakerResponseDTO(
                makerReference.getMaker().getMkrId(),
                makerReference.getMaker().getMkrName()
        );

        return new MakerReferenceResponseDTO(
                articleResponseMakerReferenceDTO,
                makerResponseDTO,
                makerReference.getMkrRefReference(),
                makerReference.getMkrRefStock()
        );
    }
}
