package com.mns.cda.saas_facturation.unitaire.model;

import com.mns.cda.saas_facturation.TestUtilitaire;
import com.mns.cda.saas_facturation.model.PostalCode;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PostalCodeUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void validPostalCodeWithNameNotBlank_MustBeValidated() {
        PostalCode postalCode = new PostalCode();
        postalCode.setPCodeName("12345");

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(postalCode),
                "pCodeName",
                "NotBlank"
        );

        Assertions.assertFalse(constraintExists);
    }

    @Test
    public void validPostalCodeWithNameNull_MustNotBeValidated() {
        PostalCode postalCode = new PostalCode();
        postalCode.setPCodeName(null);

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(postalCode),
                "pCodeName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validPostalCodeWithNameEmpty_MustNotBeValidated() {
        PostalCode postalCode = new PostalCode();
        postalCode.setPCodeName("");

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(postalCode),
                "pCodeName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validPostalCodeWithNameWithOnlySpace_MustNotBeValidated() {
        PostalCode postalCode = new PostalCode();
        postalCode.setPCodeName(" ");

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(postalCode),
                "pCodeName",
                "NotBlank"
        );

        Assertions.assertTrue(contraintExists);
    }

}
