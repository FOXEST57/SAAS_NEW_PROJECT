package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.ArticleCreateDTO;
import com.mns.cda.saas_facturation.DTO.ArticleDTO;
import com.mns.cda.saas_facturation.Iservice.IArticleService;
import com.mns.cda.saas_facturation.model.Article;
import com.mns.cda.saas_facturation.model.Tva;
import com.mns.cda.saas_facturation.repository.ArticleRepository;
import com.mns.cda.saas_facturation.repository.TvaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ArticleService implements IArticleService {

    protected final ArticleRepository articleRepository;
    protected final TvaRepository tvaRepository;

    @Override
    public List<ArticleDTO> findAll() {
        return articleRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public Optional<ArticleDTO> findById(Long id) {
        return articleRepository.findById(id)
                .map(this::toDTO);

    }

    @Override
    public Article create(ArticleCreateDTO dto) {
        Tva articleTva = tvaRepository.findById(dto.tvaId())
                .orElseThrow(() -> new IllegalArgumentException("TVA not found")); //On vérifie si la TVA donner dans l'objet existe bien en base de données

        Article article = new Article(
                null,
                dto.artReference(),
                dto.artName(),
                dto.artDescription(),
                dto.artPriceExcludeTaxes(),
                dto.artStock(),
                articleTva
        );
        return articleRepository.save(article);
    }

    @Override
    public void delete(Long id) {
        articleRepository.deleteById(id);
    }

    @Override
    public ArticleDTO update(long id, ArticleCreateDTO dto) throws ArticleNotFoundException {
        Article article = articleRepository.findById(id)
                .orElseThrow(ArticleNotFoundException::new);

        // Mise à jour des champs
        article.setArtReference(dto.artReference());
        article.setArtName(dto.artName());
        article.setArtDescription(dto.artDescription());
        article.setArtPriceExcludeTaxes(dto.artPriceExcludeTaxes());
        article.setArtStock(dto.artStock());

        // Mise à jour de la TVA si elle change
        Tva tva = tvaRepository.findById(dto.tvaId()).orElseThrow(IllegalArgumentException::new);
        article.setTva(tva);

        Article saved = articleRepository.save(article);
        return toDTO(saved);
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
