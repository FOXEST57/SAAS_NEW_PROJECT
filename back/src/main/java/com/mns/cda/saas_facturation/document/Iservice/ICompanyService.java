package com.mns.cda.saas_facturation.document.Iservice;

import com.mns.cda.saas_facturation.config.CompanyProperties;

/**
 * <p>Décrit une seule promesse : savoir donner les informations de
 * l'entreprise (nom, adresse, SIRET…).</p>
 *
 * <p>Cette interface ne dit pas <b>d'où</b> viennent ces informations. Pour
 * l'instant elles sont lues dans {@code application.properties}, mais elles
 * pourraient un jour venir d'une table en base de données, modifiable par
 * l'utilisateur sans avoir à redéployer l'application.</p>
 *
 * <p>C'est tout l'intérêt de passer par une interface : le code qui fabrique
 * les PDF demandera toujours « donne-moi les infos entreprise » sans se
 * soucier de la réponse. Le jour où la source change, on écrit une nouvelle
 * classe et le reste du code n'est pas touché.</p>
 */
public interface ICompanyService {

    /**
     * <p>Renvoie les informations de l'entreprise à afficher sur les
     * documents.</p>
     */
    CompanyProperties getCompany();
}