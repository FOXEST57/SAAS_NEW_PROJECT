package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.SupplierReferenceDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.SupplierReferenceRequestDTO;
import com.mns.cda.saas_facturation.Iservice.IArticleService;
import com.mns.cda.saas_facturation.Iservice.ISupplierReferenceService;
import com.mns.cda.saas_facturation.Iservice.ISupplierService;
import com.mns.cda.saas_facturation.mapper.SupplierReferenceMapper;
import com.mns.cda.saas_facturation.mapper.responseMapper.SupplierReferenceResponseMapper;
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
    private final SupplierReferenceResponseMapper supplierReferenceResponseMapper;


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
    public List<SupplierReferenceDTO> findByArticleId(Long articleId) {
        return supplierReferenceRepository.findBySplRefId_ArticleId(articleId)
                .stream()
                .map(supplierReferenceMapper::toDTO)
                .toList();
    }

    //Get By ID Supplier
    @Override
    public List<SupplierReferenceDTO> findBySupplierId(Long supplierId) {
        return supplierReferenceRepository.findBySplRefId_SupplierId(supplierId)
                .stream()
                .map(supplierReferenceMapper::toDTO)
                .toList();
    }


    //Post
    @Override
    public SupplierReferenceDTO create(SupplierReferenceRequestDTO dto)
            throws IArticleService.ArticleNotFoundException,
            ISupplierService.SupplierNotFoundException {

        Supplier artSplSupplier = supplierRepository.findById(dto.supplierId())
                .orElseThrow(ISupplierService.SupplierNotFoundException::new);

        Article artSplArticle = articleRepository.findById(dto.articleId())
                .orElseThrow(IArticleService.ArticleNotFoundException::new);

        SupplierReference.SupplierReferenceId artSplId= new SupplierReference.SupplierReferenceId(dto.articleId(), dto.supplierId());

        SupplierReference artSpl = new SupplierReference(
                artSplId,
                artSplArticle,
                artSplSupplier,
                dto.splRefReference(),
                dto.splRefStock()
        );

        return supplierReferenceMapper.toDTO(supplierReferenceRepository.save(artSpl));
    }



    //PUT
    @Override
    public SupplierReferenceDTO update(SupplierReference.SupplierReferenceId id, SupplierReferenceRequestDTO dto)
            throws SupplierReferenceNotFoundException,
            ISupplierService.SupplierNotFoundException,
            IArticleService.ArticleNotFoundException {

        SupplierReference artSpl = supplierReferenceRepository.findById(id)
                .orElseThrow(SupplierReferenceNotFoundException::new);

        Supplier supplier = supplierRepository.findById(dto.supplierId())
                .orElseThrow(ISupplierService.SupplierNotFoundException::new);
        artSpl.setSupplier(supplier);

        Article article = articleRepository.findById(dto.articleId())
                .orElseThrow(IArticleService.ArticleNotFoundException::new);
        artSpl.setArticle(article);


        SupplierReference.SupplierReferenceId artSplId = new SupplierReference.SupplierReferenceId(dto.articleId(), dto.supplierId());
        artSpl.setSplRefId(artSplId);
        artSpl.setSplRefReference(dto.splRefReference());
        artSpl.setSplRefStock(dto.splRefStock());

        SupplierReference saved = supplierReferenceRepository.save(artSpl);
        return supplierReferenceMapper.toDTO(saved);
    }

    @Override
    public void deleteById(SupplierReference.SupplierReferenceId id) {
        supplierReferenceRepository.deleteById(id);
    }

}
