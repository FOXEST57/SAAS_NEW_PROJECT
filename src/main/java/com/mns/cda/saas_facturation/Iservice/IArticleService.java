package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.ArticleRequestDTO;
import com.mns.cda.saas_facturation.DTO.ArticleDTO;
import com.mns.cda.saas_facturation.model.Article;

import java.util.List;
import java.util.Optional;

public interface IArticleService {

    public static class ArticleNotFoundException extends Exception {}

    //GetAll
    List<ArticleDTO> findAll();

    //GetByID
    Optional<ArticleDTO> findById(Long id);

    //Post
    Article create(ArticleRequestDTO dto);

    //Delete
    void delete(Long id);

    //Put
    public ArticleDTO update(long id, ArticleRequestDTO dto) throws ArticleNotFoundException ;

}