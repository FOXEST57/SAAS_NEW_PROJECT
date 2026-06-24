package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.ArticleDTO;
import com.mns.cda.saas_facturation.model.Article;
import com.mns.cda.saas_facturation.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {

    protected final ArticleRepository articleRepository;

    public List<ArticleDTO> findAll() {
        return articleRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Méthode qui permet de transformer un article en articleDTO pour ajouter le prix TTC dans l'objet.
     * @param article
     * @return ArticleDTO
     */

    private ArticleDTO toDTO(Article article) {
        Double priceTTC = article.getArtPriceExcludeTaxes()* (1+article.getTva().getTvaTaux());

        return new ArticleDTO(
                article.getArtId(),
                article.getArtReference(),
                article.getArtName(),
                article.getArtDescription(),
                article.getArtPriceExcludeTaxes(),
                article.getArtStock(),
                article.getTva(),
                priceTTC
        );
    }
}
