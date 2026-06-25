package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.ArticleRequestDTO;
import com.mns.cda.saas_facturation.DTO.ArticleDTO;

import java.util.List;
import java.util.Optional;

public interface IArticleService {

    public static class ArticleNotFoundException extends Exception {}

    //GetAll
    List<ArticleDTO> findAll();

    //GetByID
    Optional<ArticleDTO> findById(Long id);

    //GetArticlesBySplId
    List<ArticleDTO> findBySupplier(Long id) throws ISupplierService.SupplierNotFoundException;

    //Post
    ArticleDTO create(ArticleRequestDTO dto) throws ITvaService.TvaNotFoundException, ISupplierService.SupplierNotFoundException;

    //Delete
    void delete(Long id);

    //Put
    public ArticleDTO update(long id, ArticleRequestDTO dto) throws ArticleNotFoundException,ITvaService.TvaNotFoundException, ISupplierService.SupplierNotFoundException ;

}