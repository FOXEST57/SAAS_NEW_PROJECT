package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.CategoryDTO;
import com.mns.cda.saas_facturation.DTO.CategoryRequestDTO;
import com.mns.cda.saas_facturation.Iservice.IArticleService;
import com.mns.cda.saas_facturation.Iservice.ICategoryService;
import com.mns.cda.saas_facturation.model.Article;
import com.mns.cda.saas_facturation.model.Category;
import com.mns.cda.saas_facturation.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    protected final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDTO> findAll() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public Optional<CategoryDTO> findById(Long id) {
        return categoryRepository.findById(id)
                .map(this::toDTO);
    }

    @Override
    public CategoryDTO create(CategoryRequestDTO dto) throws CategoryNotFoundException {

        Category categoryParent = categoryRepository.findById(dto.parentId())
                .orElseThrow(ICategoryService.CategoryNotFoundException::new);

        Category category = new Category();
        category.setCatName(dto.catName());
        category.setCatParent(categoryParent);

        return toDTO(categoryRepository.save(category));
    }

    @Override
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public CategoryDTO update(long id, CategoryRequestDTO categoryToUpdate)
            throws CategoryNotFoundException {

        Category category = categoryRepository.findById(id)
                .orElseThrow(CategoryNotFoundException::new);
        Category categoryParent = categoryRepository.findById(categoryToUpdate.parentId())
                .orElseThrow(CategoryNotFoundException::new);

        category.setCatName(categoryToUpdate.catName());
        category.setCatParent(categoryParent);

        return toDTO(categoryRepository.save(category));
    }

    /**
     * Méthode qui permet de transformer une Category en CategoryDTO pour éviter les Json infini
     * @param category
     * @return
     */
    private CategoryDTO toDTO(Category category) {
        return new CategoryDTO(
                category.getCatId(),
                category.getCatName(),
                category.getCatParent() != null ? category.getCatParent().getCatName() : null,
                category.getCatChildren() != null
                        ? category.getCatChildren().stream()
                        .map(this::toDTO)
                        .toList()
                        : List.of()
        );
    }
}
