package com.mns.cda.saas_facturation.unitaire.model;

import com.mns.cda.saas_facturation.TestUtilitaire;
import com.mns.cda.saas_facturation.model.City;
import com.mns.cda.saas_facturation.model.Country;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CityUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // Tests sur cityName (@NotBlank)
    @Test
    public void validCityWithNameNotBlank_MustBeValidated() {
        City city = new City();
        city.setCityName("Nom");

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(city),
                "cityName",
                "NotBlank"
        );

        Assertions.assertFalse(constraintExists);
    }

    @Test
    public void validCityWithNameNull_MustNotBeValidated() {
        City city = new City();
        city.setCityName(null);

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(city),
                "cityName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validCityWithNameEmpty_MustNotBeValidated() {
        City city = new City();
        city.setCityName("");

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(city),
                "cityName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validCityWithNameWithOnlySpace_MustNotBeValidated() {
        City city = new City();
        city.setCityName(" ");

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(city),
                "cityName",
                "NotBlank"
        );

        Assertions.assertTrue(contraintExists);
    }
    
    // Tests sur country (@NotNull)
    @Test
    public void validCityWithCountryNotNull_MustBeValidated() {
        City city = new City();
        city.setCountry(new Country());

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(city),
                "country",
                "NotNull"
        );

        Assertions.assertFalse(contraintExists);
    }

    @Test
    public void validCityWithCountryNull_MustNotBeValidated() {
        City city = new City();
        city.setCountry(null);

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(city),
                "country",
                "NotNull"
        );

        Assertions.assertTrue(contraintExists);
    }

}
