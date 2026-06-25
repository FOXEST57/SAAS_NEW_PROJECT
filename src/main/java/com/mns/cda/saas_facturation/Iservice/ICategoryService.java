package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.CategoryRequestDTO;
import com.mns.cda.saas_facturation.model.Category;

import java.util.List;
import java.util.Optional;

public interface ICategoryService {

    public static class CategoryNotFoundException extends Exception {}

    List<Category> findAll();

    Optional<Category> findById(Long id);

    Category create(CategoryRequestDTO dto);

    void delete(Long id);

    Category update(long id, String catName) throws CategoryNotFoundException ;
}
