package com.mns.cda.saas_facturation.unitaire.model;

import com.mns.cda.saas_facturation.TestUtilitaire;
import com.mns.cda.saas_facturation.model.Maker;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MakerUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // Maker mkrName : @NotBlank
    @Test
    public void validMakerWithMkrNameNull_MustNotBeValidated() {

        Maker maker = new Maker();
        maker.setMkrName(null);

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(maker),
                "mkrName",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validMakerWithMkrNameNotNull_MustNotBeValidated() {

        Maker maker = new Maker();
        maker.setMkrName("Lenovo");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(maker),
                "mkrName",
                "NotBlank"
        );
        Assertions.assertFalse(constraintExist);
    }
}
