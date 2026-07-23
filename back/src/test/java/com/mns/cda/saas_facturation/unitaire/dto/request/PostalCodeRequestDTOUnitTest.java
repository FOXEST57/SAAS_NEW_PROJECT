package com.mns.cda.saas_facturation.unitaire.dto.request;

import com.mns.cda.saas_facturation.TestUtilitaire;
import com.mns.cda.saas_facturation.DTO.requestDTO.PostalCodeRequestDTO;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PostalCodeRequestDTOUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // Tests sur pCodeName (@NotBlank)
    @Test
    public void validPostalCodeWithNameNotBlank_MustBeValidated() {
        PostalCodeRequestDTO postalCodeRequestDTO = new PostalCodeRequestDTO(
                "12345"
        );

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(postalCodeRequestDTO),
                "pCodeName",
                "NotBlank"
        );

        Assertions.assertFalse(constraintExists);
    }

    @Test
    public void validPostalCodeWithNameNull_MustNotBeValidated() {
        PostalCodeRequestDTO postalCodeRequestDTO = new PostalCodeRequestDTO(
                null
        );

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(postalCodeRequestDTO),
                "pCodeName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validPostalCodeWithNameEmpty_MustNotBeValidated() {
        PostalCodeRequestDTO postalCodeRequestDTO = new PostalCodeRequestDTO(
                ""
        );

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(postalCodeRequestDTO),
                "pCodeName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validPostalCodeWithNameWithOnlySpace_MustNotBeValidated() {
        PostalCodeRequestDTO postalCodeRequestDTO = new PostalCodeRequestDTO(
                " "
        );

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(postalCodeRequestDTO),
                "pCodeName",
                "NotBlank"
        );

        Assertions.assertTrue(contraintExists);
    }

}
