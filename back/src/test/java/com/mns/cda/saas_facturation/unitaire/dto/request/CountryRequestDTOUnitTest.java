package com.mns.cda.saas_facturation.unitaire.dto.request;

import com.mns.cda.saas_facturation.TestUtilitaire;
import com.mns.cda.saas_facturation.location.DTO.requestDTO.CountryRequestDTO;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CountryRequestDTOUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // CountryRequestDTO cntName : @NotBlank
    @Test
    public void validCountryWithCntNameNull_MustNotBeValidated() {

        CountryRequestDTO country = new CountryRequestDTO(
                null
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(country),
                "cntName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCountryWithCntNameBlankSpace_MustNotBeValidated() {

        CountryRequestDTO country = new CountryRequestDTO(
                " "
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(country),
                "cntName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCountryWithCntNameBlank_MustNotBeValidated() {

        CountryRequestDTO country = new CountryRequestDTO(
                ""
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(country),
                "cntName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCountryWithCntNameNotBlank_MustBeValidated() {

        CountryRequestDTO country = new CountryRequestDTO(
                "France"
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(country),
                "cntName",
                "NotBlank"
        );

        Assertions.assertFalse(constraintExist);
    }
}
