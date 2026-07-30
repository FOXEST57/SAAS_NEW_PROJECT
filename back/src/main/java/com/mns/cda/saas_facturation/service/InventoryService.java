package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.InventoryDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.InventoryRequestDTO;
import com.mns.cda.saas_facturation.Iservice.IInventoryService;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.mapper.InventoryMapper;
import com.mns.cda.saas_facturation.model.Article;
import com.mns.cda.saas_facturation.model.Inventory;
import com.mns.cda.saas_facturation.model.Country;
import com.mns.cda.saas_facturation.repository.ArticleRepository;
import com.mns.cda.saas_facturation.repository.InventoryRepository;
import com.mns.cda.saas_facturation.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class InventoryService implements IInventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;
    private final ArticleRepository articleRepository;

    @Override
    public List<InventoryDTO> findAll() {
        return inventoryRepository.findAll()
                .stream()
                .map(inventoryMapper::toDTO)
                .toList();
    }

    @Override
    public Optional<InventoryDTO> findById(Long invId) {
        return inventoryRepository.findById(invId).map(inventoryMapper::toDTO);
    }

    @Override
    public List<InventoryDTO> findByArticleId(Long articleId) {
        return inventoryRepository.findInventoriesByArticle_ArtId(articleId)
                .stream()
                .map(inventoryMapper::toDTO)
                .toList();
    }

    @Override
    public InventoryDTO create(InventoryRequestDTO dto) throws ResourceNotFoundException {
        Article article = articleRepository.findById(dto.articleId()).orElseThrow(() -> new ResourceNotFoundException("Article non existant"));

        Inventory inventory = new Inventory();
        inventory.setInvStock(dto.invStock());
        inventory.setArticle(article);

        return inventoryMapper.toDTO(inventoryRepository.save(inventory));
    }

    @Override
    public InventoryDTO update(Long invId, InventoryRequestDTO dto) throws ResourceNotFoundException {
        Inventory inventory = inventoryRepository.findById(invId).orElseThrow(() -> new ResourceNotFoundException("Inventaire non existante"));
        Article article = articleRepository.findById(dto.articleId()).orElseThrow(() -> new ResourceNotFoundException("Article non existant"));

        inventory.setInvStock(dto.invStock());
        inventory.setArticle(article);

        return inventoryMapper.toDTO(inventoryRepository.save(inventory));
    }

    @Override
    public void delete(Long invId) throws ResourceNotFoundException {
        Inventory inventory = inventoryRepository.findById(invId).orElseThrow(() -> new ResourceNotFoundException("Inventaire non existante"));
        
        inventoryRepository.delete(inventory);
    }

}
