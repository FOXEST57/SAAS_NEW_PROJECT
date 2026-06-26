package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.*;
import com.mns.cda.saas_facturation.Iservice.IArticleService;
import com.mns.cda.saas_facturation.Iservice.ISupplierService;
import com.mns.cda.saas_facturation.Iservice.ITvaService;
import com.mns.cda.saas_facturation.model.Article;
import com.mns.cda.saas_facturation.model.Supplier;
import com.mns.cda.saas_facturation.model.Tva;
import com.mns.cda.saas_facturation.repository.ArticleRepository;
import com.mns.cda.saas_facturation.repository.SupplierRepository;
import com.mns.cda.saas_facturation.repository.TvaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ArticleService implements IArticleService {

    protected final ArticleRepository articleRepository;
    protected final TvaRepository tvaRepository;
    protected final SupplierRepository supplierRepository;

    // Voir pagination
    // Pageable (page, size)
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
    public List<ArticleDTO> findBySupplier(Long id) throws ISupplierService.SupplierNotFoundException {

        Optional<Supplier> supplier = supplierRepository.findById(id);

        if (supplier.isEmpty()) {
            throw new ISupplierService.SupplierNotFoundException();
        }

        return articleRepository.findBySupplierSplId(id)
                .stream()
                .map(this::toDTO)
                .toList();
    }


    /**
     * Crée un nouvel article en base de données à partir des données fournies dans le DTO.
     *
     * <p>Les étapes réalisées sont les suivantes :
     * <ol>
     *   <li>Vérification de l'existence de la TVA associée</li>
     *   <li>Récupération optionnelle du fournisseur si un {@code supplierId} est fourni</li>
     *   <li>Construction et persistance de l'entité {@link Article}</li>
     *   <li>Mapping de l'entité sauvegardée vers un {@link ArticleDTO} via {@code toDTO()}</li>
     * </ol>
     *
     * @param dto les données de l'article à créer (référence, nom, description, prix, stock, TVA, fournisseur optionnel)
     * @return un {@link ArticleDTO} contenant les informations de l'article créé,
     *         incluant la TVA et le fournisseur associé (ou {@code null} si aucun fournisseur)
     * @throws ITvaService.TvaNotFoundException si la TVA fournie n'existe pas en base de données
     * @throws ISupplierService.SupplierNotFoundException si le fournisseur fourni n'existe pas en base de données
     */
    @Override
    public ArticleDTO create(ArticleRequestDTO dto) throws ITvaService.TvaNotFoundException,
            ISupplierService.SupplierNotFoundException {

        // Récupération de la TVA — obligatoire, lève une TvaNotFoundException si l'id est inconnu
        Tva articleTva = tvaRepository.findById(dto.tvaId())
                .orElseThrow(ITvaService.TvaNotFoundException::new);

        // Le fournisseur est optionnel (0,1) — on initialise à null par défaut
        Supplier supplier = null;

        // Si un supplierId est fourni, on vérifie qu'il existe bien en base
        // Lève une SupplierNotFoundException si l'id est inconnu, pour éviter une association invalide
        if (dto.supplierId() != null) {
            supplier = supplierRepository.findById(dto.supplierId())
                    .orElseThrow(ISupplierService.SupplierNotFoundException::new);
        }

        // Construction de l'entité Article à partir du DTO
        // L'id est null car il sera généré automatiquement par la base de données
        Article article = new Article(
                null,
                dto.artReference(),
                dto.artName(),
                dto.artDescription(),
                dto.artPriceExcludeTaxes(),
                dto.artStock(),
                articleTva,
                supplier
        );

        // Persistance en base puis mapping vers le DTO de réponse via toDTO()
        // L'entité retournée par save() contient l'id généré par la base
        return toDTO(articleRepository.save(article));
    }

    @Override
    public void delete(Long id) {
        articleRepository.deleteById(id);
    }

    @Override
    public ArticleDTO update(long id, ArticleRequestDTO dto) throws ArticleNotFoundException,
            ISupplierService.SupplierNotFoundException,
            ITvaService.TvaNotFoundException{

        Article article = articleRepository.findById(id)
                .orElseThrow(ArticleNotFoundException::new);

        // Pas de vérification si référence exite déjà en base
        // Mise à jour des champs
        article.setArtReference(dto.artReference());
        article.setArtName(dto.artName());
        article.setArtDescription(dto.artDescription());
        article.setArtPriceExcludeTaxes(dto.artPriceExcludeTaxes());
        article.setArtStock(dto.artStock());

        // Mise à jour de la TVA si elle change
        Tva tva = tvaRepository.findById(dto.tvaId()).orElseThrow(ITvaService.TvaNotFoundException::new);
        article.setTva(tva);

        // Le fournisseur est optionnel (0,1) — on initialise à null par défaut
        Supplier supplier = null;

        // Si un supplierId est fourni, on vérifie qu'il existe bien en base
        // Lève une SupplierNotFoundException si l'id est inconnu, pour éviter une association invalide
        if (dto.supplierId() != null) {
            supplier = supplierRepository.findById(dto.supplierId())
                    .orElseThrow(ISupplierService.SupplierNotFoundException::new);
        }

        article.setSupplier(supplier);

        Article saved = articleRepository.save(article);
        return toDTO(saved);
    }


    /**
     * Méthode qui permet de transformer un article en articleDTO pour ajouter le prix TTC dans l'objet.
     * @param article
     * @return ArticleDTO
     */
    private ArticleDTO toDTO(Article article) {
        BigDecimal priceTTC = article.getArtPriceExcludeTaxes().multiply(BigDecimal.ONE.add(article.getTva().getTvaTaux()));

        // Mapping du fournisseur vers son DTO de réponse
        // Si aucun fournisseur n'est associé, on retourne null pour ce champ
        SupplierResponseDTO supplierResponse = article.getSupplier() != null
                ? new SupplierResponseDTO(
                article.getSupplier().getSplId(),
                article.getSupplier().getSplName())
                : null;

        return new ArticleDTO(
                article.getArtId(),
                article.getArtReference(),
                article.getArtName(),
                article.getArtDescription(),
                article.getArtPriceExcludeTaxes(),
                article.getArtStock(),
                article.getTva(),
                priceTTC,
                supplierResponse
        );
    }
}
