package com.mns.cda.saas_facturation.document.Iservice;

import com.mns.cda.saas_facturation.exception.DocumentStorageException;
import org.springframework.core.io.Resource;

/**
 * <p>Décrit les trois actions qu'on doit pouvoir faire avec un fichier stocké :
 * l'enregistrer, le relire, et vérifier qu'il existe.</p>
 *
 * <p>C'est juste une liste de promesses ("contrat"). Cette interface ne dit pas
 * <b>comment</b> le fichier est stocké (sur le disque ? dans le cloud ?) — c'est le
 * travail des classes qui l'implémentent, comme {@code LocalFileStorageService}.</p>
 *
 * <p>Avantage : le reste du code appelle toujours les mêmes méthodes, même si on
 * change plus tard la façon dont les fichiers sont réellement stockés.</p>
 */
public interface IDocumentStorageService {

    /**
     * <p>Enregistre le fichier ({@code content}) sous le nom donné ({@code key}).</p>
     * <p>Renvoie le nom utilisé, pour qu'on puisse le garder en base.</p>
     *
     * @throws DocumentStorageException si l'enregistrement échoue
     */
    String store(byte[] content, String key);

    /**
     * <p>Va chercher le fichier enregistré sous ce nom ({@code key}).</p>
     *
     * @throws DocumentStorageException si le fichier est introuvable ou illisible
     */
    Resource retrieve(String key);

    /**
     * <p>Dit si un fichier existe déjà sous ce nom, sans avoir à le lire en entier.</p>
     */
    boolean exists(String key);
}