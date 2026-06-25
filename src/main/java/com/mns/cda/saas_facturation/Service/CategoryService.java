package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.CategoryRequestDTO;
import com.mns.cda.saas_facturation.Iservice.ICategoryService;
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
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Category create(CategoryRequestDTO dto) {
        Category category = new Category(
                null,
                dto.catName());
        return categoryRepository.save(category);
    }

    @Override
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public Category update(long id, String catName) throws CategoryNotFoundException {
        Category category = categoryRepository.findById(id)
                .orElseThrow(CategoryNotFoundException::new);

        category.setCatName(catName);
        return categoryRepository.save(category);
    }
}
