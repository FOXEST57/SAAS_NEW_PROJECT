package com.mns.cda.saas_facturation.unitaire.model;

import com.mns.cda.saas_facturation.TestUtilitaire;
import com.mns.cda.saas_facturation.model.Country;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CountryUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // Country cntName : @NotBlank
    @Test
    public void validCountryWithCntNameNull_MustNotBeValidated() {

        Country country = new Country();
        country.setCntName(null);

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(country),
                "cntName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCountryWithCntNameBlankSpace_MustNotBeValidated() {

        Country country = new Country();
        country.setCntName("  ");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(country),
                "cntName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCountryWithCntNameBlank_MustNotBeValidated() {

        Country country = new Country();
        country.setCntName("");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(country),
                "cntName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCountryWithCntNameNotBlank_MustBeValidated() {

        Country country = new Country();
        country.setCntName("France");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(country),
                "cntName",
                "NotBlank"
        );

        Assertions.assertFalse(constraintExist);
    }
}
