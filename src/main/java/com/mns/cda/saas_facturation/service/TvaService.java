package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.requestDTO.TvaRequestDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.TvaResponseDTO;
import com.mns.cda.saas_facturation.Iservice.ITvaService;
import com.mns.cda.saas_facturation.model.Tva;
import com.mns.cda.saas_facturation.repository.TvaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service métier gérant la logique liée aux taux de TVA ({@link Tva}).
 *
 * <p>Implémente {@link ITvaService} et délègue toutes les opérations
 * de persistance à {@link TvaRepository}.</p>
 *
 * <p>Contrairement à {@link ArticleService} et {@link SupplierService}, ce service
 * retourne directement les entités {@link Tva} sans couche DTO de sortie dédiée,
 * car l'entité ne contient pas de relations JPA susceptibles de provoquer
 * des boucles infinies JSON.</p>
 *
 * <p>Ce service propose deux types de mise à jour :</p>
 * <ul>
 *   <li>{@link #update(long, TvaRequestDTO)} — remplacement complet (HTTP PUT) :
 *       nom et taux sont tous les deux mis à jour</li>
 *   <li>{@link #patchTaux(long, BigDecimal)} — mise à jour partielle (HTTP PATCH) :
 *       seul le taux est modifié, le nom reste inchangé</li>
 * </ul>
 *
 * <p>L'injection de dépendances se fait via le constructeur,
 * généré automatiquement par {@code @RequiredArgsConstructor} de Lombok.</p>
 *
 * @see ITvaService
 * @see TvaRepository
 * @see Tva
 */
@Service
@RequiredArgsConstructor
public class TvaService implements ITvaService {

    protected final TvaRepository tvaRepository;

    /**
     * Récupère la liste complète de tous les taux de TVA en base de données.
     *
     * <p>Aucune conversion DTO n'est nécessaire : l'entité {@link Tva} est retournée
     * directement car elle ne contient pas de relations JPA problématiques.</p>
     *
     * @return une {@link List} de {@link Tva} (vide si aucune TVA n'existe)
     */
    @Override
    public List<Tva> findAll() {
        return tvaRepository.findAll();
    }

    /**
     * Recherche un taux de TVA par son identifiant unique.
     *
     * <p>Retourne un {@link Optional} vide si aucune TVA ne correspond à l'id fourni,
     * sans lever d'exception — la vérification est laissée à la charge du contrôleur.</p>
     *
     * @param id l'identifiant unique de la TVA à rechercher
     * @return un {@link Optional} contenant la {@link Tva} si trouvée, vide sinon
     */
    @Override
    public Optional<Tva> findById(Long id) {
        return tvaRepository.findById(id);
    }

    /**
     * Crée un nouveau taux de TVA en base de données à partir des données fournies dans le DTO.
     *
     * <p>L'identifiant est forcé à {@code null} lors de la construction de l'entité
     * pour garantir que c'est la base de données qui génère l'id via
     * la stratégie {@code @GeneratedValue}.</p>
     *
     * @param tva le DTO contenant le libellé et le taux de la TVA à créer
     * @return la {@link Tva} créée avec son id généré
     */
    @Override
    public Tva create(TvaRequestDTO tva) {

        // Construction de l'entité — l'id est null pour forcer la génération en base
        Tva tvaEntity = new Tva(
                null,
                tva.tvaName(),
                tva.tvaTaux());

        return tvaRepository.save(tvaEntity);
    }

    /**
     * Supprime un taux de TVA par son identifiant unique.
     *
     * <p>L'existence de la TVA est vérifiée en amont dans le contrôleur
     * avant d'appeler cette méthode.</p>
     *
     * @param id l'identifiant unique de la TVA à supprimer
     */
    @Override
    public void delete(Long id) {
        tvaRepository.deleteById(id);
    }

    /**
     * Met à jour uniquement le taux d'une TVA existante (mise à jour partielle).
     *
     * <p>Seul le champ {@code tvaTaux} est modifié — le libellé {@code tvaName}
     * reste inchangé. Cette méthode correspond à la sémantique HTTP PATCH.</p>
     *
     * @param id      l'identifiant unique de la TVA à modifier
     * @param tvaTaux la nouvelle valeur du taux à appliquer
     * @return la {@link Tva} après mise à jour du taux
     * @throws TvaNotFoundException si aucune TVA ne correspond à l'id fourni
     */
    @Override
    public Tva patchTaux(long id, BigDecimal tvaTaux) throws TvaNotFoundException {

        // Récupération de l'entité existante — lève TvaNotFoundException si absente
        Tva tvaEntity = tvaRepository.findById(id)
                .orElseThrow(TvaNotFoundException::new);

        // Seul le taux est mis à jour — le nom reste inchangé
        tvaEntity.setTvaTaux(tvaTaux);
        tvaRepository.save(tvaEntity);

        return tvaEntity;
    }

    /**
     * Met à jour intégralement un taux de TVA existant (remplacement complet).
     *
     * <p>Les deux champs de l'entité sont remplacés par les valeurs fournies dans le DTO
     * (sémantique HTTP PUT) : le libellé {@code tvaName} et le taux {@code tvaTaux}
     * sont tous les deux écrasés.</p>
     *
     * @param id  l'identifiant unique de la TVA à modifier
     * @param dto le DTO contenant le nouveau libellé et le nouveau taux
     * @return la {@link Tva} après mise à jour complète
     * @throws TvaNotFoundException si aucune TVA ne correspond à l'id fourni
     */
    @Override
    public Tva update(long id, TvaRequestDTO dto) throws TvaNotFoundException {

        // Récupération de l'entité existante — lève TvaNotFoundException si absente
        Tva tvaEntity = tvaRepository.findById(id)
                .orElseThrow(TvaNotFoundException::new);

        // Mise à jour des deux champs de l'entité
        tvaEntity.setTvaName(dto.tvaName());
        tvaEntity.setTvaTaux(dto.tvaTaux());

        return tvaRepository.save(tvaEntity);
    }

    @Override
    public TvaResponseDTO toResponseDto(Tva tva) {
        return new TvaResponseDTO(
                tva.getTvaId(),
                tva.getTvaName(),
                tva.getTvaTaux()
        );
    }
}