package com.mns.cda.saas_facturation.Service;

import com.mns.cda.saas_facturation.model.Supplier;
import com.mns.cda.saas_facturation.Repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service gérant la logique métier liée aux fournisseurs (Supplier).
 * Implémente ISupplierService et délègue la persistance à SupplierRepository.
 *
 * L'injection de dépendance du repository se fait via le constructeur,
 * généré automatiquement par l'annotation @RequiredArgsConstructor de Lombok.
 */
@Service
@RequiredArgsConstructor
public class SupplierService implements ISupplierService {

    private final SupplierRepository supplierRepository;

    /**
     * Récupère la liste de tous les fournisseurs en base de données.
     *
     * @return Liste de tous les Supplier existants (vide si aucun).
     */
    @Override
    public List<Supplier> findAll() {
        return supplierRepository.findAll();
    }

    /**
     * Recherche un fournisseur par son identifiant.
     *
     * @param id Identifiant du fournisseur à rechercher.
     * @return Un Optional contenant le fournisseur trouvé.
     * @throws SupplierNotFoundException Si aucun fournisseur ne correspond à cet id.
     */
    @Override
    public Optional<Supplier> findById(Long id) throws SupplierNotFoundException {
        Optional<Supplier> optionalSupplier = supplierRepository.findById(id);

        if (optionalSupplier.isEmpty()) {
            throw new SupplierNotFoundException();
        }
        return optionalSupplier;
    }

    /**
     * Crée un nouveau fournisseur en base de données.
     * L'identifiant est forcé à null avant la sauvegarde pour garantir
     * que c'est la base de données qui génère l'id (et non le client).
     *
     * @param supplier Le fournisseur à créer.
     */
    @Override
    public void create(Supplier supplier) throws ExistingSupplierException {

        // On vérifie que le nom du fournisseur n'existe pas en BDD
        if( supplierRepository.existsBySplName(supplier.getSplName())) {
            throw new ExistingSupplierException();
        }

        // On s'assure que l'id est null pour forcer une insertion (INSERT)
        // et éviter qu'un id fourni par le client n'écrase un enregistrement existant.
        supplier.setSplId(null);
        supplierRepository.save(supplier);
    }

    /**
     * Supprime un fournisseur par son identifiant.
     * Vérifie d'abord l'existence du fournisseur avant toute suppression.
     *
     * @param id Identifiant du fournisseur à supprimer.
     * @throws SupplierNotFoundException Si aucun fournisseur ne correspond à cet id.
     */
    @Override
    public void delete(Long id) throws SupplierNotFoundException {
        Optional<Supplier> optionalSupplier = supplierRepository.findById(id);

        if (optionalSupplier.isEmpty()) {
            throw new SupplierNotFoundException();
        }
        supplierRepository.deleteById(id);
    }

    /**
     * Met à jour un fournisseur existant avec les nouvelles données fournies.
     * L'id de l'URL est imposé sur l'objet à sauvegarder pour garantir
     * qu'on modifie bien le bon enregistrement (et non un id éventuellement
     * fourni dans le corps de la requête).
     *
     * @param id              Identifiant du fournisseur à mettre à jour.
     * @param supplierToUpdate Objet contenant les nouvelles valeurs.
     * @throws SupplierNotFoundException Si aucun fournisseur ne correspond à cet id.
     */
    @Override
    public void modify(Long id, Supplier supplierToUpdate) throws SupplierNotFoundException, ExistingSupplierException {

        Optional<Supplier> optionalSupplier = supplierRepository.findById(id);

        // On vérifie l'existance du fournisseur
        if (optionalSupplier.isEmpty()) {
            throw new SupplierNotFoundException();
        }

        // On vérifie que le fournisseur n'existe pas déjà en BDD avec un id différent du fournisseur à modifier
        if (supplierRepository.existsBySplNameAndSplIdIsNot(supplierToUpdate.getSplName()
                , optionalSupplier.get().getSplId() )) {
            throw new ExistingSupplierException();
        }

        // On force l'id de l'objet à mettre à jour avec celui de l'URL
        // pour garantir la cohérence (l'id du body est ignoré).
        supplierToUpdate.setSplId(id);

        supplierRepository.save(supplierToUpdate);
    }
}