package com.mns.cda.saas_facturation.unitaire.dto.request;

import com.mns.cda.saas_facturation.TestUtilitaire;
import com.mns.cda.saas_facturation.DTO.requestDTO.CityRequestDTO;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CityRequestDTOUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // Tests sur cityName (@NotBlank)
    @Test
    public void validCityWithNameNotBlank_MustBeValidated() {
        CityRequestDTO city = new CityRequestDTO(
                "Nom",
                1L
        );


        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(city),
                "cityName",
                "NotBlank"
        );

        Assertions.assertFalse(constraintExists);
    }

    @Test
    public void validCityWithNameNull_MustNotBeValidated() {
        CityRequestDTO city = new CityRequestDTO(
                null,
                1L
        );

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(city),
                "cityName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validCityWithNameEmpty_MustNotBeValidated() {
        CityRequestDTO city = new CityRequestDTO(
                "",
                1L
        );

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(city),
                "cityName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validCityWithNameWithOnlySpace_MustNotBeValidated() {
        CityRequestDTO city = new CityRequestDTO(
                " ",
                1L
        );

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(city),
                "cityName",
                "NotBlank"
        );

        Assertions.assertTrue(contraintExists);
    }

    // Tests sur countryId (@NotNull)
    @Test
    public void validCityWithCountryNotNull_MustBeValidated() {
        CityRequestDTO city = new CityRequestDTO(
                "Nom",
                1L
        );

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(city),
                "cntId",
                "NotNull"
        );

        Assertions.assertFalse(contraintExists);
    }

    @Test
    public void validCityWithCountryNull_MustNotBeValidated() {
        CityRequestDTO city = new CityRequestDTO(
                "Nom",
                null
        );

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(city),
                "cntId",
                "NotNull"
        );

        Assertions.assertTrue(contraintExists);
    }

}