package com.mns.cda.saas_facturation.unitaire.model;

import com.mns.cda.saas_facturation.TestUtilitaire;
import com.mns.cda.saas_facturation.product.model.Tva;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class TvaUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // Tests sur tvaName (@NotBlank)
    @Test
    public void validTvaWithNameNotBlank_MustBeValidated() {
        Tva tva = new Tva();
        tva.setTvaName("Nom");

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(tva),
                "tvaName",
                "NotBlank"
        );

        Assertions.assertFalse(constraintExists);
    }

    @Test
    public void validTvaWithNameNull_MustNotBeValidated() {
        Tva tva = new Tva();
        tva.setTvaName(null);

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(tva),
                "tvaName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validTvaWithNameEmpty_MustNotBeValidated() {
        Tva tva = new Tva();
        tva.setTvaName("");

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(tva),
                "tvaName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validTvaWithNameWithOnlySpace_MustNotBeValidated() {
        Tva tva = new Tva();
        tva.setTvaName(" ");

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(tva),
                "tvaName",
                "NotBlank"
        );

        Assertions.assertTrue(contraintExists);
    }


    // Tests sur tvaTaux (@DecimalMin(value = "0.0"))
    @Test
    public void validTvaWithTauxPositive_MustBeValidated() {
        Tva tva = new Tva();
        tva.setTvaTaux(BigDecimal.valueOf(1.0));

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(tva),
                "tvaTaux",
                "DecimalMin"
        );

        Assertions.assertFalse(contraintExists);
    }

    @Test
    public void validTvaWithTauxNegative_MustNotBeValidated() {
        Tva tva = new Tva();
        tva.setTvaTaux(BigDecimal.valueOf(-5));

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(tva),
                "tvaTaux",
                "DecimalMin"
        );

        Assertions.assertTrue(contraintExists);
    }

}
