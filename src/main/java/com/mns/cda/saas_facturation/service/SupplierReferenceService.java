package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.SupplierDTO;
import com.mns.cda.saas_facturation.DTO.SupplierReferenceDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.SupplierReferenceRequestDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.ArticleResponseSupplierDTO;
import com.mns.cda.saas_facturation.DTO.updateDTO.UpdateSupplierReferenceDTO;
import com.mns.cda.saas_facturation.Iservice.IArticleService;
import com.mns.cda.saas_facturation.Iservice.ISupplierReferenceService;
import com.mns.cda.saas_facturation.Iservice.ISupplierService;
import com.mns.cda.saas_facturation.mapper.ArticleMapper;
import com.mns.cda.saas_facturation.mapper.SupplierMapper;
import com.mns.cda.saas_facturation.mapper.SupplierReferenceMapper;
import com.mns.cda.saas_facturation.model.Article;
import com.mns.cda.saas_facturation.model.SupplierReference;
import com.mns.cda.saas_facturation.model.Supplier;
import com.mns.cda.saas_facturation.repository.ArticleRepository;
import com.mns.cda.saas_facturation.repository.SupplierReferenceRepository;
import com.mns.cda.saas_facturation.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class SupplierReferenceService implements ISupplierReferenceService {

    protected final SupplierReferenceRepository supplierReferenceRepository;
    protected final SupplierRepository supplierRepository;
    protected final ArticleRepository articleRepository;

    private final SupplierReferenceMapper supplierReferenceMapper;
    private final SupplierMapper supplierMapper;
    private final ArticleMapper articleMapper;


    //GetAll
    @Override
    public List<SupplierReferenceDTO> findAll() {
        return supplierReferenceRepository.findAll()
                .stream()
                .map(supplierReferenceMapper::toDTO)
                .toList(); }

    //Get By Id composite
    @Override
    public Optional<SupplierReferenceDTO> findById(SupplierReference.SupplierReferenceId id) {
        return supplierReferenceRepository.findById(id)
                .map(supplierReferenceMapper::toDTO);
    }

    //Get By Id Article
    @Override
    public List<SupplierDTO> findByArticleId(Long articleId) {
        return supplierReferenceRepository.findBySplRefId_ArticleId(articleId)
                .stream()
                .map(supplierMapper::ReferenceToDTO)
                .toList();
    }

    //Get By ID Supplier
    @Override
    public List<ArticleResponseSupplierDTO> findBySupplierId(Long supplierId) {
        return supplierReferenceRepository.findBySplRefId_SupplierId(supplierId)
                .stream()
                .map(articleMapper::supplierReferenceToDTO)
                .toList();
    }


    //Post
    @Override
    public SupplierReferenceDTO create(SupplierReferenceRequestDTO dto)
            throws IArticleService.ArticleNotFoundException,
            ISupplierService.SupplierNotFoundException {

        Supplier splRefSupplier = supplierRepository.findById(dto.supplierId())
                .orElseThrow(ISupplierService.SupplierNotFoundException::new);

        Article splRefArticle = articleRepository.findById(dto.articleId())
                .orElseThrow(IArticleService.ArticleNotFoundException::new);

        SupplierReference splRef = new SupplierReference(
                new SupplierReference.SupplierReferenceId(),
                splRefArticle,
                splRefSupplier,
                dto.splRefReference(),
                dto.splRefStock()
        );

        return supplierReferenceMapper.toDTO(supplierReferenceRepository.save(splRef));
    }



    //PUT
    @Override
    public SupplierReferenceDTO update(Long artId, Long mkrId, UpdateSupplierReferenceDTO dto)
            throws SupplierReferenceNotFoundException{

        SupplierReference splRef = supplierReferenceRepository.findById(
                new SupplierReference.SupplierReferenceId(artId,mkrId)
        ).orElseThrow(ISupplierReferenceService.SupplierReferenceNotFoundException::new);

        splRef.setSplRefReference(dto.splRefReference());
        splRef.setSplRefStock(dto.splRefStock());

        SupplierReference saved = supplierReferenceRepository.save(splRef);
        return supplierReferenceMapper.toDTO(saved);
    }

    @Override
    public void deleteById(SupplierReference.SupplierReferenceId id) {
        supplierReferenceRepository.deleteById(id);
    }

}
