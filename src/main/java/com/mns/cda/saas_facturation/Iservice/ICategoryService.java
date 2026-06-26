package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.CategoryDTO;
import com.mns.cda.saas_facturation.DTO.CategoryRequestDTO;
import com.mns.cda.saas_facturation.model.Category;

import java.util.List;
import java.util.Optional;

public interface ICategoryService {

    public static class CategoryNotFoundException extends Exception {}

    List<CategoryDTO> findAll();

    Optional<CategoryDTO> findById(Long id);

    CategoryDTO create(CategoryRequestDTO dto) throws CategoryNotFoundException;

    void delete(Long id);

    CategoryDTO update(long id, CategoryRequestDTO categoryToUpdate) throws CategoryNotFoundException ;
}
