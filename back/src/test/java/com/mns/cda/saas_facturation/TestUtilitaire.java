package com.mns.cda.saas_facturation;

import jakarta.validation.ConstraintViolation;

import java.util.Set;

public class TestUtilitaire {

    // Fonction pour vérifier les accesseurs dans model.
    public static boolean constraintViolationExist(
            Set<ConstraintViolation<Object>> constraintViolations,
            String fieldName,
            String annotationName) {
        return constraintViolations.stream()
                .anyMatch(contrainte -> {
                    String champs = contrainte.getPropertyPath().toString();
                    String erreur = contrainte
                            .getConstraintDescriptor()
                            .getAnnotation()
                            .annotationType()
                            .getSimpleName();

                    return champs.equals(fieldName) && erreur.equals(annotationName);
                });
    }
}