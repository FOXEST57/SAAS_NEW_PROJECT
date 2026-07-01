package com.mns.cda.saas_facturation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SaasFacturationApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaasFacturationApplication.class, args);
    }

}
