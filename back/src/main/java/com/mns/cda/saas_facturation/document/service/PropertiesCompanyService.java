package com.mns.cda.saas_facturation.document.service;

import com.mns.cda.saas_facturation.config.CompanyProperties;
import com.mns.cda.saas_facturation.document.Iservice.ICompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <p>Va chercher les informations de l'entreprise dans la configuration
 * ({@code application.properties}).</p>
 *
 * <p>C'est la version la plus simple possible : les valeurs sont fixées au
 * démarrage de l'application et ne changent pas tant qu'on ne redémarre pas.
 * C'est suffisant tant qu'une seule entreprise utilise l'application.</p>
 *
 * <p>Le jour où tu voudras pouvoir modifier ces informations depuis un écran
 * de réglages, il suffira de créer une autre classe qui implémente
 * {@link ICompanyService} en lisant une table en base — sans rien changer
 * ailleurs.</p>
 */
@Service
@RequiredArgsConstructor
public class PropertiesCompanyService implements ICompanyService {

    private final CompanyProperties properties;

    @Override
    public CompanyProperties getCompany() {
        return properties;
    }
}