package com.mns.cda.saas_facturation.unitaire.dto.request;

import com.mns.cda.saas_facturation.TestUtilitaire;
import com.mns.cda.saas_facturation.product.DTO.requestDTO.TvaRequestDTO;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class TvaRequestDTOUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // Tests sur tvaName (@NotBlank)
    @Test
    public void validTvaWithNameNotBlank_MustBeValidated() {
        TvaRequestDTO tvaRequestDTO = new TvaRequestDTO(
                "Nom",
                BigDecimal.valueOf(2)
        );

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(tvaRequestDTO),
                "tvaName",
                "NotBlank"
        );

        Assertions.assertFalse(constraintExists);
    }

    @Test
    public void validTvaWithNameNull_MustNotBeValidated() {
        TvaRequestDTO tvaRequestDTO = new TvaRequestDTO(
                null,
                BigDecimal.valueOf(2)
        );

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(tvaRequestDTO),
                "tvaName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validTvaWithNameEmpty_MustNotBeValidated() {
        TvaRequestDTO tvaRequestDTO = new TvaRequestDTO(
                "",
                BigDecimal.valueOf(2)
        );

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(tvaRequestDTO),
                "tvaName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validTvaWithNameWithOnlySpace_MustNotBeValidated() {
        TvaRequestDTO tvaRequestDTO = new TvaRequestDTO(
                " ",
                BigDecimal.valueOf(2)
        );

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(tvaRequestDTO),
                "tvaName",
                "NotBlank"
        );

        Assertions.assertTrue(contraintExists);
    }


    // Tests sur tvaTaux (@DecimalMin(value = "0.0"))
    @Test
    public void validTvaWithTauxPositive_MustBeValidated() {
        TvaRequestDTO tvaRequestDTO = new TvaRequestDTO(
                "Nom",
                BigDecimal.valueOf(2)
        );

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(tvaRequestDTO),
                "tvaTaux",
                "DecimalMin"
        );

        Assertions.assertFalse(contraintExists);
    }

    @Test
    public void validTvaWithTauxNegative_MustNotBeValidated() {
        TvaRequestDTO tvaRequestDTO = new TvaRequestDTO(
                "Nom",
                BigDecimal.valueOf(-2)
        );

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(tvaRequestDTO),
                "tvaTaux",
                "DecimalMin"
        );

        Assertions.assertTrue(contraintExists);
    }
    
}
