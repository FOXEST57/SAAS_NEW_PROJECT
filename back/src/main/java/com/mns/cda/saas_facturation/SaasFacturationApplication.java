package com.mns.cda.saas_facturation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
// Détecte et enregistre automatiquement toute classe @ConfigurationProperties du projet
// (ex. StorageProperties) : pas besoin de l'annoter @Component ni de la déclarer une à une.
@ConfigurationPropertiesScan
public class SaasFacturationApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaasFacturationApplication.class, args);
    }

}
