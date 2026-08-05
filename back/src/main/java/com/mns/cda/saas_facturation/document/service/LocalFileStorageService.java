package com.mns.cda.saas_facturation.document.service;

import com.mns.cda.saas_facturation.config.StorageProperties;
import com.mns.cda.saas_facturation.document.Iservice.IDocumentStorageService;
import com.mns.cda.saas_facturation.exception.DocumentStorageException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * <p>Stocke les fichiers sur le disque, dans le dossier défini par
 * {@code app.storage.local-path}.</p>
 *
 * <p>Active par défaut ({@code matchIfMissing = true}) : si {@code app.storage.type}
 * n'est pas défini du tout, c'est cette implémentation qui démarre plutôt qu'une
 * erreur au lancement de l'application.</p>
 */
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.storage", name = "type", havingValue = "local", matchIfMissing = true)
public class LocalFileStorageService implements IDocumentStorageService {

    private final StorageProperties properties;

    @Override
    public String store(byte[] content, String key) {
        Path target = resolve(key);
        try {
            Files.createDirectories(target.getParent());
            Files.write(target, content);
        } catch (IOException e) {
            throw new DocumentStorageException("Impossible d'écrire le fichier : " + key, e);
        }
        return key;
    }

    @Override
    public Resource retrieve(String key) {
        Path target = resolve(key);
        if (!Files.exists(target)) {
            throw new DocumentStorageException("Fichier introuvable : " + key);
        }
        return new FileSystemResource(target);
    }

    @Override
    public boolean exists(String key) {
        return Files.exists(resolve(key));
    }

    /**
     * <p>Transforme la clé logique (ex. {@code "invoices/2026/FAC-2026-0007.pdf"})
     * en emplacement réel sur le disque.</p>
     *
     * <p>Vérifie aussi que le résultat reste bien à l'intérieur du dossier de
     * stockage, pour empêcher une clé du type {@code "../../../etc/passwd"} de
     * sortir de ce dossier.</p>
     */
    private Path resolve(String key) {
        Path base = Paths.get(properties.localPath()).toAbsolutePath().normalize();
        Path target = base.resolve(key).normalize();
        if (!target.startsWith(base)) {
            throw new DocumentStorageException("Clé de stockage invalide : " + key);
        }
        return target;
    }
}