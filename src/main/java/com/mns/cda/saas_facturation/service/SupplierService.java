package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.SupplierDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.SupplierRequestDTO;
import com.mns.cda.saas_facturation.Iservice.IAddressService;
import com.mns.cda.saas_facturation.Iservice.ISupplierService;
import com.mns.cda.saas_facturation.mapper.SupplierMapper;
import com.mns.cda.saas_facturation.model.Address;
import com.mns.cda.saas_facturation.model.Article;
import com.mns.cda.saas_facturation.model.Supplier;
import com.mns.cda.saas_facturation.repository.AddressRepository;
import com.mns.cda.saas_facturation.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service métier gérant la logique liée aux fournisseurs ({@link Supplier}).
 *
 * <p>Implémente {@link ISupplierService} et délègue toutes les opérations
 * de persistance à {@link SupplierRepository}.</p>
 *
 * <p>Ce service est responsable de :</p>
 * <ul>
 *   <li>convertir les entités JPA en DTOs avant de les retourner au contrôleur</li>
 *   <li>construire les entités {@link Supplier} à partir des DTOs reçus en entrée</li>
 *   <li>lever des exceptions métier explicites ({@link SupplierNotFoundException})
 *       lorsqu'une ressource demandée est introuvable</li>
 * </ul>
 *
 * <p>L'injection de dépendance du repository se fait via le constructeur,
 * généré automatiquement par l'annotation {@code @RequiredArgsConstructor} de Lombok.</p>
 *
 * @see ISupplierService
 * @see SupplierRepository
 * @see SupplierDTO
 */
@Service
@RequiredArgsConstructor
public class SupplierService implements ISupplierService {

    private final SupplierRepository supplierRepository;

    private final AddressRepository addressRepository;

    private final SupplierMapper supplierMapper;

    /**
     * Récupère la liste de tous les fournisseurs en base de données.
     *
     * <p>Chaque entité {@link Supplier} est convertie en {@link SupplierDTO}
     * via  avant d'être retournée au contrôleur.</p>
     *
     * @return une {@link List} de {@link SupplierDTO} (vide si aucun fournisseur n'existe)
     */
    @Override
    public List<SupplierDTO> findAll() {
        return supplierRepository.findAll()
                .stream()
                .map(supplierMapper::toDTO)  // Conversion entité → DTO pour chaque élément du flux
                .toList();
    }

    /**
     * Recherche un fournisseur par son identifiant unique.
     *
     * <p>Si aucun fournisseur ne correspond à l'splId fourni, une {@link SupplierNotFoundException}
     * est levée. Elle sera interceptée soit localement dans le contrôleur,
     * soit par le {@code GlobalExceptionInterceptor}.</p>
     *
     * @param id l'identifiant du fournisseur à rechercher
     * @return le {@link SupplierDTO} correspondant au fournisseur trouvé
     * @throws SupplierNotFoundException si aucun fournisseur ne correspond à cet splId
     */
    @Override
    public SupplierDTO findById(Long id) throws SupplierNotFoundException {
        return supplierMapper.toDTO(supplierRepository.findById(id)
                .orElseThrow(SupplierNotFoundException::new)); // Lève l'exception si l'Optional est vide
    }

    /**
     * Crée un nouveau fournisseur en base de données à partir d'un DTO de requête.
     *
     * <p>L'identifiant est forcé à {@code null} lors de la construction de l'entité
     * pour garantir que c'est la base de données qui génère l'splId via
     * la stratégie {@code @GeneratedValue}, et non le client.</p>
     *
     * @param dto le DTO contenant les données du fournisseur à créer
     * @return le {@link SupplierDTO} du fournisseur créé, avec son splId généré
     */
    @Override
    public SupplierDTO create(SupplierRequestDTO dto) throws IAddressService.AddressNotFoundException {

        Address address = addressRepository.findById(dto.addressId())
                .orElseThrow(IAddressService.AddressNotFoundException::new);

        // Construction de l'entité à partir du DTO — l'splId est null pour forcer la génération en base
        Supplier supplier = new Supplier(
                null,
                dto.name(),
                dto.email(),
                dto.phoneNumber(),
                null,
                address
        );

        return supplierMapper.toDTO(supplierRepository.save(supplier));
    }

    /**
     * Supprime un fournisseur par son identifiant.
     *
     * <p>L'existence du fournisseur est vérifiée avant toute tentative de suppression.
     * Si l'splId est introuvable, une {@link SupplierNotFoundException} est levée
     * pour éviter un appel inutile à {@code deleteById}.</p>
     *
     * @param id l'identifiant du fournisseur à supprimer
     * @throws SupplierNotFoundException si aucun fournisseur ne correspond à cet splId
     */
    @Override
    public void delete(Long id) throws SupplierNotFoundException {
        Optional<Supplier> optionalSupplier = supplierRepository.findById(id);

        // Vérification préalable : on lève l'exception avant d'appeler deleteById
        if (optionalSupplier.isEmpty()) {
            throw new SupplierNotFoundException();
        }
        supplierRepository.deleteById(id);
    }

    /**
     * Met à jour un fournisseur existant avec les nouvelles données fournies.
     *
     * <p>Le fournisseur est d'abord récupéré en base via son splId. Si introuvable,
     * une {@link SupplierNotFoundException} est levée immédiatement.</p>
     *
     * <p>Les champs de l'entité sont ensuite mis à jour un par un via les setters
     * avant d'être sauvegardés. Cette approche garantit que seuls les champs
     * explicitement fournis dans le DTO sont modifiés.</p>
     *
     * @param id  l'identifiant du fournisseur à mettre à jour
     * @param dto le DTO contenant les nouvelles valeurs
     * @return le {@link SupplierDTO} du fournisseur après mise à jour
     * @throws SupplierNotFoundException  si aucun fournisseur ne correspond à cet splId
     */
    @Override
    public SupplierDTO modify(Long id, SupplierRequestDTO dto) throws SupplierNotFoundException, IAddressService.AddressNotFoundException {

        // Récupération de l'entité existante — lève SupplierNotFoundException si absente
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(ISupplierService.SupplierNotFoundException::new);

        Address address = addressRepository.findById(dto.addressId())
                .orElseThrow(IAddressService.AddressNotFoundException::new);

        // Mise à jour des champs de l'entité avec les valeurs du DTO
        supplier.setSplName(dto.name());
        supplier.setSplEmail(dto.email());
        supplier.setSplPhone(dto.phoneNumber());
        supplier.setAddress(address);

        return supplierMapper.toDTO(supplierRepository.save(supplier));
    }
}